package scanner;

import java.util.*;

/**
 * A class for the WordCount data structure.
 * A WordCount object is a map which pairs a word (string)
 * with a list of information (Info)
 */
public class WordCount {

	private Map<String,List<Info>> table;
	
	/**
	 * Builds an empty WordCount
	 */
	public WordCount() {
		table = new TreeMap<String,List<Info>>();
		// or HashMap<String,List<Info>>()
	}
	
	/**
	 * Adds the given 'info' in the list of
	 * Infos of the given word 'word'
	 */
	public void add(String word, Info info) {
		List<Info> list = table.get(word);
		if ( list == null ) {
			list = new LinkedList<Info>();
			table.put(word, list);
		}
		list.add(info);
	}
	
	/**
	 * Returns an iterator over the informations of
	 * the given word 'word'. If 'word' has no information
	 * returns null
	 */
	public Iterator<Info> getListIterator(String word) {
		List<Info> list = table.get(word);
		if ( list == null )
			return null;
		return list.iterator();
	}
	
	/**
	 * Displays the WordCount on System.out
	 */
	public void display() {
		for ( String word : table.keySet() ) {
			List<Info> list = table.get(word);
			System.out.printf("%10s (%d): ", word,list.size());
			for ( Info info : list ) {
				System.out.print(info + " ");
			}
			System.out.println();
		}
	}
}
