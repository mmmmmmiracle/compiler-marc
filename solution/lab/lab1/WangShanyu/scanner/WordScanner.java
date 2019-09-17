package scanner;

import java.io.*;

/**
 * This class implements a word (string) scanner
 */
public class WordScanner {

	private BufferedReader br;
	private int lineNum;
	private int charPos;
	private boolean flag;
	private String line;
	/**
	 * Builds a WordScanner object based on the given input
	 */
	public WordScanner(FileReader input) throws IOException {
		br = new BufferedReader(input);
		line = br.readLine();
		lineNum = 1;
		charPos = 0;
		flag = true;
		moveToNextWord();
	}
	
	/**
	 * Returns the next word from input
	 * Precond: there must be at least
	 * one word left in the input
	 * (i.e. hasNextWord() must evaluate to true)
	 */
	public Word nextWord() throws IOException {
		Info info = new Info(charPos, lineNum);
		int end = charPos;
		for(; end<line.length(); end++) {
			char c = line.charAt(end);
			if(isLetter(c)) continue;
			if(c=='\'') {
				if(end+1<line.length() && isLetter(line.charAt(end+1)))
					continue;
				else
					break;
			}
			break;
		}
		String newWord = line.substring(charPos, end);
		charPos = end;
		moveToNextWord();
		return new Word(newWord, info);
	}
	
	/**
	 * Returns true if there is at least
	 * one word left in the input, false otherwise
	 * @throws IOException 
	 */
	public boolean hasNextWord(){
		return flag;
	}
	
	private void moveToNextWord() throws IOException {
		while(line!=null) {
			for(; charPos < line.length(); charPos++) {
				if(isLetter(line.charAt(charPos)))
					return;
			}
			line = br.readLine();
			lineNum++;
			charPos = 0;
		}
		flag = false;
	}
	
	private boolean isLetter(char c) {
		return (c>='a'&&c<='z')||(c>='A'&&c<='Z');
	}	
}
