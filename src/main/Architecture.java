package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import javafx.scene.control.TextArea;

public class Architecture {
	DataMemory data;
	private static InstructionMemory instructionsMemory;
	static DataMemory dataMemory;
	static RegisterFile registerFile;
	static PC pc;
	static int clock;
	public static boolean branch;
	static ArrayList <String> instructions;
	
	static PrintStream ogRegOutputStream;
	static PrintStream ogInsOutputStream;
	static PrintStream ogDataOutputStream;
	static PrintStream ogOutputStream;
	static TextArea outputTextReg;
	static TextArea outputTextIns;
	static TextArea outputTextData;
	static TextArea outputText;

public Architecture() {
		
		this.dataMemory = new DataMemory();
		this.instructionsMemory = new InstructionMemory();
		this.registerFile = new RegisterFile();
		this.clock =1;	
		this.pc = new PC();
		this.pc.data = 0;
		this.instructions = new ArrayList<String>();
		
	}
	public static void storeInMemory() {
		 ArrayList<String> assemblyLines = new ArrayList<>();
		 int i =0;

	        try (BufferedReader br = new BufferedReader(new FileReader("src/files/Assembly.txt"))) {
	            String line;
	            while ((line = br.readLine()) != null) {
	                try {
	                	instructions.add(line);
						instructionsMemory.instructionMemory[i]= decoding(line);
						instructionsMemory.count++;
					} catch (Exception e) {
						
						e.printStackTrace();
					}
	                i++;
	            }

	        } catch (IOException e) {
	            System.err.format("IOException: %s%n", e);
	        }


	        for (String assemblyLine : assemblyLines) {

	        }
		
	}
	
	public static short fetch() {
		return instructionsMemory.instructionMemory[pc.data];
	}
	
	
	public static short decoding(String ins) throws Exception {
		String [] assemblyArray = ins.split(" ");
		

		String opcode = assemblyArray[0];

		String arg1 = assemblyArray[1];

		String arg2 = assemblyArray[2];
	

		String result = "";
		 
		switch(opcode) {
		case "ADD": result = "0000"; break;
		case "SUB":result = "0001"; break;
		case "MUL":result = "0010"; break;
		case "MOVI":result = "0011"; break;
		case "BEQZ":result = "0100"; break;
		case "ANDI":result = "0101"; break;
		case "EOR":result = "0110"; break;
		case "BR":result = "0111"; break;
		case "SAL":result = "1000"; break;
		case "SAR":result = "1001"; break;
		case "LDR":result = "1010"; break;
		case "STR":result = "1011"; break;
		default: throw new Exception("Undefined Instruction!");
		}
		
		String index = arg1.substring(1);
		System.out.println("index: " + index);
		Byte arg1B = Byte.parseByte(index);
		String binary = String.format("%8s", Integer.toBinaryString(arg1B & 0b111111)).replace(' ', '0');
		binary = binary.substring(2);
		result += binary;
		
		if(arg2.contains("R")) {
			index = arg2.substring(1);
			Byte arg2B = Byte.parseByte(index);
			binary = String.format("%8s", Integer.toBinaryString(arg2B & 0b111111)).replace(' ', '0');
			binary = binary.substring(2);
			result += binary;
		}
		else {
			Byte immediate = Byte.parseByte(arg2);
	 
			binary = String.format("%8s", Integer.toBinaryString(immediate & 0b111111)).replace(' ', '0');
			binary = binary.substring(2);
			result += binary;
		}
		System.out.println("Result: " + result);
		short res = getTwosComplement(result);

		return getTwosComplement(result);
		
	}
	public static String padding(String s,int v) {
		int count = v - s.length();
		for( int i = 0; i < count;i++) {
			s = "0" + s;
		}
		return s;
	}
	public static short getTwosComplement(String binaryInt) {

	    if (binaryInt.charAt(0) == '1') {

	        String invertedInt = invertDigits(binaryInt);
	        //Change this to decimal format.
	        short decimalValue = Short.parseShort(invertedInt, 2);
	        //Add 1 to the curernt decimal and multiply it by -1
	        //because we know it's a negative number
	        decimalValue = (short) ((decimalValue + 1) * -1);
	        //return the final result
	        return decimalValue;
	    } else {
	        //Else we know it's a positive number, so just convert
	        //the number to decimal base.
	    	
	        return Short.parseShort(binaryInt, 2);
	    }
	}

