package stemmer;

public interface Stemmer {

	public String stem(String token);

	public int getMeasure(String token);

}