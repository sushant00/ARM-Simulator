package application;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Scanner;
/**
 * 
 * @author Adarsh Kumar Choudhary, Pratyush Kaushal, Sushant Kumar Singh
 *
 */

//TODO error print, write data, comment fetch, initialise stack pointer, add stacck pointer in r13
// pg 4-11 setting flags
public class ARMSim {
	
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
	int L;							// Branch and link if l=1 else branch
	
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
	}
	
	
	
	private void swi_exit(){
		writeMem();		// write the data memory
		System.exit(0);
	}
	
	
	/**
	 * This function loads the Mem with instructions from the Mem file
	 */
	public void loadMem(){
		Scanner in =null;
		try
		{
			in= new Scanner(new BufferedReader(new FileReader("input.mem")));
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
		
	}
	
	
	
	public void simulate(){
		while(true)
		{
			fetch();
//			decode();
//			execute();
//			memory();
//			writeBack();
		}
	}	
	/**
	 * This method fetches the instruction from memory and increments the program counter
	 */
	public void fetch(){
		instruction= Mem[pc];
		System.out.println("Fetch instruction 0x" + Long.toHexString(instruction)+ " from address 0x" + Long.toHexString(pc));;
		pc+=4;
		R[15]+=4;
	}
	
	/**
	 * This method decodes the instruction word
	 */
	public void decode(){
		String binary = Long.toBinaryString(instruction);
		binary = String.join(""+Collections.nCopies(32-binary.length(), "0")); // add 0s to make 32 bit
		
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
				int interrupt = Integer.parseInt(binary.substring(24), 2);
				switch(interrupt){
					// swi 0x00
					case 0:System.out.println("PrChr");
							System.out.println((char)R[0]);
							break;
					// swi 0x11
					case 17:System.out.println("Exit");
							swi_exit();
							break;
					// swi 0x6b
					case 107:System.out.println("PrInt");
						if(R[0]==1){
							System.out.println(R[1]);
						}
							break;
					// swi 0x6c
					case 108:System.out.println("RdInt");
						if(R[0]==0){
							Scanner sc = new Scanner(System.in);
							R[0] = sc.nextInt();
						}
							break;
				}
			}
			
		}else{
			System.out.print("DECODE: Operation is ");

			switch(OpCode){
				case 0: System.out.println("AND");
						break;
				case 1: System.out.println("EOR");
						break;
				case 2: System.out.println("SUB");
						break;
				case 4: System.out.println("ADD");
						break;
				case 5: System.out.println("ADC");
						break;	
				case 10: System.out.println("CMP");
						break;	
				case 12: System.out.println("ORR");
						break;	
				case 13: System.out.println("MOV");
						break;	
				case 15: System.out.println("MVN");
						break;							
			}
			// F = 0 means Data Processing instructions
			if(F==0){
				switch(OpCode){
					case 0: System.out.println("AND");
							break;
					case 1: System.out.println("EOR");
							break;
					case 2: System.out.println("SUB");
							break;
					case 4: System.out.println("ADD");
							break;
					case 5: System.out.println("ADC");
							break;	
					case 10: System.out.println("CMP");
							break;	
					case 12: System.out.println("ORR");
							break;	
					case 13: System.out.println("MOV");
							break;	
					case 15: System.out.println("MVN");
							break;							
				}
				//TODO why check Rn == 0/ not, initialise operand1 if necessary
				System.out.print(", First Operand is R"+Rn);
				if(I==0){ // I=0 means Operand2 is Register
					System.out.println(", Second Operand is R"+Operand2);
				}else{	// I = 1 means Operand2 is Immediate value
					Operand2Val = getImmediateValue(Operand2);
					System.out.println(", immediate Second Operand is "+Operand2Val+",");					
				}
				
				System.out.println("Destination Register is R"+Rd+".");
				
				// Reading register
				System.out.print("Read Registers: ");
				
				// Rn i.e. operand1 is read
				Operand1Val = R[Rn];
				System.out.print("R"+Rn+" = "+R[Rn]);
				
				// Operand2 is read if I=0
				if(I==0){
					Operand2Val = R[Operand2];
					System.out.print(", R"+Operand2+" = "+R[Operand2]);
				}
				System.out.println();				
			}
			
			// F = 1 means Data Transfer instructions
			else if(F==1){
				switch(OpCode){
				case 24: System.out.println("STR");
						break;
				case 25: System.out.println("LDR");
						break;
				}
				
				System.out.print(", Base Register is R"+Rn);
				System.out.print(", Destination Register is R"+Rd);
				
				if(I==0){	//I = 0 means offset is immediate value
					Operand2Val = getImmediateValue(Operand2);
					System.out.println(", Offset is "+Operand2Val);
				}
				else{		//I = 1 means offset is register
					System.out.println(", Offset is R"+Operand2);					
				}
				
				// Reading register
				System.out.print("Read Registers: ");
				
				// Rn i.e. operand1 is read
				Operand1Val = R[Rn];
				System.out.print("R"+Rn+" = "+R[Rn]);
				
				// Operand2 is read if I=1
				if(I==1){
					Operand2Val = R[Operand2];
					System.out.print(", R"+Operand2+" = "+R[Operand2]);
				}
				System.out.println();				
			}
			//TODO where to hadle branch? execute or here
			// F = 2 means Branch instructions
			else if(F==2){
				L = Integer.parseInt(binary.substring(7,8));
				if (L==0){	//	L = 0 means branch
					System.out.print("B");
				}
				else{		// L = 1 means branch with link
					System.out.print("BL");
				}
				
				switch(Cond){	
					case 0: System.out.print("EQ");//equal
							break;
					case 1: System.out.print("NE");//not equal
							break;
					case 10: System.out.print("GE");//greater than or equal
							break;
					case 11: System.out.print("LT");// less than
							break;
					case 12: System.out.print("GT");// greater than
							break;	
					case 13: System.out.println("LE");// less than or equal
							break;	
					case 14: System.out.println("AL");//always
							break;	
				}
				//TODO operand val is 2s comp, pc + sign extend(shift two left)
				Operand2Val = Integer.parseInt(binary.substring(8));
				System.out.println(", Offset is "+Operand2Val);				
			}
		}
		
	}
	
	/**
	 * This method returns the actual value represented by the immediate field
	 * @param operand These are the immediate 12 bits 
	 * @return actual value denoted by the immediate operand
	 */
	private long getImmediateValue(int operand){
		String operand2 = Integer.toBinaryString(operand);
		operand2 = String.join(""+Collections.nCopies(12-operand2.length(), "0")); // add 0s to make 12 bit
		
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
	 * this method performs read and write to memory if required in current instruction
	 */
	public void memory(){
		System.out.print("MEMORY: ");
		if(F==1){
			long finalAddress = Operand1Val + Operand2Val;		// address at base register + offset
			switch(OpCode){
			case 24: Mem[(int)finalAddress] = R[Rd];
					System.out.println("Write "+R[Rd]+" to address 0x"+Long.toHexString(finalAddress));
					break;
			case 25: 
					result = Mem[(int)finalAddress];	//read from memory
					System.out.println("Read "+result+" from address 0x"+Long.toHexString(finalAddress));
					break;
			}			
		}
		else{// F=0,2,3
			System.out.println("No memory operation");
		}
	}
	
	public void runARMSim(){
		init(); 			// reset the processor
		loadMem();			// read the input file and prepare the memory
		simulate();			// start executing (simulating)
	}
	
	public static void main(String[] args){
		ARMSim instance = new ARMSim();
		instance.runARMSim();
	}
	
}