	public static String invertDigits(String binaryInt) {
	    String result = binaryInt;
	    result = result.replace("0", " "); //temp replace 0s
	    result = result.replace("1", "0"); //replace 1s with 0s
	    result = result.replace(" ", "1"); //put the 1s back in
	    return result;
	}
	
	public static String determineTypeOpcode(byte opcode) {
		// b mask el opcode w bashoof el instruction R type wala I type according to el opcode
		String res ="";
		switch(opcode) {
		case 0,1,2,6,7:return res="R";
		case 3,4,5,8,9,10,11: return res="I";
		}
		return res;
		
	}
	public static ArrayList<Byte> decodeRtype (short ins){
		ArrayList<Byte> result = new ArrayList<Byte>();
		int opcodeMask=0b1111000000000000;
		int R1Mask=0b0000111111000000;
		int R2Mask=0b0000000000111111;
		byte opcode = (byte) ((ins & opcodeMask)>>>12);
		byte R1 = (byte) ((ins & R1Mask)>>>6);
		byte R2 = (byte) (ins & R2Mask);
		result.add(opcode);
		result.add(R1);
		result.add(R2);
		return result;
	}
	public static ArrayList<Byte> decodeItype(short ins) {

		ArrayList<Byte> result = new ArrayList<Byte>();
		int opcodeMask = 0b1111000000000000;
		int R1Mask = 0b0000111111000000;
		int immediateMask = 0b0000000000111111;
		byte opcode = (byte) ((ins & opcodeMask)>>>12);
		byte R1 = (byte) ((ins & R1Mask)>>>6);
		byte immediate = (byte)(ins & immediateMask);
		byte maskNeg = (byte) 0b00100000;
		byte temp = immediate;
		if((immediate & maskNeg )== 32) {
			immediate=getTwosComplementVal(immediate);
//			System.out.println("IMMEDIATEeeeeeeeeeeeeeeeees: " + immediate);
		}
		if(immediate < -31 && immediate > 31) {
			immediate = temp;
		}
		
		
		
		result.add(opcode);
		result.add(R1);
		result.add(immediate);
//		System.out.println("RESULT: " + result);
		return result;
		
	}	
	public static Byte getTwosComplementVal(Byte rakam) {
		byte maskKhara = 0b00011111;
		byte res = (byte) (maskKhara & rakam);
		byte result = (byte) (-32 + res);
		return result;
		
	}

	public static ArrayList<ArrayList<Byte>>decodeBoth(short ins) {
		ArrayList<ArrayList<Byte>> result = new ArrayList<ArrayList<Byte>>();
		ArrayList<Byte> Rtype = decodeRtype(ins);
		ArrayList<Byte> Itype = decodeItype(ins);
		result.add(Rtype);
		result.add(Itype);
		return result;
		
	}
	public static void executeWhich(ArrayList<Byte> Rtype, ArrayList<Byte>Itype) throws Exception {
		String type = determineTypeOpcode(Rtype.get(0));
//		System.out.println("RType: " + Rtype);
//		System.out.println("IType: " + Itype);
//		System.out.println("TYPE: " + type);
		if(type.equals("R"))
			executeRtype(Rtype);
		else
			executeItype(Itype);
		
	}
	
