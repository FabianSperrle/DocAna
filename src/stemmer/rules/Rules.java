package stemmer.rules;

import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import stemmer.KehlbeckSperrleStemmer;
import stemmer.Stemmer;

public enum Rules {
	PluralS(1,
			t -> t.endsWith("s"),
			t -> t.substring(0, t.length() - 1),
			t -> t),
	AtionAte(1,
			t -> t.endsWith("ation"),
			t -> t.substring(0, t.length() - 5),
			t -> t + "ate"),
	Ed_(1,
			t -> t.endsWith("ed"),
			t -> t.substring(0, t.length() - 2),
			t -> t);

	private Rules(int minimumRemainingMeasure, Function<String, Boolean> correctEnding,
			Function<String, String> reducer, Function<String, String> appender) {
		this.minimumRemainigMeasure = minimumRemainingMeasure;
		this.correctEnding = correctEnding;
		this.reducer = reducer;
		this.appender = appender;
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
