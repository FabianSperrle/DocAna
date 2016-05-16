package stemmer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class KehlbeckSperrleStemmer implements Stemmer  {
	private final Logger logger = LogManager.getLogger(KehlbeckSperrleStemmer.class);
	
	
	/* (non-Javadoc)
	 * @see stemmer.Stemmer#stem(java.lang.String)
	 */
	@Override
	public String stem(final String token) {
		String current = token;
		String previous = "";

		logger.debug("==========================");
		logger.debug("Starting to stem {}", token);

		while (!current.equals(previous)) {
			final int measure = this.getMeasure(current);
			boolean appliedAnyRule = false;

			logger.debug("Current measure is {}.", measure);

			for (Rules RULE : Rules.values()) {
				
				logger.debug("Cheking next rule {}", RULE);
				
				try {
					if (RULE.isApplicable(current)) {
						previous = current;
						current = RULE.apply(current);
						appliedAnyRule = true;
						break;
					} 
				} catch (StringIndexOutOfBoundsException e) {
					continue;
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
	public int getMeasure(final String token){
		final char[] word = token.toCharArray();
		boolean charIsVowel = false;
		int measure = 0;
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
					measure++;
					charIsVowel = false;
				}
				break;
			}
		}
		return measure;
	}
}
