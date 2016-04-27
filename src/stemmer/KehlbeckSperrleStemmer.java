package stemmer;

public class KehlbeckSperrleStemmer {

	public String stem(String token) {
		return token;
	}

	public int numberOfSyllabels(String token){
		char[] word = token.toCharArray();
		boolean charIsVowel = false;
		int syllableCount = 0;
		for (char c : word) {
			switch (c) {
			case 'a':
			case 'e':
			case 'i':
			case 'o':
			case 'u':
			case 'A':
			case 'E':
			case 'I':
			case 'O':
			case 'U':
				charIsVowel = true;
				break;
			default:
				if (charIsVowel) {
					syllableCount++;
					charIsVowel = false;
				}
				break;
			}
		}
		return syllableCount;
	}
}
