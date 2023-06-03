package main;
import java.util.ArrayList;

public class RegisterFile {
	ArrayList <Register> registers;
	
	public RegisterFile() {
		registers = new ArrayList<Register>();
		for(int i=0 ; i<= 63 ;i++) {
			String regName = "R"+i;
			registers.add(new Register(regName));
		}
		registers.add(new Register("SREG"));
		
	}

}
