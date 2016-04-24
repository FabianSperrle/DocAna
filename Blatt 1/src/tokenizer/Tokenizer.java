package tokenizer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Tokenizer {

	private StreamTokenizer st;
	
	public Tokenizer(String text) {
		this.st = new StreamTokenizer(new StringReader(text));
		this.st.quoteChar('"');
	}
	
	
	private String removeTrailingPeriod(String token) {
		if (token.endsWith(".")) {
			token = token.substring(0, token.length()-1);
		}
		return token;
	}
	
	public String[] tokenize() throws IOException {
		List<String> results = new ArrayList<>();
		
		while(this.st.nextToken() != StreamTokenizer.TT_EOF) {
			switch (st.ttype) {
			case StreamTokenizer.TT_WORD:
				results.add(st.sval);
				break;
			case '"':
			case '\'':
				results.addAll(Arrays.asList(new Tokenizer(st.sval).tokenize()));
				break;
			default:
				break;
			}
		}

		results = results.stream().map(token -> removeTrailingPeriod(token)).collect(Collectors.toList());
		return results.toArray(new String[0]);
	}	
}