	public static void executeRtype(ArrayList<Byte> ins) throws Exception {
			byte opcode = ins.get(0);
			byte R1 = ins.get(1);
			byte R2 = ins.get(2);

			byte R1data=(byte) (registerFile.registers.get(R1).data);
			byte R2data=(byte) (registerFile.registers.get(R2).data);
			System.out.println("Register "+R1 +" old: "+R1data);
			System.out.println("opcode"+ opcode);
			
			String esmElInstruction = getInstruction(opcode);
			switch(esmElInstruction) {
			case"ADD":R1data= ADD(R1data,R2data);break;
			case"SUB":R1data= SUB(R1data,R2data);break;
			case"MUL":R1data= MUL(R1data,R2data);break;
			case"EOR":R1data= EOR(R1data,R2data);break;
			case"BR":
				short sh =(short)R1data;
				sh<<=8;
				short concat=(short)(sh | R2data);
				
				
				if(concat>=pc.data) {
					
					pc.data=concat;
					pc.data-=1;
				
					break;
				}
				else {
				    break;
				}
					
			default: throw  new Exception("Not R-Type instruction");
			}
			registerFile.registers.get(R1).data = R1data;
			
			
		}
			
		
	//assuming eno kol el dataa el fi el registers already in 2's complement
	public static byte ADD( byte R1data,byte R2data) {
		boolean overflow=false;//8
		boolean negative= false;//4
		int temp;
		int overFlowMask=0b00000000000000000000000010000000;
		int carryMask=0b00000000000000000000000100000000;
	
		
		//handling overflow
		if(R1data>0 && R2data>0) {
			temp = ((R1data+R2data) & overFlowMask)>>7;
			if(temp ==1) {
				overflow = true;
				registerFile.registers.get(64).data+=8;
			}
		}
		if(R1data<0 && R2data<0) {
			temp = ((R1data+R2data) & overFlowMask)>>7;
		   if(temp ==0) {
			overflow = true;
			registerFile.registers.get(64).data+=8;
		   }
		}
		//handling carry ******************** hassan byfty
		System.out.println(R1data+R2data);
		temp = ((R1data|R2data)&carryMask)>>8;
		System.out.println(temp);
		if(temp==1 || (R1data >= 64 && R2data == -1 * R1data) || (R2data >= 64 && R1data == -1 * R2data) ) {	
			registerFile.registers.get(64).data+=16;
		}
		//handling negative
		if(((byte)R1data+R2data)<0) {
			negative = true;
			registerFile.registers.get(64).data+=4;
		}
		//handling sign
		if(negative^overflow) {
			registerFile.registers.get(64).data+=2;
		}
		//handling zero
		if((R1data+R2data ==0)){
			registerFile.registers.get(64).data+=1;
			
		}
		return (byte)(R1data + R2data);
	}
	
	public static byte SUB( byte R1data,byte R2data) {
		boolean overflow=false;//8
		boolean negative= false;//4
		int temp;
		int overFlowMask=0b00000000000000000000000010000000;
	
		
		//handling overflow
		if(R1data>0 && R2data>0) {
			temp = ((R1data-R2data) & overFlowMask)>>7;
			if(temp ==1) {
				overflow = true;
				registerFile.registers.get(64).data+=8;
			}
		}
		if(R1data<0 && R2data<0) {
			temp = ((R1data-R2data) & overFlowMask)>>7;
		   if(temp ==0) {
			overflow = true;
			registerFile.registers.get(64).data+=8;
		   }
		}
		//handling sign
		if(negative^overflow) {
			registerFile.registers.get(64).data+=2;
		}
		//handling zero
		if(R1data-R2data == 0) {
			registerFile.registers.get(64).data+=1;
			
		}
		return (byte)(R1data - R2data);
	}
	
	public static byte MUL(byte R1data,byte R2data) {
	
		if((R1data*R2data)==0) 
			registerFile.registers.get(64).data+=1;
		if((R1data*R2data)<0) 
			registerFile.registers.get(64).data+=4;
		return (byte)(R1data*R2data);

	}
	
	public static byte EOR(byte R1data,byte R2data) {
		//negative w zero
		if((R1data^R2data)==0) 
			registerFile.registers.get(64).data+=1;
		if((R1data^R2data)<0) 
			registerFile.registers.get(64).data+=4;
		return (byte)(R1data^R2data);
			
	}
	
	public static void executeItype(ArrayList<Byte> ins) throws Exception {
			byte opcode = ins.get(0);
			byte R1= ins.get(1);
			byte immediate =ins.get(2);
//			System.out.println("Immediate: " + immediate);
			byte R1data=registerFile.registers.get(R1).data;
			String esmElInstruction = getInstruction(opcode);
			switch(esmElInstruction) {
			case "MOVI": 
				if(immediate >= -31 && immediate <= 31) {
				R1data = (byte) immediate;break;
				}
				else {
					System.out.println("BALASH GHABAA2");break;
				}
			//3ayzeen nshoof hoa by use byte addressing wala word addressing
			case "BEQZ": 
				if(immediate >= 0 && R1data==0) {
					
					pc.data=(short)(pc.data+1+immediate-3);break;
				}
				else{
					System.out.println("BALASH GHABA2 TANY");
				}
			case"ANDI":R1data = ANDI(R1data,immediate);break;
			case"SAL":R1data=SAL(R1data,immediate);break;
			case"SAR":R1data =SAR(R1data,immediate);break;
			case"LDR":R1data= dataMemory.dataMemory[immediate];break;
			case"STR":
				System.out.println("Old value at memory location "+immediate+": "+dataMemory.dataMemory[immediate]);
				dataMemory.dataMemory[immediate]=R1data;
				System.out.println("New value at memory location "+immediate+": "+dataMemory.dataMemory[immediate]);break;
			default:throw  new Exception("Not I-Type instruction");	
			}
			registerFile.registers.get(R1).data = R1data;
		}
		
