package stemmer.rules;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import stemmer.Stemmer;

public class PluralS implements Rule {
	
	private final String NAME = "PluralS";
	private final Logger logger = LogManager.getLogger(PluralS.class);

	/**
	 * {@inheritDoc }
	 */
	@Override
	public boolean isApplicable(String token, Stemmer stemmer) {
		String remainingStem = token.substring(0, token.length()-1);
		
		if (token.endsWith("s") && stemmer.getMeasure(remainingStem) > 0) {
			
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
		
		logger.debug("Apply rule {}", this.getName());
		
		return token.substring(0, token.length()-1);
	}

	@Override
	public String getName() {
		return this.NAME;
	}

}
