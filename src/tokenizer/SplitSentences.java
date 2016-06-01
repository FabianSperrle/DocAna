package tokenizer;

import java.io.IOException;
import java.util.StringTokenizer;

public class SplitSentences {

	private String s;
	
	public SplitSentences(String text) {
		this.s = text;
	}
	
	public String[] tokenize() throws IOException {

		String[] r = s.split("(\\.|\\?|\\!) (?=[A-Z])"); 
		r[r.length-1] = r[r.length-1].substring(0, r[r.length-1].length()-1);
		return r;
	}	
	
}
