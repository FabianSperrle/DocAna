package stemmer.rules;

import stemmer.Stemmer;

public interface Rule {
	
	/**
	 * Returns the name of the rule.
	 * @return the name
	 */
	public String getName();
	
	/**
	 * Tests whether a rule can be applied on a given token.
	 * The result is determinded by the correct number of syllables
	 * and the correct ending sequence. 
	 * 
	 * @param token
	 * @param syllables
	 * @return true iff the rule is applicable
	 */
	public boolean isApplicable(String token, Stemmer stemmer);
	
	/**
	 * Applies the rule to the token.
	 * 
	 * @param token
	 * @return the resulting string
	 */
	public String apply(String token);
}
