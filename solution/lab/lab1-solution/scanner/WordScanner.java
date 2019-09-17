package scanner;

import java.io.*;

/**
 * This class implements a word (string) scanner
 */
public class WordScanner {

	private BufferedReader input; // the text file
	private String line; // the current line
	private int charPos; // the current position in the line
	private int lineNum; // the number of the line
	
	/**
	 * Builds a WordScanner object based on the given input
	 */
	public WordScanner(FileReader input) throws IOException {
		this.input = new BufferedReader(input);
		this.line = this.input.readLine();
		this.charPos = 0;
		this.lineNum = 1;
		moveToNextWord();
	}
	
	/**
	 * Returns the next word from input
	 * Precond: there must be at least
	 * one word left in the input
	 * (i.e. hasNextWord() must evaluate to true)
	 */
	public Word nextWord() throws IOException {
		int start = charPos++;
		boolean inWord = true;
		
		while ( charPos < line.length() && inWord ) {
			char c = line.charAt(charPos);
			if ( isLetter(c) || ( c == '\'' && charPos + 1 < line.length() && isLetter(line.charAt(charPos+1) ) ) )
				charPos++;
			else
				inWord = false;
		}
		Word w = new Word(line.substring(start, charPos),new Info(start,lineNum));
		moveToNextWord();
		return w;
	}
	
	/**
	 * Returns true if there is at least
	 * one word left in the input, false otherwise
	 */
	public boolean hasNextWord() {
		return line != null;
	}
	
	private void moveToNextWord() throws IOException {
		boolean noLetter = true;
		while ( line != null && noLetter ) {
			if ( charPos == line.length() ) {
				line = input.readLine();
				charPos = 0;
				lineNum++;
			}
			else if ( ! isLetter(line.charAt(charPos)) )
				charPos++;
			else
				noLetter = false;
		}
	}
	
	private boolean isLetter(char c) {
		return 'a' <= c && c <= 'z' || 'A' <= c && c <= 'Z';
	}	
}
