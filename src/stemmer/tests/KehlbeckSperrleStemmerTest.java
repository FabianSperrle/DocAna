package stemmer.tests;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import stemmer.KehlbeckSperrleStemmer;
import stemmer.Stemmer;

import static org.junit.Assert.assertEquals;

public class KehlbeckSperrleStemmerTest extends KehlbeckSperrleStemmer {

	private final Logger logger = LogManager.getLogger(KehlbeckSperrleStemmerTest.class);

	@Test
	public void stem() {
		Stemmer stemmer = new KehlbeckSperrleStemmer();
		Pair[] testWords = {
				new Pair("words", "word"),
				new Pair("nation", "nation"), 
				new Pair("bus", "bus"),
				new Pair("signed", "sign"),
				new Pair("bed", "bed"),
				new Pair("beds", "bed"),
				new Pair("running", "run"),
				new Pair("skulls", "skull"),
				new Pair("ties", "ties"),
				new Pair("ponies", "poni"),
				new Pair("computation", "comput"),
				new Pair("computer", "comput"),
				new Pair("compute", "comput"),
				new Pair("computationally", "comput"),
			};

		for (Pair pair : testWords) {
			assertEquals(pair.getValue() + "'s stem should be " + pair.getExpectedResult(), pair.getExpectedResult(),
					stemmer.stem(pair.getValue()));
		}
	}
}
