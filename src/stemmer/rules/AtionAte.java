package stemmer.rules;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AtionAte implements Rule {
	private final Logger logger = LogManager.getLogger(AtionAte.class);

	private final String NAME = "AtionAte";

	/**
	 * {@inheritDoc }
	 */
	@Override
	public boolean isApplicable(String token, int syllables) {
		if (syllables > 0 && token.endsWith("ation")) {
			
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
