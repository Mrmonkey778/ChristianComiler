package ADT;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import ADT.*;


public class Interpreter {
	//inites necessary variables
	int pc;
	int maxCommands = 20;
	QuadTable qtable;
	SymbolTable stable;
	ReserveTable rtable;
	
	public Interpreter() {//constructor that inites the reserve table with the commands
		rtable = new ReserveTable(maxCommands);
		rtable.Add("STOP", 0);
		rtable.Add("DIV", 1);
		rtable.Add("MUL", 2);
		rtable.Add("SUB", 3);
		rtable.Add("ADD", 4);
		rtable.Add("MOV", 5);
		rtable.Add("PRINT", 6);
		rtable.Add("READ", 7);
		rtable.Add("JMP", 8);
		rtable.Add("JZ", 9);
		rtable.Add("JP", 10);
		rtable.Add("JN", 11);
		rtable.Add("JNZ", 12);
		rtable.Add("JNP", 13);
		rtable.Add("JNN", 14);
		rtable.Add("JINDR", 15);
	}//Interpreter
	
	boolean initializeFactorialTest(SymbolTable stable, QuadTable qtable) {//sets the stable and qtable to run the factorial test
		//filling symbol table accordingly
		stable.AddSymbol("n", 'I', 10);
		stable.AddSymbol("i", 'I', 0);
		stable.AddSymbol("product", 'I', 0);
		stable.AddSymbol("1", 'I', 1);
		stable.AddSymbol("$temp", 'I', 0);
		
		//filling Quad table accordingly
		qtable.AddQuad(5, 3, 00, 2);//MOV
		qtable.AddQuad(5, 3, 00, 1);//MOV
		qtable.AddQuad(3, 1, 0, 04);//SUB
		qtable.AddQuad(10, 04, 00, 07);//JP
		qtable.AddQuad(2, 02, 01, 02);//MUL
		qtable.AddQuad(4, 01, 03, 01);//ADD
		qtable.AddQuad(8, 00, 00, 02);//JMP
		qtable.AddQuad(6, 00, 00, 02);//PRINT
		qtable.AddQuad(0, 0, 00, 00);//STOP
		return true;
	}//initializeFactorialTest
	
	boolean initializeSummationTest(SymbolTable stable, QuadTable qtable) {//sets the stable and qtable to run the summation test
		//filling symbol table accordingly
		stable.AddSymbol("n", 'I', 10);
		stable.AddSymbol("i", 'I', 0);
		stable.AddSymbol("sum", 'I', 0);
		stable.AddSymbol("1", 'I', 1);
		stable.AddSymbol("$temp", 'I', 0);
		
		//filling quad table accordingly
		qtable.AddQuad(5, 03, 00, 02);//MOV
		qtable.AddQuad(5, 3, 00, 1);//MOV
		qtable.AddQuad(3, 01, 00, 04);//SUB
		qtable.AddQuad(10, 04, 00, 07);//JP
		qtable.AddQuad(4, 02, 01, 02);//ADD
		qtable.AddQuad(4, 01, 03, 01);//ADD
		qtable.AddQuad(8, 0, 0, 2);//JMP
		qtable.AddQuad(6, 0, 00, 02);//PRINT
		qtable.AddQuad(0, 0, 00, 00);//STOP
		return true;
	}//initializeSummationTest
	
