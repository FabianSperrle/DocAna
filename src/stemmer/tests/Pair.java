package stemmer.tests;

public class Pair {
	
	private String value;
	private String expectedResult;
	
	public Pair(String value, String expectedResult) {
		this.value = value;
		this.expectedResult = expectedResult;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the expectedResult
	 */
	public String getExpectedResult() {
		return expectedResult;
	}

	/**
	 * @param expectedResult the expectedResult to set
	 */
	public void setExpectedResult(String expectedResult) {
		this.expectedResult = expectedResult;
	}
}
