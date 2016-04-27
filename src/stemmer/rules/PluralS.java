package stemmer.rules;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PluralS implements Rule {
	
	private final String NAME = "PluralS";
	private final Logger logger = LogManager.getLogger(PluralS.class);

	/**
	 * {@inheritDoc }
	 */
	@Override
	public boolean isApplicable(String token, int syllables) {
		if (token.endsWith("s")) {
			
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
		return token.substring(0, token.length()-1);
	}

	@Override
	public String getName() {
		return this.NAME;
	}

}
