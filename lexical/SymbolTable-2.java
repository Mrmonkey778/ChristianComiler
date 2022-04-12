//Christian Alexander Compilers spring 2022
package ADT;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;



public class SymbolTable {
	Entry[] table;
	int currentSize = 0;
	int max;
	
	//constructor to set up array of entry objects
	public SymbolTable(int maxSize){
		table = new Entry [maxSize]; //array of entries
		max=maxSize;
	}
	
	//Three overloaded methods to add symbol with given kind and value to the end 
	//of the symbol table, automatically setting the correct data_type, and returns the
	//index where the symbol was located
	int AddSymbol(String symbol, char kind, int value) {
		if(currentSize<=max) {
			if(LookupSymbol(symbol)==-1) {//checks to make sure not duplicate
			table[currentSize] = new Entry(symbol, kind);
			table[currentSize].SetInt(value);
			currentSize++;
			return currentSize-1;
			}
			return LookupSymbol(symbol);// if duplicate return index
		}
		return -1;//greater than max size
	}
	
	int AddSymbol(String symbol, char kind, double value) {
		if(currentSize<=max) {
			if(LookupSymbol(symbol)==-1) {
			table[currentSize] = new Entry(symbol, kind);
			table[currentSize].SetFloat(value);
			currentSize++;
			return currentSize-1;
			}
			return LookupSymbol(symbol);// -1 for the index
		}
		return -1;
	}
	
	int AddSymbol(String symbol, char kind, String value) {
		if(currentSize<=max) {
			if(LookupSymbol(symbol)==-1) {
			table[currentSize] = new Entry(symbol, kind);
			table[currentSize].SetString(value);
			currentSize++;
			return currentSize-1;
			}
			return LookupSymbol(symbol);// -1 for the index
		}
		return -1;
	}
	
	
	//Returns the index where symbol is found, or -1 if not in the table. Uses a non-case-sensitive comparison.
	int LookupSymbol(String symbol) {
		//uses exhaustive search to find name
		for(int i = 0; i < currentSize; i++) {
			if (symbol.compareToIgnoreCase(table[i].name) == 0) {
				return i;
			}
		}
		return -1;
	}
	
	
	//prints the symbol table to a file. ONLY PRINTS RELEVANT DATA
	void PrintSymbolTable(String filename){
		File out = new File(filename);
		try {
			FileWriter writer = new FileWriter(out);
			writer.write("Index     Name  Type       value\n");//writing to file formatted
			for(int i = 0; i<currentSize; i++) {
				writer.write(String.format("%2d, %30s, %3c,    ", i, table[i].name, table[i].kind));
				if(table[i].data_type=='F') {
					writer.write(String.format("%30f\tf\n", table[i].floatValue));
				}else if(table[i].data_type=='I') {
					writer.write(String.format("%30d\ti\n", table[i].integerValue));
				}else if(table[i].data_type=='S') {
					writer.write(String.format("%30s\ts\n", table[i].stringValue));
				}else {
					writer.write(String.format("%c\n", table[i].data_type));
				}
			}
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	//Return the various values currently stored at index.
	String GetSymbol(int index) {
		return table[index].name;
	}
	
	char GetKind(int index) {
		return table[index].kind;
	}
	
	char GetDataType(int index) {
		return table[index].data_type;
	}
	
	String GetString(int index) {
		return table[index].stringValue;
	}
	
	int GetInteger(int index) {
		return table[index].integerValue;
	}
	
	double GetFloat(int index) {
		return table[index].floatValue;
	}
	//Return the various values currently stored at index.
	
	//overloaded updates
	void UpdateSymbol(int index, char kind, int value){
		table[index].kind=kind;
		table[index].integerValue=value;
	}
	
	void UpdateSymbol(int index, char kind, double value) {
		table[index].kind=kind;
		table[index].floatValue=value;
	}
	
	void UpdateSymbol(int index, char kind, String value) {
		table[index].kind=kind;
		table[index].stringValue=value;
	}
	//overloaded updates
	
	//Entry class used to make an array of entries
	public class Entry{
		String name;
		char kind;
		char data_type;
		int integerValue;
		double floatValue;
		String stringValue;
		
		//sets the entry on initialization w/ constructor
		public Entry(String symbol, char t ) {
			name = symbol;
			kind = t;
		}
		
		
		void SetInt(int a) {
			integerValue = a;
			data_type = 'I';
		}
		
		void SetFloat(double a) {
			floatValue = a;
			data_type = 'F';
		}
		
		void SetString(String a) {
			stringValue = a;
			data_type = 'S';
		}
	}
}
