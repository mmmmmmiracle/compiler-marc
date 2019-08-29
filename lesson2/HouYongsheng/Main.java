import java.util.*;
import java.io.*;
import java_cup.runtime.*;  // defines the class Symbol
import lexer.*;
import parser.*;

/**
 * This program is to be used to test the Scanner.
 */
public class Main {
    public static void main(String[] args) throws IOException {

        java.util.Scanner console = new java.util.Scanner(System.in);
        String stop = "stop";
        String filename = readFileName(console,stop);
        while ( ! filename.equals(stop) ) {
            testAllTokens(filename,filename + ".out");
            System.out.println("result of scanner are in " + filename + ".out");
            // CharNum.num = 1;
            filename = readFileName(console,stop);
        }
    }

    /**
     * Open and read from file 'infile'.
     * For each token read, write the corresponding string to 'outfile'
     */
    private static void testAllTokens(String infile, String outfile) throws IOException {
        // open input and output files
        FileReader inFile = null;
        PrintWriter outFile = null;
        try {
            inFile = new FileReader(infile);
            outFile = new PrintWriter(new FileWriter(outfile));
        } catch (FileNotFoundException ex) {
            System.err.println("File " + infile + " not found.");
            System.exit(-1);
        } catch (IOException ex) {
            System.err.println(outfile + " cannot be opened.");
            System.exit(-1);
        }

        // create and call the scanner
        Yylex scanner = new Yylex(inFile);
        Symbol token = scanner.next_token();
        while (token.sym != sym.EOF) {
            switch (token.sym) {
            case sym.BOOL:
                outFile.println("bool"); 
                break;
			case sym.INT:
                outFile.println("int");
                break;
            case sym.VOID:
                outFile.println("void");
                break;
            case sym.TRUE:
                outFile.println("true"); 
                break;
            case sym.FALSE:
                outFile.println("false"); 
                break;
            case sym.STRUCT:
                outFile.println("struct"); 
                break;
            case sym.CIN:
                outFile.println("cin"); 
                break;
            case sym.COUT:
                outFile.println("cout");
                break;				
            case sym.IF:
                outFile.println("if");
                break;
            case sym.ELSE:
                outFile.println("else");
                break;
            case sym.WHILE:
                outFile.println("while");
                break;
            case sym.RETURN:
                outFile.println("return");
                break;
            case sym.ID:
                outFile.println(((IdTokenVal)token.value).idVal);
                break;
            case sym.INTLITERAL:  
                outFile.println(((IntLitTokenVal)token.value).intVal);
                break;
            case sym.STRINGLITERAL: 
                outFile.println(((StrLitTokenVal)token.value).strVal);
                break;    
            case sym.LCURLY:
                outFile.println("{");
                break;
            case sym.RCURLY:
                outFile.println("}");
                break;
            case sym.LPAREN:
                outFile.println("(");
                break;
            case sym.RPAREN:
                outFile.println(")");
                break;
            case sym.SEMICOLON:
                outFile.println(";");
                break;
            case sym.COMMA:
                outFile.println(",");
                break;
            case sym.DOT:
                outFile.println(".");
                break;
            case sym.WRITE:
                outFile.println("<<");
                break;
            case sym.READ:
                outFile.println(">>");
                break;				
            case sym.PLUSPLUS:
                outFile.println("++");
                break;
            case sym.MINUSMINUS:
                outFile.println("--");
                break;	
            case sym.PLUS:
                outFile.println("+");
                break;
            case sym.MINUS:
                outFile.println("-");
                break;
            case sym.TIMES:
                outFile.println("*");
                break;
            case sym.DIVIDE:
                outFile.println("/");
                break;
            case sym.NOT:
                outFile.println("!");
                break;
            case sym.AND:
                outFile.println("&&");
                break;
            case sym.OR:
                outFile.println("||");
                break;
            case sym.EQUALS:
                outFile.println("==");
                break;
            case sym.NOTEQUALS:
                outFile.println("!=");
                break;
            case sym.LESS:
                outFile.println("<");
                break;
            case sym.GREATER:
                outFile.println(">");
                break;
            case sym.LESSEQ:
                outFile.println("<=");
                break;
            case sym.GREATEREQ:
                outFile.println(">=");
                break;
            case sym.ASSIGN:
                outFile.println("=");
                break;
            default:
                outFile.println("UNKNOWN TOKEN");
            } // end switch

            token = scanner.next_token();
        } // end while
        outFile.close();
    }

    /**
     * To read a valid input file name or the keyword 'stop' from the user
     */
    private static String readFileName(java.util.Scanner input, String stop) {
        String filename = null;
        do {
            System.out.print("file name ('stop' to exit)? ");
            filename = input.nextLine().trim();
            if ( ! filename.equals(stop) ) {
                File file = new File(filename);	
                if ( ! file.exists() ) {
                    System.out.println(filename + " not found");
                    filename = null;
                }
            }
        } while ( filename == null );
        return filename;
    }
}
