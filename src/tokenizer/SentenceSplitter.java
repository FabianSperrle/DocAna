package tokenizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SentenceSplitter {

	public String[] split(String s) {
		Pattern stuff = Pattern.compile("\\S(?:.|\\n)*?(?:\\.|\\?|!|:)(?=(?:\\s+[A-Z])|\\Z)");
		Matcher matcher = stuff.matcher(s);
		List<String> sentences = new ArrayList<>();
		while (matcher.find()) {
			sentences.add(matcher.group(0)); // add match to the list
		}
		return sentences.toArray(new String[0]);
	}

}
