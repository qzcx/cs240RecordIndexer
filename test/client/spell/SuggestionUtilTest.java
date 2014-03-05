package client.spell;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class SuggestionUtilTest {

	private Set<String> dict;
	
	@Before
	public void Before(){
		dict = new HashSet<String>();
		//testing bob
		//edit distance 1
		dict.add("boa".toUpperCase());
		dict.add("bo".toUpperCase());
		dict.add("bobb".toUpperCase());
		dict.add("bbo".toUpperCase());
		//edit distance 2
		dict.add("boab".toUpperCase());
		dict.add("b".toUpperCase());
		dict.add("bobbb".toUpperCase());
		dict.add("bbom".toUpperCase());
		
		//not to be found
		dict.add("asdfasfas".toUpperCase());
		dict.add("wsgsghhrhrh".toUpperCase());
		dict.add("bobbbb".toUpperCase());
		dict.add("sdfggsdgsdg".toUpperCase());
	}
	
	@Test
	public void test() {
		Set<String> test = SuggestionUtil.generateEditDistanceTwo("BOB", dict);
		int size  = test.size();
		//System.out.println(dict);
		//System.out.println(test);
		//System.out.println(size);
		assertTrue(size == 8);
		
		
	}

}
