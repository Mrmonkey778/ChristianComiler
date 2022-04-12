/*
 The following code is provided by the instructor for the completion of PHASE 2 
of the compiler project for CS4100.

STUDENTS ARE TO PROVIDE THE FOLLOWING CODE FOR THE COMPLETION OF THE ASSIGNMENT:

1) Initialize the 2 reserve tables, which are fields in the Lexical class,
    named reserveWords and mnemonics.  Create the following functions,
    whose calls are in the lexical constructor:
        initReserveWords(reserveWords);
        initMnemonics(mnemonics);

2) 


 */
package ADT;

import ADT.*;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Locale;

/**
 *
 * @author abrouill
 */
import java.io.*;

public class Lexical {

    private File file;                        //File to be read for input
    private FileReader filereader;            //Reader, Java reqd
    private BufferedReader bufferedreader;    //Buffered, Java reqd
    private String line;                      //Current line of input from file   
    private int linePos;                      //Current character position
    //  in the current line
    private SymbolTable saveSymbols;          //SymbolTable used in Lexical
    //  sent as parameter to construct
    private boolean EOF;                      //End Of File indicator
    private boolean echo;                     //true means echo each input line
    private boolean printToken;               //true to print found tokens here
    private int lineCount;                    //line #in file, for echo-ing
    private boolean needLine;                 //track when to read a new line
    //Tables to hold the reserve words and the mnemonics for token codes
    private ReserveTable reserveWords = new ReserveTable(50); //a few more than # reserves
    private ReserveTable mnemonics = new ReserveTable(50); //a few more than # reserves
//global char
    char currCh;

    //constructor
    public Lexical(String filename, SymbolTable symbols, boolean echoOn) {
        saveSymbols = symbols;  //map the initialized parameter to the local ST
        echo = echoOn;          //store echo status
        lineCount = 0;          //start the line number count
        line = "";              //line starts empty
        needLine = true;        //need to read a line
        printToken = false;     //default OFF, do not print tokesn here
        //  within GetNextToken; call setPrintToken to
        //  change it publicly.
        linePos = -1;           //no chars read yet
        //call initializations of tables
        initReserveWords(reserveWords);
        initMnemonics(mnemonics);

        //set up the file access, get first character, line retrieved 1st time
        try {
            file = new File(filename);    //creates a new file instance  
            filereader = new FileReader(file);   //reads the file  
            bufferedreader = new BufferedReader(filereader);  //creates a buffering character input stream  
            EOF = false;
            currCh = GetNextChar();
        } catch (IOException e) {
            EOF = true;
            e.printStackTrace();
        }
    }

    // token class is declared here, no accessors needed
    public class token {

        public String lexeme;
        public int code;
        public String mnemonic;

        token() {
            lexeme = "";
            code = 0;
            mnemonic = "";
        }
    }

    private token dummyGet() {
        token result = new token();
        result.lexeme = "" + currCh; //have the first char
        currCh = GetNextChar();
        result.code = 0;
        result.mnemonic = "DUMMY";
        return result;

    }

    private final int UNKNOWN_CHAR = 99;
    public final int _GRTR = 38;
    public final int _LESS = 39;
    public final int _GREQ = 40;
    public final int _LEEQ = 41;
    public final int _EQLS = 42;
    public final int _NEQL = 43;

    private final int IDENT_ID = 50;
    private final int INTEGER_ID = 51;
    private final int FLOAT_ID = 52;
    private final int STRING_ID = 53;

    /* @@@ These are nice for syntax to call later 
    // given a mnemonic, find its token code value
    public int codeFor(String mnemonic) {
        return mnemonics.LookupName(mnemonic);
    }
    // given a mnemonic, return its reserve word
    public String reserveFor(String mnemonic) {
        return reserveWords.LookupCode(mnemonics.LookupName(mnemonic));
    }
     */
    // Public access to the current End Of File status
    public boolean EOF() {
        return EOF;
    }

    // DEBUG enabler, turns on token printing inside of GetNextToken
    public void setPrintToken(boolean on) {
        printToken = on;
    }

    /* @@@ */
    private void initReserveWords(ReserveTable reserveWords) {
        reserveWords.Add("GOTO", 0);
        reserveWords.Add("INTEGER", 1);
        // add the rest of the reserve words here

        // add 1 and 2-char tokens here
        reserveWords.Add("/", 30);
        reserveWords.Add(":=", 37);

    }

