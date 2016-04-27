package stemmer;

public interface Stemmer {

	public String stem(String token);

	public int numberOfSyllabels(String token);

}