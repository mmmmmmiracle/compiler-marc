package scanner;

import java.util.*;

/**
 * A class for the WordCount data structure.
 * A WordCount object is a map which pairs a word (string)
 * with a list of information (Info)
 */
public class WordCount {
	private Map<String, List<Info>> map;
	/**
	 * Builds an empty WordCount
	 */
	public WordCount() {
		map = new TreeMap<String, List<Info>>();
	}
	
	/**
	 * Adds the given 'info' in the list of
	 * Infos of the given word 'word'
	 */
	public void add(String word, Info info) {
		if(!map.containsKey(word)) 
			map.put(word, new ArrayList<Info>());
		map.get(word).add(info);
	}
	
	/**
	 * Returns an iterator over the informations of
	 * the given word 'word'. If 'word' has no information
	 * returns null
	 */
	public Iterator<Info> getListIterator(String word) {
		if(!map.containsKey(word))
			return null;
		return map.get(word).iterator();
	}
	
	/**
	 * Displays the WordCount on System.out
	 */
	public void display() {
		Iterator<String> iterator = map.keySet().iterator();
		while (iterator.hasNext()) {
		    String word = iterator.next();
		    System.out.print(word + " (" + map.get(word).size() + "): ");
		    Iterator<Info> iter = getListIterator(word);
		    while(iter.hasNext()) {
		    	System.out.print(" " + iter.next());
		    }
		    System.out.println();
		}
	}
}
