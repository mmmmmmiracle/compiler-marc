package scanner;

/**
 * A class for Word
 */
public class Word {

	/**
	 * Builds a Word object with the actual
	 * word (string) 'word' and the information 'info'
	 */
	 private String word;
	 private Info info ;

	public Word(String word, Info info) {
		this.word = word;
		this.info = info;
	}
	
	/**
	 * Returns the actual word (string)
	 * of this Word
	 */
	public String getWord() {
		return this.word;
	}
	
	/**
	 * Returns the information (Info)
	 * of this Word
	 */
	public Info getInfo() {
		return this.info;
	}
	
	/**
	 * Returns a String representation
	 * of this Word
	 * (for testing/debugging only)
	 */
	public String toString() {
		return "word: " + this.word + ", info: " + this.info.toString();
	} 
}