	public static byte ANDI(byte R1data,byte immediate) {
		//negative w zero
		if((R1data&immediate)==0) 
			registerFile.registers.get(64).data+=1;
		if((R1data&immediate)<0) 
			registerFile.registers.get(64).data+=4;
		return (byte)(R1data&immediate);			
	}
	public static byte SAL(byte R1data,byte immediate) {
		//negative w zero
		if((R1data<<immediate)==0) 
			registerFile.registers.get(64).data+=1;
		if((R1data<<immediate)<0) 
			registerFile.registers.get(64).data+=4;
		return (byte)(R1data<<immediate);			
	}
	public static byte SAR(byte R1data,byte immediate) {
		//negative w zero
		if((R1data>>immediate)==0) 
			registerFile.registers.get(64).data+=1;
		if((R1data>>immediate)<0) 
			registerFile.registers.get(64).data+=4;
		return (byte)(R1data>>immediate);			
	}
	
	public static String getInstruction(short opcode) {
		String result = "";
		switch(opcode) {
		case 0: result = "ADD"; break;
		case 1:result = "SUB"; break;
		case 2:result = "MUL"; break;
		case 3:result = "MOVI"; break;
		case 4:result = "BEQZ"; break;
		case 5:result = "ANDI"; break;
		case 6:result = "EOR"; break;
		case 7:result = "BR"; break;
		case 8:result = "SAL"; break;
		case 9:result = "SAR"; break;
		case 10:result = "LDR"; break;
		case 11:result = "STR"; break;
	}
		return result;
	}
	public static void oneInstruction(ArrayList<Short>fetch,ArrayList<ArrayList<Byte>>decode)throws Exception {
		System.out.println("========================");
		System.out.println("Clock Cycle: "+clock);
		System.out.println("========================");
		fetch.add(fetch());
		System.out.println("Fetching:  Instruction 0");
		clock++;
		System.out.println("========================");
		System.out.println("Clock Cycle: "+clock);
		System.out.println("========================");
		decode.addAll(decodeBoth(fetch.get(0)));
		System.out.println("Decoding:  Instruction 0");
		clock++;
		System.out.println("========================");
		System.out.println("Clock Cycle: "+clock);
		System.out.println("========================");
		executeWhich(decode.get(0),decode.get(1));
		System.out.println("Executing:  Instruction 0");
		clock++;
		
	}
	public static void pipelining () throws Exception {
		storeInMemory();
		int n = instructionsMemory.count;
//		System.out.println("N????????????: " + n);
//		int clockCyclesNeeded = 3+((n-1)*1);
		int i=0;
		int toBeDecoded=0;
		int toBeExecuted =0;
		int decodeCount = 0;
		int executeCount = 0;
		int fetchCount=0;
		ArrayList<Short> fetch= new ArrayList<Short>();
		ArrayList<ArrayList<Byte>> decode= new ArrayList<ArrayList<Byte>>();
		int insNum =0;
		if(n==1) {
			oneInstruction(fetch, decode);
			finalPrint();
		}
		else {
			while(pc.data < n) {
			System.out.println("========================");
			System.out.println("Clock Cycle: "+clock);
			System.out.println("========================");
			if(fetch.isEmpty() && decode.isEmpty()) {
				if(pc.data <= n) {
					fetch.add(fetch());//fetch 0
					pc.data++;
					System.out.println("Fetching:  Instruction "+ fetchCount + ": " + instructions.get(fetchCount));
					fetchCount++;
//					System.out.println(fetch);
				}
				clock++;
				continue;
			}
			if(!fetch.isEmpty()&& decode.isEmpty()) {
//				decodeCount = 0;
			if(pc.data < n) {
				fetch.add(fetch());//fetch 1
				pc.data++;
				System.out.println("Fetching:  Instruction "+ fetchCount + ": " + instructions.get(fetchCount));
				fetchCount++;
//				System.out.println(fetch);
			}
			if(fetch.size() > toBeDecoded) {
				decode.addAll(decodeBoth(fetch.get(toBeDecoded)));
				toBeDecoded++;
				System.out.println("Decoding:  Instruction "+decodeCount + ": " + instructions.get(decodeCount));
				decodeCount++;
				}
				
				
				
//				insNum++;
				clock++;
				
				continue;
				
			}
			if(!fetch.isEmpty()&& !decode.isEmpty()) {
				if(pc.data <n) {
					fetch.add(fetch());
					pc.data++;
					System.out.println("Fetching:  Instruction "+ fetchCount);
					fetchCount++;
//					System.out.println(fetch);
				}
				if(fetch.size() > toBeDecoded) {
					if(fetch.size() > toBeDecoded) {
						decode.addAll(decodeBoth(fetch.get(toBeDecoded)));
						toBeDecoded++;
						System.out.println("Decoding:  Instruction "+decodeCount);
						decodeCount++;
						
						}
				}
				//bashv oof lw el opcode opcode el branch if zero
				if(decode.get(toBeExecuted).get(0)== 4) { ///// ADD PRINTINGSSS
					int index = decode.get(toBeExecuted).get(1);
					int immediate = decode.get(toBeExecuted).get(2);
					//lw el data b zero f3ln ha execute el instruction w a flush
					if(registerFile.registers.get(index).data == 0) {
						
						executeWhich(decode.get(toBeExecuted),decode.get(++toBeExecuted));
						toBeExecuted++;
						System.out.println("Executing: Instruction "+ executeCount);
						executeCount++;
						fetchCount = pc.data;
						decodeCount = pc.data;
						executeCount = pc.data;
						//hafady el fetch w el decode
						while(!fetch.isEmpty())
							fetch.remove(0);
						while(!decode.isEmpty())
							decode.remove(0);	
						//reset el pointers
						toBeDecoded=0;
						toBeExecuted=0;
						clock++;
						continue;
					}
					else {
						executeWhich(decode.get(toBeExecuted),decode.get(++toBeExecuted));
						toBeExecuted++;
						System.out.println("Executing: Instruction "+ executeCount);
						executeCount++;
						clock++;
						continue;
						
					}
						
				}
				 else {
					 if(decode.get(toBeExecuted).get(0)==7){
						 int indexR1 = decode.get(toBeExecuted).get(1);
						int indexR2 = decode.get(toBeExecuted).get(2);
						Byte r1 = registerFile.registers.get(indexR1).data;
						Byte r2 = registerFile.registers.get(indexR2).data;
						short conncat = (short)(r1|r2);
						if(conncat >= pc.data) {
							executeWhich(decode.get(toBeExecuted),decode.get(++toBeExecuted));
					toBeExecuted++;
					System.out.println("Executing: Instruction "+ executeCount);
					executeCount++;
					fetchCount = pc.data;
					decodeCount = pc.data;
					executeCount = pc.data;
					//hafady el fetch w el decode
					
					while(!fetch.isEmpty())
						fetch.remove(0);
					while(!decode.isEmpty())
						decode.remove(0);
					
					
					//reset el pointers
					toBeDecoded=0;
					toBeExecuted=0;
					clock++;
					continue;
						}
						else {
							executeWhich(decode.get(toBeExecuted),decode.get(++toBeExecuted));
							toBeExecuted++;
							System.out.println("Executing: Instruction "+ executeCount);
							executeCount++;
							clock++;
							continue;
							
						}
						
				}
			else {
				executeWhich(decode.get(toBeExecuted),decode.get(++toBeExecuted));
				toBeExecuted++;
				System.out.println("Executing: Instruction "+ executeCount);
				executeCount++;
				clock++;
				continue;
				}
			 }
				
			}
		  
		}
			  System.out.println("========================");
			  System.out.println("Clock Cycle: "+clock);
			  System.out.println("========================");
			  clock++;
			if(!fetch.isEmpty()) {
			if(fetch.size() > toBeDecoded) {
				decode.addAll(decodeBoth(fetch.get(toBeDecoded)));
				toBeDecoded++;
				System.out.println("Decoding:  Instruction "+decodeCount);
				decodeCount++;
				
				}
		}
		if(!decode.isEmpty()) {
		  executeWhich(decode.get(toBeExecuted),decode.get(++toBeExecuted));
		  toBeExecuted++;
		  System.out.println("Executing: Instruction "+ executeCount);
		  executeCount++;
		}
		  System.out.println("========================");
		  System.out.println("Clock Cycle: "+clock);
		  System.out.println("========================");
		  clock++;
		  
		  
		  if(!decode.isEmpty()) {
			  if(decode.size() > toBeExecuted + 1) {
			  executeWhich(decode.get(toBeExecuted),decode.get(++toBeExecuted));
			  toBeExecuted++;
			  System.out.println("Executing: Instruction "+ executeCount);
			  executeCount++;
			  }
			}
//		  System.out.println("========================");
//		  System.out.println("Clock Cycle: "+clock);
//		  System.out.println("========================");
//		 
//		  System.out.println("Executing: Instruction "+executeCount);
//		  
//		  clock++;
//		  finalPrint();
		}
		
	}
	public static void finalPrint() {
		  System.out.println("=======================Register File=====================");
		  for(int j =0; j<65 ;j++) {
			  System.out.println(registerFile.registers.get(j).name+ ": "+ registerFile.registers.get(j).data);  
		  }
		  System.out.println("PC: "+pc.data);
		  System.out.println();
		  System.out.println("=======================Data Memory=====================");
		  for(int k=0; k<dataMemory.dataMemory.length;k++) {
			  System.out.println("Data Memory["+k+"] = "+dataMemory.dataMemory[k]);
		  }
		  System.out.println();
		  System.out.println("=======================Instruction Memory=====================");
		  for(int l=0; l<instructionsMemory.instructionMemory.length;l++) {
			  System.out.println("Instruction Memory["+l+"] = "+instructionsMemory.instructionMemory[l]);
		  }		  	
	}
	public static void regFilePrint() {
		System.out.println("=======================Register File=====================");
		  for(int j =0; j<65 ;j++) {
			  System.out.println(registerFile.registers.get(j).name+ ": "+ registerFile.registers.get(j).data);  
		  }
		  System.out.println("PC: "+pc.data);
		  System.out.println();
	}
	public static void insMemPrint() {
		System.out.println("=======================Instruction Memory=====================");
		  for(int l=0; l<instructionsMemory.instructionMemory.length;l++) {
			  System.out.println("Instruction Memory["+l+"] = "+instructionsMemory.instructionMemory[l]);
		  }	
		  System.out.println();
	}
	public static void dataMemPrint() {
		System.out.println("=======================Data Memory=====================");
		  for(int k=0; k<dataMemory.dataMemory.length;k++) {
			  System.out.println("Data Memory["+k+"] = "+dataMemory.dataMemory[k]);
		  }
		  System.out.println();
	}
	public void reg() {
		OutputStream outputStream = new OutputStream() {
            public void write(int b) {
                outputTextReg.appendText(String.valueOf((char) b));
            }
        };
        ogRegOutputStream = System.out;
        System.setOut(new PrintStream(outputStream));
        try {
			regFilePrint();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        System.setOut(ogRegOutputStream);
	}
	public void ins() {
		OutputStream outputStream = new OutputStream() {
            public void write(int b) {
                outputTextIns.appendText(String.valueOf((char) b));
            }
        };
        ogInsOutputStream = System.out;
        System.setOut(new PrintStream(outputStream));
        try {
			insMemPrint();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        System.setOut(ogInsOutputStream);
	}
	public void data() {
		OutputStream outputStream = new OutputStream() {
            public void write(int b) {
                outputTextData.appendText(String.valueOf((char) b));
            }
        };
        ogDataOutputStream = System.out;
        System.setOut(new PrintStream(outputStream));
        try {
			dataMemPrint();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        System.setOut(ogDataOutputStream);
	}
		  
	public void pipeline() {
		OutputStream outputStream = new OutputStream() {
            public void write(int b) {
                outputText.appendText(String.valueOf((char) b));
            }
        };
        ogOutputStream = System.out;
        System.setOut(new PrintStream(outputStream));
        try {
			pipelining();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        System.setOut(ogOutputStream);

	}

//public static void main(String[] args) throws Exception {
////	int x = -4;
//	
////	int y = 5;
////	int x1 =~x+1;
////	int y1=~y+1;
////	System.out.println(x1<0);
//	Architecture a = new Architecture();
//	a.pipelining();
////	int num = -20353;
////	System.out.println();
//	
//
////	a.registerFile.registers.get(1).data = 64;
////	a.registerFile.registers.get(2).data = -64;
////	a.ADD((byte)-64, (byte)64);
////	//a.SUB((byte)64, (byte)64);
////	System.out.println(registerFile.registers.get(64).data);
//	
//
//}

}
