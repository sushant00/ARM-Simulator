package application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Scanner;
/**
 * 
 * @author Adarsh Kumar Choudhary, Pratyush Kaushal, Sushant Kumar Singh
 *
 */

public class ARMSim {
	// variables for gui
	GUIController gui;			//Controller of GUI
	PrintWriter output;			//write the outputs to this file	
	boolean runOnGUI;			//program is run from GUI or not

	
	// Main Memory
	long[] Mem;						// size is 4KB

	// ARM Registers
	long[] R;						// R0-R13 is general purpose, R13 is sp, R14 is lr, R15 is pc
	
	// Flags, value is either 0 or 1
	int N, Z, C, V;     			// IRQ, FIQ, S1, S0 not used currently
	
	// intermediate datapath and control path signals
	long instruction;
	int Cond, F, I, OpCode, S, Rn, Rd, Operand2; //decoded instruction
	
	
	long Operand1Val, Operand2Val;	//Operand1Val has value at Rn, Operand2Val has value as found from Operand2
	long result; 					// result to be stored in destination register
	
	int pc;							// Program Counter
	int P, U, B, W;					// Pre/post index, Up/down, Byte/Word, Writeback bit
	int L;							// Branch Link Bit or Load(1)/Store(0) bit
	
	/**
	 * Constructor for ARMSim, this initialises the output.txt file to be written in with outputs of simulator
	 */
	public ARMSim(){
		 try {
			output=new PrintWriter(new BufferedWriter(new FileWriter("output.txt")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * This method returns the actual value represented by the immediate field
	 * @param operand These are the immediate 12 bits 
	 * @return actual value denoted by the immediate operand
	 */
	private long getImmediateValue(int operand){
		String operand2 = Integer.toBinaryString(operand);
		operand2 = String.join("", Collections.nCopies((12-operand2.length()) , "0")) + operand2; // add 0s to make 12 bit
		
		String number = operand2.substring(4);
		number = "000000000000000000000000"+number;		// pad with 24 0s
		int rotation = Integer.parseInt(operand2.substring(0, 4), 2);
		
		int index = (32 - rotation*2)%32;
		String actualNumber = "";
		while(actualNumber.length()!=32){
			actualNumber+=number.charAt(index);
			index = (index+1)%32;
		}
		
		return Long.parseLong(actualNumber, 2);
	}



	/**
	 * This function initialises all variables to start
	 */
	public void init(){
		R= new long[16];
		Mem= new long[4000];		// Memory is 4KB
		N=0;
		V=0;
		Z=0;
		C=0;
		pc=0;
		
		instruction = 0;
		
		// initialize utility variable 
		Cond=0;
		F=0;
		I=0;
		OpCode=0;
		S=0;
		Rn=0;
		Rd=0;
		Operand2=0;
		
		Operand1Val=0;
		Operand2Val=0;
		result=0;
		
		P=0;
		U=0;
		B=0;
		W=0;
		L=0;
		
		R[13]=3996; //INITIALIZE STACK POINTER AS 1000
	}
	
	
	/**
	 * this function halts the simulation/ execution
	 */
	public void swi_exit() throws SwiExitException{
		writeMem();		// write the data memory
		System.out.println("EXIT");
		output.println("EXIT");
		throw new SwiExitException("exiting execution");
		//System.exit(0);
	}
	
	
	/**
	 * This function loads the Mem with instructions from the Mem file
	 */
	public void loadMem(String filename){
		Scanner in =null;
		try
		{
			in= new Scanner(new BufferedReader(new FileReader(filename)));
			while(in.hasNext()){
				String address=(String)in.next();
				address=address.substring(2);
				int index=Integer.parseInt(address,16);
				String instruction=(String)in.next();
				instruction=instruction.substring(2);
				long valueOfInstruction= Long.parseLong(instruction,16);
				Mem[index]=valueOfInstruction;
			}
		}
		catch(IOException e)
		{
			// write error opening file
			e.printStackTrace();
			
		}
		finally
		{
			if(in!=null)
				in.close();
		}
	}
	
	/**
	 * This method prints the values as stored in data memory to an "output.mem" file
	 */
	public void writeMem(){
		PrintWriter out=null;
		try
		{
			out=new PrintWriter(new FileWriter("output.mem"));
			for(int i=0;i<4000;i+=4)
			{
				out.println(Integer.toHexString(i)+" " + Long.toHexString(Mem[i]));
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(out!=null)
				out.close();
		}
	}
	

	/**
	 * This method fetches the instruction from memory and increments the program counter
	 */
	public void fetch(){
		instruction= Mem[pc];
		System.out.println("Fetch instruction 0x" + Long.toHexString(instruction)+ " from address 0x" + Long.toHexString(pc));
		output.println("Fetch instruction 0x" + Long.toHexString(instruction)+ " from address 0x" + Long.toHexString(pc));
		pc+=4;
		R[15]+=4;
	}
	
	/**
	 * This method decodes the instruction word
	 */
	public void decode() throws SwiExitException{		
		String binary = Long.toBinaryString(instruction);
		binary = String.join("",Collections.nCopies(32-binary.length(), "0")) + binary; // add 0s to make 32 bit

		
		// get the required bits and store value in control and decode variables 
		Cond = Integer.parseInt(binary.substring(0, 4), 2); 
		F = Integer.parseInt(binary.substring(4, 6), 2);
		I = Integer.parseInt(binary.substring(6, 7), 2);
		OpCode = Integer.parseInt(binary.substring(7, 11), 2);
		S = Integer.parseInt(binary.substring(11, 12), 2);
		Rn = Integer.parseInt(binary.substring(12, 16), 2);
		Rd = Integer.parseInt(binary.substring(16, 20), 2);
		Operand2 = Integer.parseInt(binary.substring(20), 2);
		//TODO only if condition is true ???
		
		// F = 3 means Software Interrupt
		if (F==3){
			if(Cond==14 && binary.substring(4,8).equals("1111")){
				System.out.print("DECODE: Software Interrupt SWI_");
				output.print("DECODE: Software Interrupt SWI_");
				int interrupt = Integer.parseInt(binary.substring(24), 2);
				switch(interrupt){
					// swi 0x00
					case 0:System.out.println("PrChr");
							output.println("PrChr");
							if(runOnGUI){
								gui.consoleOutput.setText(gui.consoleOutput.getText()+(char)R[0]+"\n");
							}else{
								System.out.println((char)R[0]);								
							}
							break;
					// swi 0x11
					case 17:System.out.println("Exit");
							output.println("Exit");
							swi_exit();
							break;
					// swi 0x6b
					case 107:System.out.println("PrInt");
							output.print("PrInt");
						if(R[0]==1){
							if(runOnGUI){
								gui.consoleOutput.setText(gui.consoleOutput.getText()+R[1]+"\n");
							}else{
								System.out.println(R[1]);							
							}
						}
							break;
					// swi 0x6c
					case 108:System.out.println("RdInt");
							output.print("RdInt");
						if(R[0]==0){
							if(runOnGUI){
								R[0] = Integer.parseInt(gui.consoleInput.getText());
							}else{
								Scanner sc = new Scanner(System.in);
								R[0] = sc.nextInt();
								sc.close();						
							}
						}
							break;
				}
			}
			
		}else{
			System.out.print("DECODE: Operation is ");
			output.print("DECODE: Operation is");
			// F = 0 means Data Processing instructions
			if(F==0 && Cond==14){
				switch(OpCode){
					case 0: System.out.print("AND");
							output.print("AND");
							break;
					case 1: System.out.print("EOR");
							output.print("EOR");
							break;
					case 2: System.out.print("SUB");
							output.print("SUB");
							break;
					case 4: System.out.print("ADD");
							output.print("ADD");
							break;
					case 5: System.out.print("ADC");
							output.print("ADC");		
							break;	
					case 10: System.out.print("CMP");
							output.print("CMP");		
							break;	
					case 12: System.out.print("ORR");
							output.print("ORR");		
							break;	
					case 13: System.out.print("MOV");
							output.print("MOV");		
							break;	
					case 15: System.out.print("MVN");
							output.print("MNV");		
							break;							
				}
				//TODO why check Rn == 0/ not, initialise operand1 if necessary
				System.out.print(", First Operand is R"+Rn);
				output.print(", First Operand is R"+Rn);
				if(I==0){ // I=0 means Operand2 is Register
					System.out.println(", Second Operand is R"+Operand2);
					output.println(", Second Operand is R"+Operand2);
				}else{	// I = 1 means Operand2 is Immediate value
					Operand2Val = getImmediateValue(Operand2);
					System.out.println(", immediate Second Operand is "+Operand2Val+",");
					output.println(", immediate Second Operand is "+Operand2Val+",");
				}
				
				System.out.println("Destination Register is R"+Rd+".");
				output.println("Destination Register is R"+Rd+".");
				// Reading register
				System.out.print("Read Registers: ");
				output.print("Read Registers: ");
				
				// Rn i.e. operand1 is read
				Operand1Val = R[Rn];
				System.out.print("R"+Rn+" = "+R[Rn]);
				output.print("R"+Rn+" = "+R[Rn]);
				// Operand2 is read if I=0
				if(I==0){
					Operand2Val = R[Operand2];
					System.out.print(", R"+Operand2+" = "+R[Operand2]);
					output.print(", R"+Operand2+" = "+R[Operand2]);
				}
				System.out.println();
				output.println();
			}
			
			// F = 1 means Data Transfer instructions
			else if(F==1 && Cond==14){

				P = Integer.parseInt(binary.substring(7,8), 2);
				U = Integer.parseInt(binary.substring(8,9), 2);
				B = Integer.parseInt(binary.substring(9,10), 2);
				W = Integer.parseInt(binary.substring(10,11), 2);
				L = Integer.parseInt(binary.substring(11,12), 2);
				
				if(B==0){
					switch(L){
					case 0: System.out.print("STR");
							output.print("STR");
							break;
					case 1: System.out.print("LDR");
							output.print("LDR");
							break;
					}
				}else{					
					switch(L){
					case 0: System.out.print("STRB");
							output.print("STRB");
							break;
					case 1: System.out.print("LDRB");
							output.print("LDRB");
							break;
					}
				}
				System.out.print(", Base Register is R"+Rn);
				output.print(", Base Register is R"+Rn);
				System.out.print(", Destination Register is R"+Rd);
				output.print(", Destination Register is R"+Rd);
				
				if(I==0){	//I = 0 means offset is immediate value
					Operand2Val = getImmediateValue(Operand2);
					System.out.println(", Offset is "+Operand2Val);
					output.println(", Offset is "+Operand2Val);
				}
				else{		//I = 1 means offset is register
					System.out.println(", Offset is R"+Operand2);
					output.println(", Offset is R"+Operand2);
				}
				
				// Reading register
				System.out.print("Read Registers: ");
				output.print("Read Registers: ");
				
				// Rn i.e. operand1 is read
				Operand1Val = R[Rn];
				System.out.print("R"+Rn+" = "+R[Rn]);
				output.print("R"+Rn+" = "+R[Rn]);
				
				// Operand2 is read if I=1
				if(I==1){
					Operand2Val = R[Operand2];
					System.out.print(", R"+Operand2+" = "+R[Operand2]);
					output.print(", R"+Operand2+" = "+R[Operand2]);
				}
				System.out.println();
				output.println();
			}
			//TODO where to hadle branch? execute or here
			// F = 2 means Branch instructions
			else if(F==2){
				L = Integer.parseInt(binary.substring(7,8));
				if (L==0){	//	L = 0 means branch
					System.out.print("B");
					output.print("B");
				}
				else{		// L = 1 means branch with link
					System.out.print("BL");
					output.print("BL");
				}
				
				switch(Cond){	
					case 0: System.out.print("EQ");//equal
							output.print("EQ");
							break;
					case 1: System.out.print("NE");//not equal
							output.print("NE");
							break;
					case 10: System.out.print("GE");//greater than or equal
							output.print("GE");		
							break;
					case 11: System.out.print("LT");// less than
							output.print("LT");		
							break;
					case 12: System.out.print("GT");// greater than
							output.print("GT");		
							break;	
					case 13: System.out.print("LE");// less than or equal
							output.print("LE");		
							break;	
					case 14: System.out.print("AL");//always
							output.print("AL");		
							break;	
				}
				//TODO operand val is 2s comp, pc + sign extend(shift two left)
				Operand2Val = Integer.parseInt(binary.substring(8), 2);
				System.out.println(", Offset is "+Operand2Val);	
				output.println(", Offset is "+Operand2Val);
			}
		}
		
	}
	
	/**
	 * Execute() : that will tell what we going to execute
	 * @return : void
	 * 
	 */
	
	public void execute(){
		System.out.print("EXECUTE: ");
		output.print("EXECUTE: ");
		if(F==0){
			switch(OpCode){
			case 0:
				System.out.println("AND"+" "+Operand1Val+" and "+Operand2Val);
				output.println("AND"+" "+Operand1Val+" and "+Operand2Val);
				result=Operand1Val&Operand2Val;
				
				break;
			case 2:
				
				System.out.println("SUB"+" "+Operand1Val+" and "+Operand2Val);
				output.println("SUB"+" "+Operand1Val+" and "+Operand2Val);
				result=Operand1Val-Operand2Val;
				
				break;
			case 4:
				System.out.println("ADD"+" "+Operand1Val+" and "+Operand2Val);
				output.println("ADD"+" "+Operand1Val+" and "+Operand2Val);
				result=Operand1Val+Operand2Val;
				
				break;
			case 10:
				Z=0;N=0;
				System.out.println("CMP"+" "+Operand1Val+" and "+Operand2Val);
				output.println("CMP"+" "+Operand1Val+" and "+Operand2Val);
				if(Operand1Val-Operand2Val==0){
			    	
			    	Z=Z+1;
			    	System.out.println("Z updated");
			    	output.println("Z updated");
			    }
			    else if(Operand1Val-Operand2Val<1){
			    	N=N+1;
			    	System.out.println("N updated");
			    	output.println("N updated");
			    }
			    
				break;
			case 12:
				
				System.out.println("OR"+" "+Operand1Val+" and "+Operand2Val);
				output.println("OR"+" "+Operand1Val+" and "+Operand2Val);
		
				result=Operand1Val | Operand2Val;
				
				break;
			case 13:
				System.out.println("MOV "+Operand2Val+" in R"+Rd);
				output.println("MOV "+Operand2Val+" in R"+Rd);
				
				result=Operand2Val;
				break;
			case 15:
				
				System.out.println("MVN"+" of "+Operand2Val+" ( MOV ~ operandvalue)");
				output.println("MVN"+" of "+Operand2Val+" ( MOV ~ operandvalue)");
			//	
				result=~Operand2Val;
				
				break;
			}
			}
		else if(F==2){
			 boolean branching=false;
			 System.out.print("B");
			 output.print("B");
			switch(Cond){
			case 0: System.out.print("EQ ");//equal
					output.print("EQ ");
					if(Z==1){
						branching=true;
					}
					break;
			case 1: System.out.print("NE ");//not equal
					output.print("NE ");
					if(Z==0){
						branching=true;
					}
					break;
			case 10: System.out.print("GE ");//greater than or equal
					output.print("GE ");		
					if(N==0 || Z==1){
						branching=true;
					}
					break;
			case 11: System.out.print("LT ");// less than
					output.print("LT ");		
					if(N==1){
						branching=true;
					}
					break;
			case 12: System.out.print("GT ");// greater than
					output.print("GT ");		
					if(N==0){
						branching=true;
					}
					break;	
			case 13: System.out.print("LE ");// less than or equal
					output.print("LE ");		
					if(N==1 || Z==1){
						branching=true;
					}
					break;	
			case 14: System.out.print("AL ");//always
					output.print("AL ");		
						branching = true;
					break;	
					}
			if(branching){
				String offsetBinary = Long.toBinaryString(Operand2Val);
				offsetBinary = String.join("",Collections.nCopies(6, String.valueOf(offsetBinary.charAt(0)) )) + offsetBinary + "00";
				long offset = Long.parseLong(offsetBinary, 2);
								
				if(L==0){	//Branch
					pc+=offset;
					pc+=4;
					R[15]=pc;
				}else{		//Branch with link
					R[14] = pc;
					pc+=offset;
					pc+=4;
					R[15] = pc;
				}
				System.out.println("Program Counter changed to "+pc);
				output.println("Program Counter changed to "+pc);
			}else{
				System.out.println("No execute operation");
				output.println("No execute operation");
			}
			
		}
		else{
			System.out.println("No execute operation");
			output.println("No execute operation");
		}
	}



	/**
	 * this method performs read and write to memory if required in current instruction
	 */
	public void memory(){
		System.out.print("MEMORY: ");
		output.print("MEMORY: ");
		if(F==1){
			long finalAddress = 0;
			if(B==0){//WORD BASED
				if(P==0){//post
					finalAddress = Operand1Val;
					if(W==1){	// writeback to register
						if(U==0){//Down
							R[Rn] -= Operand2Val;
						}else{//Up
							R[Rn] += Operand2Val;							
						}
					}
				}else{//pre
					if(W==1){
						if(U==0){//Down
							R[Rn] -= Operand2Val;
						}else{//Up
							R[Rn] += Operand2Val;
						}
						finalAddress = R[Rn];
					}else{	//no writeback to register
						if(U==0){//Down
							finalAddress = R[Rn] - Operand2Val;
						}else{//Up
							finalAddress = R[Rn] + Operand2Val;
						}
					}						
				}
			}else{//BYTE BASED
				if(P==0){//post
					finalAddress = Operand1Val;
					if(W==1){
						if(U==0){//Down
							R[Rn] -= Operand2Val;
						}else{//Up
							R[Rn] += Operand2Val;							
						}
					}
				}else{//pre
					if(U==0){//Down
						R[Rn] -= Operand2Val;
					}else{//Up
						R[Rn] += Operand2Val;
					}
					finalAddress = R[Rn];						
				}
			}
			switch(L){
			case 0: Mem[(int)finalAddress] = R[Rd];
					System.out.println("Write "+R[Rd]+" to address 0x"+Long.toHexString(finalAddress));
					output.println("Write "+R[Rd]+" to address 0x"+Long.toHexString(finalAddress));
					break;
			case 1: 
					result = Mem[(int)finalAddress];	//read from memory
					System.out.println("Read "+result+" from address 0x"+Long.toHexString(finalAddress));
					output.println("Read "+result+" from address 0x"+Long.toHexString(finalAddress));
					break;
			}			
		}
		else{// F=0,2,3
			System.out.println("No memory operation");
			output.println("No memory operation");
		}
	}



	public void writeBack(){
		System.out.print("WRITEBACK: ");
		output.print("WRITEBACK: ");
		if(F==1){
			switch(L){
			case 0:
				System.out.println("No WriteBack");
				output.println("No WriteBack");
				break;
			case 1:	
				R[Rd]=result;
				System.out.println("write "+result+" to R"+Rd);
				output.println("write "+result+" to R"+Rd);
				break;
			}
		}
		else if(F==0){
			R[Rd]=result;
			System.out.println("write "+result+" to R"+Rd);
			output.println("write "+result+" to R"+Rd);
			
		}
		else {
			System.out.println("No WriteBack");
			output.println("No WriteBack");
		}
		
	}



	public void simulate() throws SwiExitException{
		while(true)
		{
			fetch();
			decode();
			execute();
			memory();
			writeBack();			
			System.out.println();System.out.println();
			output.println();output.println();
		}
	}	
	
	public void nextStep() throws SwiExitException{
		fetch();
		decode();
		execute();
		memory();
		writeBack();
		output.println();output.println();
	}
	
	public void runARMSim(){
		try{
			init(); 			// reset the processor
			loadMem("input.mem");			// read the input file and prepare the memory
			simulate();			// start executing (simulating)
		}catch(SwiExitException e){
			return;
		}
	}
	
	public String getRegisterStatus(){
		String regStatus = "";
		for(int i = 0; i < 10; i++){
			regStatus = regStatus + "R"+i+"    : "+Long.toHexString(R[i]) + "\n";
		}
		for(int i = 10; i < 16; i++){
			regStatus = regStatus + "R"+i+"  : "+Long.toHexString(R[i]) + "\n";
		}
		regStatus+="---------\n";
		regStatus+="CPSR Registers\n\n";
		regStatus+="Negative(N)    "+N+"\n";
		regStatus+="Zero(Z)    "+N+"\n";
		regStatus+="Carry(C)    "+N+" \n";
		regStatus+="Overflow(V)    "+N+" \n";
		
		return regStatus;
	}



	public String getMemoryStatus(){
		String regStatus = "";
		for(int i=0;i<4000;i+=4)
		{
			regStatus+=(Integer.toHexString(i)+"    " + Long.toHexString(Mem[i])+"\n");
		}
		return regStatus;
	}



	public static void main(String[] args){
		ARMSim instance = new ARMSim();
		instance.runOnGUI = false;
		instance.runARMSim();
	}
		
}

