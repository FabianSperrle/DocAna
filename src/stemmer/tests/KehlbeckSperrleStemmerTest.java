package stemmer.tests;

import static org.junit.Assert.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import stemmer.KehlbeckSperrleStemmer;

public class KehlbeckSperrleStemmerTest extends KehlbeckSperrleStemmer {
	
	private final Logger logger = LogManager.getLogger(KehlbeckSperrleStemmerTest.class);

	@Test
	public void zeroSyllables() {
		KehlbeckSperrleStemmer stemmer = new KehlbeckSperrleStemmer();
		String[] testWords = { "TO", "CNN" };

		for (String token : testWords) {
			assertEquals(token + " should have 0 syllables", 0, stemmer.numberOfSyllabels(token));
		}
	}

	@Test
	public void oneSyllable() {
		KehlbeckSperrleStemmer stemmer = new KehlbeckSperrleStemmer();
		String[] testWords = { "or", "brick" };

		for (String token : testWords) {
			assertEquals(token + " should have 1 syllable", 1, stemmer.numberOfSyllabels(token));
		}
	}

	@Test
	public void twoSyllables() {
		KehlbeckSperrleStemmer stemmer = new KehlbeckSperrleStemmer();
		String[] testWords = { "eastern", "dogmas" };

		for (String token : testWords) {
			assertEquals(token + " should have 2 syllables", 2, stemmer.numberOfSyllabels(token));
		}
	}

	@Test
	public void stem() {
		KehlbeckSperrleStemmer stemmer = new KehlbeckSperrleStemmer();
		Pair[] testWords = { new Pair("alienation", "alienate"), new Pair("words", "word") };

		for (Pair pair : testWords) {
			assertEquals(pair.getValue() + "'s stem should be " + pair.getExpectedResult(), pair.getExpectedResult(),
					stemmer.stem(pair.getValue()));
		}
	}
}
