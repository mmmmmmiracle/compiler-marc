package scanner;

import java.util.*;


/**
 * A class for the WordCount data structure. A WordCount object is a map which
 * pairs a word (string) with a list of information (Info)
 */
public class WordCount {

	private Map<String, List<Info>> wordInfosMap;

	/**
	 * Builds an empty WordCount
	 */
	public WordCount() {
//		this.wordInfosMap = new HashMap<String, List<Info>>();
		this.wordInfosMap = new TreeMap<String, List<Info>>();
	}

	/**
	 * Adds the given 'info' in the list of Infos of the given word 'word'
	 */
	public void add(String word, Info info) {
		List<Info> curWordInfoList = new ArrayList<Info>();

		// check if current word occured before,
		// if occured, get it's infos stored history
		if (this.wordInfosMap.containsKey(word)) {
			curWordInfoList = this.wordInfosMap.get(word);
		}
		curWordInfoList.add(info);
		this.wordInfosMap.put(word, curWordInfoList);
	}

	/**
	 * Returns an iterator over the informations of the given word 'word'. If 'word'
	 * has no information returns null
	 */
	public Iterator<Info> getListIterator(String word) {
		if (this.wordInfosMap.containsKey(word)) {
			List<Info> curWordInfoList = this.wordInfosMap.get(word);
			Iterator<Info> it = curWordInfoList.iterator();
			return it;
		} else {
			return null;
		}
	}

	/**
	 * Displays the WordCount on System.out
	 */
	public void display() {
		if (this.wordInfosMap.isEmpty()) {
			System.out.println("No words counted!");
		} else {
			Set<String> wordsSet = this.wordInfosMap.keySet();
			Iterator<String> it = wordsSet.iterator();
			while (it.hasNext()) {
				String word = it.next();
				List<Info> wordInfoList = this.wordInfosMap.get(word);
				int wordFrequency = wordInfoList.size();
				System.out.print(word + " (" + wordFrequency + "): ");
				Iterator infoIterator = wordInfoList.iterator();
				while (infoIterator.hasNext()) {
					Info info = (Info) infoIterator.next();
					System.out.print(info.toString());
					System.out.print(" ");
				}
				System.out.println();
			}
		}
	}
}
