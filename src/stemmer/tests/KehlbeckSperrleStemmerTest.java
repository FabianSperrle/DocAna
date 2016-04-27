package stemmer.tests;

import static org.junit.Assert.*;

import org.junit.Test;
import stemmer.KehlbeckSperrleStemmer;

public class KehlbeckSperrleStemmerTest extends KehlbeckSperrleStemmer {
	
	@Test
	public void zeroSyllables() {
		KehlbeckSperrleStemmer stemmer = new KehlbeckSperrleStemmer();
		String[] testWords = {"TO", "CNN"};
		
		for (String token : testWords) {
			assertEquals(token + " should have 0 syllables", 0, stemmer.numberOfSyllabels(token));
		}
	}
	
	@Test
	public void oneSyllable() {
		KehlbeckSperrleStemmer stemmer = new KehlbeckSperrleStemmer();
		String[] testWords = {"or", "brick"};
		
		for (String token : testWords) {
			assertEquals(token + " should have 1 syllable", 1, stemmer.numberOfSyllabels(token));
		}
	}

	@Test
	public void twoSyllables() {
		KehlbeckSperrleStemmer stemmer = new KehlbeckSperrleStemmer();
		String[] testWords = {"eastern", "dogmas"};
		
		for (String token : testWords) {
			assertEquals(token + " should have 2 syllables", 2, stemmer.numberOfSyllabels(token));
		}
	}
}
