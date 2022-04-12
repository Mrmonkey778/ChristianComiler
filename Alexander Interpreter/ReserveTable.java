//Christian Alexander Compilers spring 2022
package ADT;
import java.io.File;
import java.io.IOException;
import java.io.FileWriter;

//Christian Alexander
public class ReserveTable {
	Entry[] table;
	int currentSize = 0;
	
	public ReserveTable(int maxSize){
		table = new Entry [maxSize]; //array of entries
	}
	
	int Add(String name, int code) {
		table[currentSize] = new Entry(name, code);
		currentSize++;
		return currentSize-1; // -1 for the index
	}
	
	int LookupName(String name) {
		//uses exhaustive search to find name
		for(int i = 0; i < currentSize; i++) {
			if (name.compareToIgnoreCase(table[i].word) == 0) {
				return table[i].op;
			}
		}
		return -1;
	}
	
	String LookupCode(int code) {
		//uses exhaustive search to find code
		for(int i = 0; i < currentSize; i++) {
			if (code == table[i].op) {
				return table[i].word;
			}
		}
		return "";
	}
	
	void PrintReserveTable(String filename){
		File out = new File(filename);
		try {
			FileWriter writer = new FileWriter(out);
			writer.write("Index     Name  Code\n");//writing to file formatted
			for(int i = 0; i<currentSize; i++) {
				writer.write(String.format("%2d, %10s, %d\n", i, table[i].word, table[i].op));
			}
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//Entry class used to make an array on entries with a name and a code
	public class Entry{
		String word;
		int op;
		
		//sets the entry on initialization w/ constructor
		public Entry(String name, int code) {
			word = name;
			op = code;
		}
		
		void SetOp(int code) {
			op = code;
		}
		
		void SetWord(String name) {
			word = name;
		}
	}
}


