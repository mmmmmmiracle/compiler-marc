
import java.util.*;
import java.io.*;
import scanner.*;

/**
 * Main class for testing. You should not change this
 * class except the the name of the input file
 */
public class Main {

	/**
	 * The main method
	 */
	public static void main(String[] args) throws IOException, FileNotFoundException {
        java.util.Scanner console = new java.util.Scanner(System.in);
        String stop = "stop";
        String filename = readFileName(console,stop);
        while ( ! filename.equals(stop) ) {
           	processFile(filename);
            filename = readFileName(console,stop);
        }
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

    private static void processFile(String filepath) throws IOException, FileNotFoundException {
 		// the FileReader
		FileReader fr = new FileReader(filepath);
		// builds the scanner
		WordScanner scanner = new WordScanner(fr);
		// builds a word count
		WordCount counter = new WordCount();
		while ( scanner.hasNextWord() ) {
			Word w = scanner.nextWord();
			counter.add(w.getWord().toLowerCase(), w.getInfo());
		}
		// displays the word count
		counter.display();   	
    }
}
