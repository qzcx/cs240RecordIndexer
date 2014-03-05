package client.spell;

import java.util.HashSet;
import java.util.Set;

public class SuggestionUtil {
	
	public static Set<String> generateEditDistanceTwo(String inputWord, Set<String> dict){

		//get all distance 1 edits
		Set<String> round1 = generateWordList(inputWord.toUpperCase());
		//get all distance 2 edits
		Set<String> round2 = new HashSet<String>();
		for (String word :round1){
			round2.addAll(generateWordList(word));
		}
		
		round2.addAll(round1);
		//filter all words out that are not in the dictionary
		Set<String> filter = new HashSet<String>();
		for(String word: round2)
			if(dict.contains(word))
				filter.add(word);
		
		return filter;
		
	}
	
	
	/**
	 * Generates a set of words which have edit distance 1 away from the word given
	 * @param inputWord
	 * @param dict
	 * @return
	 */
	public static Set<String> generateWordList(String inputWord) {
		
		Set<String> retList = generateDeleteList(inputWord);
		retList.addAll(generateTranList(inputWord));
		retList.addAll(generateAltList(inputWord));
		retList.addAll(generateInsertList(inputWord));
		return retList;
	}

	private static Set<String> generateDeleteList(String inputWord){
		Set<String> ret = new HashSet<String>();
		for(int i=0; i< inputWord.length(); i++){
			String word = inputWord.substring(0,i) + inputWord.substring(i + 1);
			//if(dict.contains(word))
			ret.add(word);
		}
		return ret;
	}
	
	private static Set<String> generateTranList(String inputWord) {
		Set<String> ret = new HashSet<String>();
		char[] inputArray = inputWord.toCharArray();
		for(int i=0; i< inputWord.length() - 1; i++){
			char[] copy = inputArray.clone();
			copy[i] = inputArray[i+ 1];
			copy[i + 1] = inputArray[i];
			String word = String.copyValueOf(copy);
			//if(dict.contains(word))
			ret.add(word);
		}
		return ret;
	}
	
	private static Set<String> generateAltList(String inputWord) {
		Set<String> ret = new HashSet<String>();
		char[] inputArray = inputWord.toCharArray();
		for(int i=0; i< inputWord.length(); i++){
			for(int j=0; j < 26; j++){
				char temp = inputArray[i]; //hold the value
				inputArray[i] = Character.toChars('A' + j)[0];
				String word = String.copyValueOf(inputArray);
				//if(dict.contains(word))
				ret.add(word);
				inputArray[i] = temp;//replace the value to avoid copying i*j times
			}
		}
		return ret;
	}

	private static Set<String> generateInsertList(String inputWord) {
		Set<String> ret = new HashSet<String>();
		for(int i=0; i<= inputWord.length(); i++){
			for(int j=0; j < 26; j++){
				String word = inputWord.substring(0,i) + Character.toChars('A' + j)[0] + inputWord.substring(i);
				//if(dict.contains(word))
				ret.add(word);
			}
		}
		return ret;
	}
	
}
