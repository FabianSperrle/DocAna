package stemmer;

import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum Rules {
	GenetiveS(1,
			t -> t.endsWith("'s"),
			t -> t.substring(0, t.length() - 2)),
	PluralS(1,
			t -> t.endsWith("s"),
			t -> t.substring(0, t.length() - 1)),
	SsesS(1,
			t -> t.endsWith("sses"),
			t -> t.substring(0, t.length() - 3)),
	AtionAte(1,
			t -> t.endsWith("ation"),
			t -> t.substring(0, t.length() - 5),
			t -> t + "ate"),
	Ify_(1,
			t -> t.endsWith("ify"),
			t -> t.substring(0, t.length() - 3)),
	Ment_(1,
			t -> t.endsWith("ment"),
			t -> t.substring(0, t.length() - 4)),
	GerundIng(1,
			t -> t.endsWith("ing"),
			t -> t.substring(0, t.length() - 3)),
	Al_(1,
			t -> t.endsWith("al"),
			t -> t.substring(0, t.length() - 2)),
	Ed_(1,
			t -> t.endsWith("ed"),
			t -> t.substring(0, t.length() - 2)),
	Ly_(1,
			t -> t.endsWith("ly"),
			t -> t.substring(0, t.length() - 2)),
	Y_(1,
			t -> t.endsWith("y"),
			t -> t.substring(0, t.length() - 1)),
	Ous_(1,
			t -> t.endsWith("ous"),
			t -> t.substring(0, t.length() - 3)),
	E_(1,
			t -> t.endsWith("e"),
			t -> t.substring(0, t.length() - 1)),
	DoubleConsonantEnding(1,
			t -> t.charAt(t.length() - 1) == t.charAt(t.length() - 2),
			t -> t.substring(0, t.length() - 1));

	/**
	 * Defines a stemming rule for the KehlbeckSperrleStemmer.
	 * 
	 * @param minimumRemainingMeasure
	 *            The minimum measure that the result of reducer(input) must
	 *            have
	 * @param correctEnding
	 *            A function that determines whether a rule is applicable by
	 *            checking whether the input ends with the correct substring
	 * @param reducer
	 *            A function that removes the ending of the word to obtain a
	 *            part of the stem. 
	 * @param appender
	 *            A function that appends a string to the generated stem.
	 */
	private Rules(int minimumRemainingMeasure, Function<String, Boolean> correctEnding,
			Function<String, String> reducer, Function<String, String> appender) {
		this.minimumRemainigMeasure = minimumRemainingMeasure;
		this.correctEnding = correctEnding;
		this.reducer = reducer;
		this.appender = appender;
	}

	/**
	 * Defines a stemming rule for the KehlbeckSperrleStemmer.
	 * 
	 * @param minimumRemainingMeasure
	 *            The minimum measure that the result of reducer(input) must
	 *            have
	 * @param correctEnding
	 *            A function that determines whether a rule is applicable by
	 *            checking whether the input ends with the correct substring
	 * @param reducer
	 *            A function that removes the ending of the word to obtain a
	 *            part of the stem. 
	 */
	private Rules(int minimumRemainingMeasure, Function<String, Boolean> correctEnding,
			Function<String, String> reducer) {
		this.minimumRemainigMeasure = minimumRemainingMeasure;
		this.correctEnding = correctEnding;
		this.reducer = reducer;
		this.appender = t -> t;
	}


	private int minimumRemainigMeasure;
	private Function<String, Boolean> correctEnding;
	private Function<String, String> reducer;
	private Function<String, String> appender;

	private final Stemmer stemmer = new KehlbeckSperrleStemmer();
	private final Logger logger = LogManager.getLogger(Rules.class);

	public boolean isApplicable(String token) {
		if (!correctEnding.apply(token)) {

			logger.debug("Rule {} is not applicable", this);

			return false;
		}

		try {
			String reduced = reducer.apply(token);
			if (this.stemmer.getMeasure(reduced) >= this.minimumRemainigMeasure) {

				logger.debug("Rule {} is applicable", this);

				return true;
			} else {

				logger.debug("Rule {} is not applicable", this);

				return false;
			}
		} catch (IndexOutOfBoundsException e) {

			logger.debug("Rule {} is not applicable", this);

			return false;
		}
	}

	public String apply(String token) {
		String reduced = this.reducer.apply(token);

		logger.debug("Applied rule {}. New stem is {}", this, reduced);

		return this.appender.apply(reduced);
	}

}
