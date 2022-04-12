//Christian Alexander Compilers spring 2022
package ADT;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class QuadTable {
	int[][] table;
	private int nextAvailable = 0;
	
	//constructor
	public QuadTable(int maxSize){
		table = new int [maxSize][4]; //array of entries
	}
	
	//returns the private nextAvailable
	int NextQuad() {
		return nextAvailable;
	}
	
	//adds a quad at the nextAvailable index
	void AddQuad(int opcode, int op1, int op2, int op3) {
		table[nextAvailable][0] = opcode;
		table[nextAvailable][1] = op1;
		table[nextAvailable][2] = op2;
		table[nextAvailable][3] = op3;
		nextAvailable++;
	}
	
	//returns the value held at the index and column location
	int GetQuad(int index, int column) {
		return table[index][column];
	}
	
	//resets all the data at the index to the data passed in
	void UpdateQuad(int index, int opcode, int op1, int op2, int op3) {
		table[index][0] = opcode;
		table[index][1] = op1;
		table[index][2] = op2;
		table[index][3] = op3;
	}
	
	//prints the quad table to a specified file ONLY PRINTS RELEVANT DATA
	void PrintQuadTable(String filename){
		File out = new File(filename);
		try {
			FileWriter writer = new FileWriter(out);
			writer.write("Index   opcode o1  o2  o3\n");//writing to file formatted
			for(int i = 0; i<nextAvailable; i++) {
				writer.write(String.format("%2d         %2d, %2d, %2d, %2d\n", i, table[i][0], table[i][1], table[i][2], table[i][3]));
			}
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