    /* @@@ */
    private void initMnemonics(ReserveTable mnemonics) {
// add 5-character student created mnemonics corresponding to reserve values
        mnemonics.Add("GOTO_", 0);
        mnemonics.Add("INTGE", 1);
        mnemonics.Add("UNKWN", 99);

    }

    // Character category for alphabetic chars, upper and lower case
    private boolean isLetter(char ch) {
        return (((ch >= 'A') && (ch <= 'Z')) || ((ch >= 'a') && (ch <= 'z')));
    }

    // Character category for 0..9 
    private boolean isDigit(char ch) {
        return ((ch >= '0') && (ch <= '9'));
    }

    // Category for any whitespace to be skipped over
    // space, tab, and newline
    private boolean isWhitespace(char ch) {
        return ((ch == ' ') || (ch == '\t') || (ch == '\n'));
    }

    // Returns the VALUE of the next character without removing it from the
    //    input line.  Useful for checking 2-character tokens that start with
    //    a 1-character token.
    private char PeekNextChar() {
        char result = ' ';
        if ((needLine) || (EOF)) {
            result = ' '; //at end of line, so nothing
        } else // 
        {
            if ((linePos + 1) < line.length()) { //have a char to peek
                result = line.charAt(linePos + 1);
            }
        }
        return result;
    }

