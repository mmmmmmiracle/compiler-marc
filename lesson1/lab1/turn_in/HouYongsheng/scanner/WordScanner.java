package scanner;

import java.io.*;


/**
 * This class implements a word (string) scanner
 */
public class WordScanner {

	/**
	 * Builds a WordScanner object based on the given input
	 */
	private BufferedReader textBufferReader;
	private String lineContent;
	private int rowNumber = 0;
	private int colNumber = 0;
	// private String stopCharacters = " ~`!@#$%^&*()<>?/.,;:\"";

	public WordScanner(FileReader input) throws IOException {
		this.textBufferReader = new BufferedReader(input);
		this.lineContent = this.textBufferReader.readLine();
//		System.out.println(this.lineContent);
	}
	
	/**
	 * Returns the next word from input
	 * Precond: there must be at least
	 * one word left in the input
	 * (i.e. hasNextWord() must evaluate to true)
	 */
	public Word nextWord() throws IOException {
//		System.out.println(this.hasNextWord());	
		if(this.hasNextWord()){
			int stopColNumber = this.colNumber;
			for(int i = this.colNumber; i < this.lineContent.length(); i++){
				char c = this.lineContent.charAt(i);
				if(!this.isLetter(c)){
					if(c == '\'' && i != this.lineContent.length()-1 && this.isLetter(this.lineContent.charAt(i+1))) {
						continue;
					}
					stopColNumber = i ;
					break;
				}else {
					if(i == this.lineContent.length()-1) {
						stopColNumber = this.lineContent.length();
					}
				}
			}
			String wordString = this.lineContent.substring(this.colNumber,stopColNumber);
			Info info = new Info(this.rowNumber+1,this.colNumber+1);
			// System.out.print(wordString);
			// System.in.read();   //for debugging
			// System.out.println(info.toString());
			// System.in.read();
			this.colNumber = stopColNumber;
			return new Word(wordString,info);
		}
		return null;
	}

	/**
	 * Returns true if there is at least
	 * one word left in the input, false otherwise
	 * @throws IOException 
	 */
	public boolean hasNextWord() throws IOException {
		this.moveToNextWord();
//		System.out.println(this.colNumber);
		return this.lineContent != null;
	}
	
	private void moveToNextWord() throws IOException {	
		while(this.colNumber <= this.lineContent.length()) {
			if(this.colNumber == this.lineContent.length()) {
				this.lineContent = this.textBufferReader.readLine();
				if(this.lineContent != null) {
					this.colNumber = 0;
					this.rowNumber++ ;
				}else {
					return;
				}
			}else{
				if(!this.isLetter(this.lineContent.charAt(this.colNumber)))
					this.colNumber++ ;
				else
					return;
			}
		}
		// if(this.colNumber < this.lineContent.length()) {
		// 	if(!this.isLetter(this.lineContent.charAt(this.colNumber))) {
		// 		this.colNumber ++;
		// 		this.moveToNextWord();
		// 	}else {
		// 		return;
		// 	}
		// }else{
		// 	this.lineContent = this.textBufferReader.readLine();
		// 	System.out.println(this.lineContent);
		// 	if(this.lineContent != null) {
		// 		this.rowNumber += 1;
		// 		this.colNumber = 0;
		// 		this.moveToNextWord();
		// 	}else {
		// 		return;
		// 	}
		// }
	}
	
	private boolean isLetter(char c) {
		return (c >='a' &&  c <= 'z') || (c >= 'A' && c <= 'Z');
	}	

	// public static void main(String[] args) {
	// 	File file = new File("../files/text1.txt");
	// 	FileReader fileReader = new FileReader(file);
	// 	WordScanner wordscanner = new WordScanner(fileReader);
	// }
}
