package application;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class ARMSim {
	
	// Main Memory
	long[] Mem;						// size is 20KB

	// ARM Registers
	long[] R;						// R0-R13 is general purpose, R13 is sp, R14 is lr, R15 is pc
	
	// Flags, value is either 0 or 1
	int N, Z, C, V;     			// IRQ, FIQ, S1, S0 not used currently
	
	// Program Counter
	int pc;
	
	long instruction;
	// Utility variables - assigned during decoding
	int Cond, F, I, OpCode, S, Rn, Rd, Operand2;
	
	
	
	/**
	 * This function initialises all variables to start
	 */
	public void init(){
		R= new long[16];
		Mem= new long[20000];// Memory is 20KB
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
			e.printStackTrace();
			
		}
		finally
		{
			if(in!=null)
				in.close();
		}
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
	
	public void fetch(){
		instruction= Mem[pc];
		System.out.println("Fetch instruction 0x" + Long.toHexString(instruction)+ " from address 0x" + Long.toHexString(pc));;
		pc+=4;
		R[15]+=4;
	}
	
	public void runARMSim(){
		init(); 			// this assigns all variables to 0
		loadMem();			// read the input file and prepare the memory
		simulate();			// start executing (simulating)
	}
	
	public static void main(String[] args){
		ARMSim instance = new ARMSim();
		instance.runARMSim();
	}
	
}
