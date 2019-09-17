import java.io.*;
import java_cup.runtime.*;
import lexer.*;
import ast.*;
import parser.*;

/**
 * Main program to do the name analysis of C-- language.
 *
 * The program reads a in-file, creates a scanner and a parser, and
 * calls the parser.  If the parse is successful, the AST is unparsed
 * in a corresponding out-file
 */

public class Main {
    public static void main(String[] args) throws IOException { // may be thrown by the scanner
        java.util.Scanner console = new java.util.Scanner(System.in);
        String stop = "stop";
        String filename = readFileName(console,stop);
        
        while ( ! filename.equals(stop) ) {
            FileReader inFile = null;
            try {
                inFile = new FileReader(filename);
            } catch (FileNotFoundException ex) {
                System.err.println("Error while reading " + filename);
                System.exit(-1);
            }

            PrintWriter outFile = null;
            try {
                outFile = new PrintWriter(filename + ".out");
            } catch (FileNotFoundException ex) {
                System.err.println("File " + filename + ".out could not be opened for writing.");
                System.exit(-1);
            }
            try {
                processInputFile(inFile,outFile);
            }
            catch (SyntaxErrorException see) {
                System.out.println("syntax error: parsing aborted");
            }
            inFile.close();
            outFile.close();
            filename = readFileName(console,stop);
        }
    }

    private static void processInputFile(FileReader inFile, PrintWriter outFile) {
        CmmParser P = new CmmParser(new Yylex(inFile));
        Symbol root = null; // the parser will return a Symbol whose value
                            // field is the translation of the root nonterminal
                            // (i.e., of the nonterminal "program")
        try {
            root = P.parse(); // do the parse
            System.out.println ("program parsed correctly.");
        } catch (SyntaxErrorException see) {
            throw see;
        } catch (Exception ex){
            System.err.println("Exception occured during parse: " + ex);
            System.exit(-1);
        }
        AST.ProgramNode astRoot = (AST.ProgramNode) root.value;
        astRoot.nameAnalysis();  // perform name analysis
        astRoot.typeCheck();     // type checking
        astRoot.unparse(outFile, 0); // perform the unparsing
    }
    
    /**
     * To read a valid input file name or the keyword 'stop' from the user
     */
    private static String readFileName(java.util.Scanner input, String stop) {
        String filename = null;
        do {
            System.out.print("file name? ");
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
