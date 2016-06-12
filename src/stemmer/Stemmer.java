package stemmer;

public interface Stemmer {

	String stem(String token);

	String[] stem(String[] sentence);

	static int getMeasure(final String token) {
		final char[] word = token.toCharArray();
		boolean charIsVowel = false;
		int measure = 0;
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
						measure++;
						charIsVowel = false;
					}
					break;
			}
		}
		return measure;
	}

	static boolean getOMeasure(final String token) {
		if (token.length() < 3) {
			return false;
		}
		final char[] word = token.toCharArray();
		final int l = word.length - 1;

		return !isVowel(word[l - 2]) && isVowel(word[l - 1]) && !isVowel(word[l]) && word[l] != 'x' && word[l] != 'w' && word[l] != 'y';
	}

	static boolean containsVowel(final String token) {
		char[] word = token.toLowerCase().toCharArray();
		for (char c : word) {
			if (isVowel(c)) {
				return true;
			}
		}
		return false;
	}

	static boolean isVowel(char c) {
		switch (c) {
			case 'a':
			case 'e':
			case 'i':
			case 'o':
			case 'u':
				return true;
		}
		return false;
	}

	static boolean endsWithDoubleConsonant(final String token) {
		if (token.length() == 1)
			return false;

		int l = token.length() - 1;
		if (token.charAt(l) == token.charAt(l - 1)) {
			switch (token.charAt(l)) {
				case 'a':
				case 'e':
				case 'i':
				case 'o':
				case 'u':
					return false;
				default:
					return true;
			}
		}
		return false;
	}

}