	public void InterpretQuads(QuadTable Q, SymbolTable S, boolean TraceOn, String filename) throws IOException { // takes the q and s tables and runs the program while writing out to a file if Trace is on
		//inits necessary variables
		pc=0;
		String in;
		File out = new File(filename);
		FileWriter writer = new FileWriter(out);
		boolean c = true; //sets continue variable to true
		while(c) {
			//prints PC, INSTRUCTION, OP1, OP2, OP3
			if(TraceOn) {
				writer.write("PC = "+pc+": "+rtable.LookupCode(Q.GetQuad(pc, 0))+"\t"+Q.GetQuad(pc, 1)+", "+Q.GetQuad(pc, 2)+", "+Q.GetQuad(pc, 3)+"\n");
				}
			
			//TO GET A VALUE MUST USE S.GetInteger(Q.GetQuad(pc, op#) because we are using the symbol value directed by the index found in the corresponding op# in the quad table
			switch(Q.GetQuad(pc, 0)) {//uses a switch along with the 0 index of the qtable column (opcode)
			case 0://STOP
				c=false; //breaks continue
				break;
			case 1://DIV
				//updates symbol table
				S.UpdateSymbol(Q.GetQuad(pc, 3), //op3
						'I', 
						S.GetInteger(Q.GetQuad(pc, 1))/S.GetInteger(Q.GetQuad(pc, 2))); //op1/op2
				
				pc++;
				break;
			case 2://MUL
				//updates symbol table
				S.UpdateSymbol(Q.GetQuad(pc, 3), //op3
						'I', 
						S.GetInteger(Q.GetQuad(pc, 1))*S.GetInteger(Q.GetQuad(pc, 2))); //op1*op2
				
				pc++;
				break;
			case 3://SUB
				//updates symbol table
				S.UpdateSymbol(Q.GetQuad(pc, 3), //op3
						'I', 
						S.GetInteger(Q.GetQuad(pc, 1))-S.GetInteger(Q.GetQuad(pc, 2))); //op1-op2
				
				pc++;
				break;
			case 4://ADD
				//updates symbol table
				S.UpdateSymbol(Q.GetQuad(pc, 3), //op3
						'I', 
						S.GetInteger(Q.GetQuad(pc, 1))+S.GetInteger(Q.GetQuad(pc, 2))); //op1+op2
				
				pc++;
				break;
			case 5://MOV
				//updates symbol table
				S.UpdateSymbol(Q.GetQuad(pc, 3), //op3
						'I', 
						S.GetInteger(Q.GetQuad(pc, 1))); //op1
				
				pc++;
				break;
			case 6://PRINT
				//Prints Name and Value of a symbol
				writer.write(S.GetSymbol(Q.GetQuad(pc, 3))+" "+S.GetInteger(Q.GetQuad(pc, 3))+"\n");
				pc++;
				break;
			case 7://READ
				//GET INPUT
				in = System.console().readLine();
						//updates symbol table
						S.UpdateSymbol(Q.GetQuad(pc, 3), //op3
								'I', 
								in); //op1
				pc++;
				break;
			case 8://JMP
				//updates pc
				pc=Q.GetQuad(pc, 3);
				break;
			case 9://JZ
				//updates pc if op1==0
				if(Q.GetQuad(pc, 1)==0) {
					pc=Q.GetQuad(pc, 3);
				}else {
					pc++;
				}
				break;
			case 10://JP
				//updates pc if op1>0
				if(S.GetInteger(Q.GetQuad(pc, 1))>0) {
					pc=Q.GetQuad(pc, 3);
				}else {
					pc++;
				}
				break;
			case 11://JN
				//updates pc if op1<0
				if(Q.GetQuad(pc, 1)<0) {
					pc=Q.GetQuad(pc, 3);
				}else {
					pc++;
				}
				break;
			case 12://JNZ
				//updates pc if op1!=0
				if(Q.GetQuad(pc, 1)!=0) {
					pc=Q.GetQuad(pc, 3);
				}else {
					pc++;
				}
				break;
			case 13://JNP
				//updates pc if op1<=0
				if(Q.GetQuad(pc, 1)<=0) {
					pc=Q.GetQuad(pc, 3);
				}else {
					pc++;
				}
				break;
			case 14://JNN
				//updates pc if op1>=0
				if(Q.GetQuad(pc, 1)>=0) {
					pc=Q.GetQuad(pc, 3);
				}else {
					pc++;
				}
				break;
			case 15://JINDR
				//updates pc to op3
				pc=S.GetInteger(Q.GetQuad(pc, 3));
				break;
			}
		}//while
		writer.close();
	}//InterpretQuads
}
