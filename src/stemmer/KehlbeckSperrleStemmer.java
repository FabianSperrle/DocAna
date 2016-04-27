package stemmer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import stemmer.rules.*;

public class KehlbeckSperrleStemmer implements Stemmer  {
	private final Logger logger = LogManager.getLogger(KehlbeckSperrleStemmer.class);
	
	
	/**
	 * Array of rules that can be applied to stem a word.
	 * The order of the  rules is important! The first matching
	 * rule of the list will be applied, and the iteration through
	 * the list will be restarted. 
	 */
	private final Rule[] rules = {
			new PluralS(),
			new AtionAte(),
	};

	/* (non-Javadoc)
	 * @see stemmer.Stemmer#stem(java.lang.String)
	 */
	@Override
	public String stem(final String token) {
		String current = token;
		String previous = "";

		logger.debug("Starting to stem {}", token);

		while (!current.equals(previous)) {
			final int syllables = this.numberOfSyllabels(current);
			boolean appliedAnyRule = false;

			logger.debug("Currently there are {} syllables", syllables);

			for (Rule rule : rules) {
				
				logger.debug("Cheking next rule {}", rule.getName());
				
				if (rule.isApplicable(current, this)) {

					logger.debug("Apply rule {}", rule.getName());

					previous = current;
					current = rule.apply(current);
					appliedAnyRule = true;

					logger.debug("Stem was {} and is {} now", previous, current);

					break;
				} else {
					logger.debug("Rule {} is not applicable.", rule.getName());
				}
			}
			
			if (!appliedAnyRule) {
				
				logger.debug("No more rules applicable. Returning stem {}", current);

				break;
			}
		}

		return current;
	}

	/* (non-Javadoc)
	 * @see stemmer.Stemmer#numberOfSyllabels(java.lang.String)
	 */
	@Override
	public int numberOfSyllabels(final String token){
		final char[] word = token.toCharArray();
		boolean charIsVowel = false;
		int syllableCount = 0;
		for (char c : word) {
			switch (c) {
			case 'a':
			case 'e':
			case 'i':
			case 'o':
			case 'u':
			case 'A':
			case 'E':
			case 'I':
			case 'O':
			case 'U':
				charIsVowel = true;
				break;
			default:
				if (charIsVowel) {
					syllableCount++;
					charIsVowel = false;
				}
				break;
			}
		}
		return syllableCount;
	}
}