    // Called by GetNextChar when the characters in the current line
    // buffer string (line) are used up.
    private void GetNextLine() {
        try {
            line = bufferedreader.readLine();  //returns a null string when EOF
            if ((line != null) && (echo)) {
                lineCount++;
                System.out.println(String.format("%04d", lineCount) + " " + line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (line == null) {    // The readLine returns null at EOF, set flag
            EOF = true;
        }
        linePos = -1;      // reset vars for new line if we have one
        needLine = false;  // we have one, no need
        //the line is ready for the next call to get a char with GetNextChar
    }

    // Returns the next character in the input file, returning a 
    // /n newline character at the end of each input line or at EOF
    public char GetNextChar() {
        char result;
        if (needLine) //ran out last time we got a char, so get a new line
        {
            GetNextLine();
        }
        //try to get char from line buff
        // if EOF there is no new char, just return endofline, DONE
        if (EOF) {
            result = '\n';
            needLine = false;
        } else {
            // if there are more characters left in the input buffer
            if ((linePos < line.length() - 1)) { //have a character available
                linePos++;
                result = line.charAt(linePos);
            } else {
                //need a new line, but want to return eoln on this call first
                result = '\n';
                needLine = true; //will read a new line on next GetNextChar call
            }
        }
        return result;
    }

    final char comment_start1 = '{';
    final char comment_end1 = '}';
    final char comment_start2 = '(';
    final char comment_startend = '*';
    final char comment_end2 = ')';

    // skips over any comment as if it were whitespace, so it is ignored
    public char skipComment(char curr) {
        // if this is the start of a comment...
        if (curr == comment_start1) {
            curr = GetNextChar();
            // loop until the end of comment or EOF is reached
            while ((curr != comment_end1) && (!EOF)) {
                curr = GetNextChar();
            }
            // if the file ended before the comment terminated
            if (EOF) {
                System.out.println("Comment not terminated before End Of File");
            } else {
                // keep getting the next char
                curr = GetNextChar();
            }
        } else {
            // this is for the 2-character comment start, different start/end
            if ((curr == comment_start2) && (PeekNextChar() == comment_startend)) {
                curr = GetNextChar(); // get the second
                curr = GetNextChar(); // into comment or end of comment
                //while comment end is not reached
                while ((!((curr == comment_startend) && (PeekNextChar() == comment_end2))) && (!EOF)) {
                    curr = GetNextChar();
                }
                // EOF before comment end
                if (EOF) {
                    System.out.println("Comment not terminated before End Of File");
                } else {
                    curr = GetNextChar();          //must move past close
                    curr = GetNextChar();          //must get following
                }
            }

        }
        return (curr);
    }

    //reads past any white space, blank lines, comments
    public char skipWhiteSpace() {

        do {
            while ((isWhitespace(currCh)) && (!EOF)) {
                currCh = GetNextChar();
            }
            currCh = skipComment(currCh);
        } while (isWhitespace(currCh) && (!EOF));
        return currCh;
    }

    // returns TRUE if ch is a prefix to a 2-character token like := or <=
    private boolean isPrefix(char ch) {
        return ((ch == ':') || (ch == '<') || (ch == '>'));
    }

    // returns TRUE if ch is the string delmiter
    private boolean isStringStart(char ch) {
        return ch == '"';
    }

//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
// Student supplied methods
    private token getIdent() {
    	token result = new token();  //create token
    	String str = "";  //init string
    	 while(isLetter(currCh) || isDigit(currCh) || currCh == '|' || currCh == '_') {//check for acceptable cahracters
    		 //add and iterate
    		 str+=currCh;
    		 currCh = GetNextChar();
    	 }//while
    	 
    	//THIS LOGIC USE ANY TIME RESULT IS UPDATED
        result.lexeme = str; //put in lex
        result.code = 50;//put in code
        result.mnemonic = "IDENT";//put in mnm
        
//check for reserve words
switch (str.toLowerCase()) {
        
		//lexeme is correct so only update code and mnm
        case "go_to":
            result.code = 0;
            result.mnemonic = "GO_TO";
            break;
            
        case "integer":
            result.code = 1;
            result.mnemonic = "INTRW";
            break;
            
        case "to":
            result.code = 2;
            result.mnemonic = "TORSW";
            break;
            
        case "do":
            result.code = 3;
            result.mnemonic = "DORSW";
            break;
            
        case "if":
            result.code = 4;
            result.mnemonic = "IFRSW";
            break;
            
        case "then":
            result.code = 5;
            result.mnemonic = "THENR";
            break;
            
        case "else":
            result.code = 6;
            result.mnemonic = "ELSER";
            break;
            
        case "for":
            result.code = 7;
            result.mnemonic = "FORRW";
            break;
            
        case "of":
            result.code = 8;
            result.mnemonic = "OFRSW";
            break;
            
        case "println":
            result.code = 9;
            result.mnemonic = "PRNTL";
            break;
            
        case "readln":
            result.code = 10;
            result.mnemonic = "READL";
            break;
        
        case "begin":
            result.code = 11;
            result.mnemonic = "BEGIN";
            break;
            
        case "end":
            result.code = 12;
            result.mnemonic = "ENDRW";
            break;
            
        case "var":
            result.code = 13;
            result.mnemonic = "VARRW";
            break;
            
        case "dowhile":
            result.code = 14;
            result.mnemonic = "DOWIL";
            break;
            
        case "program":
            result.code = 15;
            result.mnemonic = "PRGRM";
            break;
            
        case "label":
            result.code = 16;
            result.mnemonic = "LABEL";
            break;
            
        case "repeat":
            result.code = 17;
            result.mnemonic = "REPET";
            break;
            
        case "until":
            result.code = 18;
            result.mnemonic = "UNTIL";
            break;
            
        case "procedure":
            result.code = 19;
            result.mnemonic = "PRCDR";
            break;
            
        case "downto":
            result.code = 20;
            result.mnemonic = "DOWNT";
            break;
            
        case "function":
            result.code = 21;
            result.mnemonic = "FUNCT";
            break;
            
        case "return":
            result.code = 22;
            result.mnemonic = "RETRN";
            break;
            
        case "float":
            result.code = 23;
            result.mnemonic = "FLOAT";
            break;
            
        case "string":
            result.code = 24;
            result.mnemonic = "STRING";
            break;
            
        case "array":
            result.code = 25;
            result.mnemonic = "ARRAY";
            break;
        }//switch

		//update the symbol table with truncated lexeme
		saveSymbols.AddSymbol(checkTruncate(result).lexeme, 'v', 0);
		
        return result;//return result token
    }//get ident

    private token getNumber() {
    	boolean flt = false;//check for float
    	String str = "";//init str
    	token result = new token();//init token
    	result.code = 51;//set code
        result.mnemonic = "INTEG";//set mnm
        
        /* a number is:   <digit>+[.<digit>*[E[+|-]<digit>+]] */
    	while(isDigit(currCh)) {//<digit>
        	str+=currCh;
        	currCh = GetNextChar();
        	
        	if(currCh == '.') {//.
        		flt=true;//is float
        		str+=currCh;
            	currCh = GetNextChar();
            	result.code = 52;//set code
                result.mnemonic = "FLOAT";//set mnm
            	
            	while(isDigit(currCh)) {//<digit>
            		str+=currCh;
                	currCh = GetNextChar();
                	
            	}//while
                	
                	if(currCh == 'E' || currCh == 'e') {//E|e
                		str+=currCh;
                    	currCh = GetNextChar();
                    	
                    	if(currCh == '+' || currCh == '-') {//+|-
                    		str+=currCh;
                        	currCh = GetNextChar();
                    	}//if
                        
                    	while(isDigit(currCh)) {//<digit>
                    		str+=currCh;
                        	currCh = GetNextChar();
            
                	}//while
            	}//if
        	}//if
        }//while
    	
    	
        result.lexeme = str; //set lex
        if(flt) {//float symbol add
        	saveSymbols.AddSymbol(checkTruncate(result).lexeme, 'c', Double.parseDouble(result.lexeme));
        }else {//int symbol add
        	saveSymbols.AddSymbol(checkTruncate(result).lexeme, 'c', Integer.parseInt(result.lexeme));
        }
        
        return result;//return result

    }

    private token getString() {
    	token result = new token();
    	String str = "";
    	currCh = GetNextChar();
    	
        while(!isStringStart(currCh)) {  //read till string end
        	str+=currCh;
        	currCh = GetNextChar();
        	//System.out.println(currCh);
        	if(currCh == '\n') {//failed string
        		System.out.println("String not finished before the line ends");
        		result.lexeme = "Failed string";//dont put in failed string
                result.code = 53;
                result.mnemonic = "SFAIL";
                return result;//return failed reult
        	}
        }
        currCh = GetNextChar();
        result.lexeme = str;
        result.code = 53;
        result.mnemonic = "STRNG";

        saveSymbols.AddSymbol(checkTruncate(result).lexeme, 'c', result.lexeme);//add symbol
        return result;//return result
		
    }

    private token getOneTwoChar() {
    	token result = new token();
    	
    	//use switch to check pretty basic logic
    	switch(currCh) {
    	case '/':
    		result.lexeme = "/";
    		result.code = 30;
    		result.mnemonic = "DIVID";
    		currCh = GetNextChar();
    		break;
    		
    	case '*':
    		result.lexeme = "*";
    		result.code = 31;
    		result.mnemonic = "MULTI";
    		currCh = GetNextChar();
    		break;
    		
    	case '+':
    		result.lexeme = "+";
    		result.code = 32;
    		result.mnemonic = "ADDOP";
    		currCh = GetNextChar();
    		break;

    	case '-':
    		result.lexeme = "-";
    		result.code = 33;
    		result.mnemonic = "SUBTR";
    		currCh = GetNextChar();
    		break;

    	case '(':
    		result.lexeme = "(";
    		result.code = 34;
    		result.mnemonic = "LTPAR";
    		currCh = GetNextChar();
    		break;

    	case ')':
    		result.lexeme = ")";
    		result.code = 35;
    		result.mnemonic = "RTPAR";
    		currCh = GetNextChar();
    		break;

    	case ';':
    		result.lexeme = ";";
    		result.code = 36;
    		result.mnemonic = "SEMIC";
    		currCh = GetNextChar();
    		break;

    	case ':':
    		currCh = GetNextChar();
    		
    		if(currCh == '=') {
    			currCh = GetNextChar();
    			result.lexeme = ":=";
        		result.code = 37;
        		result.mnemonic = "ASSGN";
        		break;
    		}
    		result.lexeme = ":";
    		result.code = 47;
    		result.mnemonic = "COLON";
    		break;
    		
    	case '<'://DO THING
    		currCh = GetNextChar();
    		if(currCh == '=') {
    			currCh = GetNextChar();
    			result.lexeme = "<=";
        		result.code = 41;
        		result.mnemonic = "LSSEQ";
        		break;
    		}else if(currCh == '>') {
    			currCh = GetNextChar();
    			result.lexeme = "<>";
        		result.code = 43;
        		result.mnemonic = "DIMND";
        		break;
    		}
    		result.lexeme = "<";
    		result.code = 39;
    		result.mnemonic = "LTHAN";
    		break;
    		
    	case '>':
    		currCh = GetNextChar();
    		if(currCh == '=') {
    			currCh = GetNextChar();
    			result.lexeme = ">=";
        		result.code = 40;
        		result.mnemonic = "GRTEQ";
        		break;
    		}
    		result.lexeme = ">";
    		result.code = 38;
    		result.mnemonic = "GTHAN";
    		break;
    		
    	
    	case '=':
    		//currCh = skipWhiteSpace(); 
    		currCh = GetNextChar();
    		result.lexeme = "=";
    		result.code = 42;
    		result.mnemonic = "EQUAL";
    		break;
    		
    	case ',':
    		currCh = GetNextChar();
    		result.lexeme = ",";
    		result.code = 44;
    		result.mnemonic = "COMMA";
    		break;
    	
    	case '[':
    		currCh = GetNextChar();
    		result.lexeme = "[";
    		result.code = 45;
    		result.mnemonic = "LBRAK";
    		break;
    	
    	case ']':
    		currCh = GetNextChar();
    		result.lexeme = "]";
    		result.code = 46;
    		result.mnemonic = "RBRAK";
    		break;
    		
    	case '.':
    		currCh =  GetNextChar();
    		result.lexeme = ".";
    		result.code = 48;
    		result.mnemonic = "PRIOD";
    		break;
    	
    		default://unknown case as default
    			result.lexeme = "" + currCh;
    			currCh = GetNextChar();
    			result.code = 99;
        		result.mnemonic = "UNKWN";
        		break;
    	}
    	
        return result;//return result
    }

    public token checkTruncate(token result) {
        // truncate long lexemes, validate doubles and integers
        // handle appropriate types and add to symbol table as needed
        switch (result.code) {
            case IDENT_ID:
            	result.lexeme = (result.lexeme.length() > 20) ? result.lexeme.substring(0,20) : result.lexeme ;//truncate if too long
                break;
            case INTEGER_ID:
            	result.lexeme = (result.lexeme.length() > 6) ? result.lexeme.substring(0,6) : result.lexeme ;//truncate if too long
            	if(integerOK(result.lexeme)) {
            		break;
            	}
            	System.out.println("INVALID INTEGER");
                break;
            case FLOAT_ID:
            	result.lexeme = (result.lexeme.length() > 12) ? result.lexeme.substring(0,12) : result.lexeme ;//truncate if too long
            	if(doubleOK(result.lexeme)) {
            		break;
            	}
            	System.out.println("INVALID DOUBLE");
                break;
            case STRING_ID:
            	
                break;
            default:
                break; //don't add                           
        }

        return result;
    }

// End of student supplied methods
//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
// numberic validation freebie code!
    // Checks to see if a string contains a valid DOUBLE 
    public boolean doubleOK(String stin) {
        boolean result;
        Double x;
        try {
            x = Double.parseDouble(stin);
            result = true;
        } catch (NumberFormatException ex) {
            result = false;
        }
        return result;
    }

    // Checks the input string for a valid INTEGER
    public boolean integerOK(String stin) {
        boolean result;
        int x;
        try {
            x = Integer.parseInt(stin);
            result = true;
        } catch (NumberFormatException ex) {
            result = false;
        }
        return result;
    }

// Main method of Lexical
    public token GetNextToken() {
        token result = new token();
        //System.out.println("Should be token");
        currCh = skipWhiteSpace();
        if (isLetter(currCh)) { //is ident
            result = getIdent();
        } else if (isDigit(currCh)) { //is numeric
            result = getNumber();
        } else if (isStringStart(currCh)) { //string literal
            result = getString();
        } else //default char checks
        {
            result = getOneTwoChar();
        }

        if ((result.lexeme.equals("")) || (EOF)) {
        	System.out.println("made null cuz");
            result = null;
        }
//set the mnemonic
        if (result != null) {
//result.mnemonic = mnemonics.LookupCode(result.code);       //THIS LINE
            result = checkTruncate(result);
            if (printToken) {
                System.out.println("\t" + result.mnemonic + " | \t" + String.format("%04d", result.code) + " | \t" + result.lexeme);
            }
        }
        return result;

    }

}
