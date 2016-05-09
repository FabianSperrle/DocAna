package stemmer.tests;

import static org.junit.Assert.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import stemmer.KehlbeckSperrleStemmer;
import stemmer.Stemmer;

public class KehlbeckSperrleStemmerTest extends KehlbeckSperrleStemmer {

	private final Logger logger = LogManager.getLogger(KehlbeckSperrleStemmerTest.class);

	@Test
	public void measureZero() {
		Stemmer stemmer = new KehlbeckSperrleStemmer();
		String[] testWords = { "TO", "CNN" };

		for (String token : testWords) {
			assertEquals(token + " should have measure 0", 0, stemmer.getMeasure(token));
		}
	}

	@Test
	public void measureOne() {
		Stemmer stemmer = new KehlbeckSperrleStemmer();
		String[] testWords = { "or", "brick" };

		for (String token : testWords) {
			assertEquals(token + " should have measure 1", 1, stemmer.getMeasure(token));
		}
	}

	@Test
	public void measureTwo() {
		Stemmer stemmer = new KehlbeckSperrleStemmer();
		String[] testWords = { "eastern", "dogmas" };

		for (String token : testWords) {
			assertEquals(token + " should have measure 2", 2, stemmer.getMeasure(token));
		}
	}

	@Test
	public void stem() {
		Stemmer stemmer = new KehlbeckSperrleStemmer();
		Pair[] testWords = { new Pair("alienation", "alienat"), 
				new Pair("words", "word"),
				new Pair("nation", "nation"), 
				new Pair("bus", "bus"),
				new Pair("signed", "sign"),
				new Pair("bed", "bed"),
				new Pair("beds", "bed"),
				new Pair("running", "run"),
				new Pair("skulls", "skull"),
			};

		for (Pair pair : testWords) {
			assertEquals(pair.getValue() + "'s stem should be " + pair.getExpectedResult(), pair.getExpectedResult(),
					stemmer.stem(pair.getValue()));
		}
	}
}
