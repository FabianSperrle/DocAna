package stemmer.rules;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import stemmer.Stemmer;

public class AtionAte implements Rule {
	private final Logger logger = LogManager.getLogger(AtionAte.class);

	private final String NAME = "AtionAte";

	/**
	 * {@inheritDoc }
	 */
	@Override
	public boolean isApplicable(String token, Stemmer stemmer) {
		// After removing 5 letters we need at least two left to have m>0
		if (token.length() < 7) {

			logger.debug("Rule {} is not applicable", this.getName());
			
			return false;
		}
		
		String remainingStem = token.substring(0, token.length() - 5);
		if (token.endsWith("ation") && stemmer.numberOfSyllabels(remainingStem) > 0) {

			logger.debug("Rule {} is applicable", this.getName());

			return true;

		} else {

			logger.debug("Rule {} is not applicable", this.getName());

			return false;
		}
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	public String apply(String token) {
		return token.substring(0, token.length() - 5) + "ate";
	}

	@Override
	public String getName() {
		return this.NAME;
	}

}
