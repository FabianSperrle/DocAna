package tokenizer;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tokenizer {
    Logger logger = LogManager.getLogger(Tokenizer.class);

    public String[] tokenize(String text) {
        // https://regex101.com/r/dM0yY9/4
        text = text.replaceAll("<.*?>", "");
        Pattern stuff = Pattern.compile("(?:(?<=[\"])(?:\\w+\\s){1,3}\\w+(?=[\"])|'\\d+|[a-zA-Z']+|(?<=[a-zA-Z])-(?=[a-zA-Z])|(?<=[a-zA-Z])&(?=[a-zA-Z]))+|(?:[-.\\p{Sc}]?\\d+(?:\\.\\d+)?)[%\\p{Sc}]?|[.,!?;():\"]+|--");
        Matcher matcher = stuff.matcher(text);
        List<String> tokens = new ArrayList<String>();
        while (matcher.find()) {
            tokens.add(matcher.group(0)); // add match to the list
        }

        for (int i = 0; i < tokens.size(); i++) {
            try {
                String token = tokens.get(i);
                if (Character.isUpperCase(token.charAt(0))) {
                    String next;
                    while (Character.isUpperCase((next = tokens.get(i + 1)).charAt(0))) {
                        token += " " + tokens.get(i + 1);
                        tokens.remove(i + 1);
                    }
                }

                if (tokens.get(i + 1).equals(".") && tokens.get(i + 3).startsWith(".")) {
                    token = tokens.get(i) + "." + tokens.get(i + 2) + ".";
                    tokens.remove(i + 1);
                    tokens.remove(i + 1);
                    tokens.remove(i + 1);
                }

                switch (token) {
                    case "aren't":
                    case "can't":
                    case "couldn't":
                    case "didn't":
                    case "doesn't":
                    case "don't":
                    case "hadn't":
                    case "hasn't":
                    case "isn't":
                    case "mightn't":
                    case "mustn't":
                    case "shan't":
                    case "shouldn't":
                    case "weren't":
                    case "wouldn't":
                    case "haven't":
                        token = token.substring(0, token.length() - 3);
                        tokens.add(i + 1, "not");
                        break;

                    case "won't":
                        token = "will";
                        tokens.add(i + 1, "not");
                        break;

                    case "ain't":
                        token = "are";
                        tokens.add(i + 1, "not");
                        break;

                    case "he'd":
                    case "I'd":
                    case "she'd":
                    case "they'd":
                    case "we'd":
                    case "you'd":
                        token = token.substring(0, token.length() - 2);
                        tokens.add(i + 1, "would");
                        break;

                    case "he'll":
                    case "I'll":
                    case "she'll":
                    case "they'll":
                    case "what'll":
                    case "who'll":
                    case "you'll":
                        token = token.substring(0, token.length() - 3);
                        tokens.add(i + 1, "will");
                        break;

                    case "he's":
                    case "she's":
                    case "that's":
                    case "there's":
                    case "what's":
                    case "let's":
                    case "where's":
                    case "who's":
                    case "here's":
                        token = token.substring(0, token.length() - 2);
                        tokens.add(i + 1, "is");
                        break;

                    case "I'm":
                        token = "I";
                        tokens.add(i + 1, "am");
                        break;

                    case "we're":
                    case "who're":
                    case "they're":
                    case "what're":
                    case "you're":
                        token = token.substring(0, token.length() - 3);
                        tokens.add(i + 1, "are");
                        break;

                    case "I've":
                    case "they've":
                    case "we've":
                    case "what've":
                    case "who've":
                    case "you've":
                        token = token.substring(0, token.length() - 3);
                        tokens.add(i + 1, "have");
                        break;

                    case "Ave":
                        token = "avenue";
                        break;
                    case "Blvd":
                        token = "boulevard";
                        break;
                    case "Ln":
                        token = "lane";
                        break;
                    case "Rd":
                        token = "road";
                        break;
                    case "St":
                        token = "street";
                        break;

                }
                tokens.set(i, token);
            } catch (IndexOutOfBoundsException e) {

            }
        }

        return tokens.toArray(new String[tokens.size()]);
    }
}
