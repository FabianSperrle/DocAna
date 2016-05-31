package pos.brill;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import tokenizer.Tokenizer;

import java.io.*;
import java.util.*;

public class BrillTagger {
    private Map<String,String> lexicon;
    private Map<String,String> endCharLexicon;
    private List<Rule> rules;

    public BrillTagger(String lexicon, String endlex, String rules) throws IOException {
        this.lexicon = new HashMap<>();
        this.endCharLexicon = new HashMap<>();
        this.rules = new LinkedList<>();

        BufferedReader lexiconReader = new BufferedReader(new FileReader(new File(lexicon)));
        String line = null;
        while ((line = lexiconReader.readLine()) != null) {
            String[] elems = line.split("\\s");
            this.lexicon.put(elems[0], elems[1]);
        }

        BufferedReader endLexReader = new BufferedReader(new FileReader(new File(endlex)));
        line = null;
        while ((line = endLexReader.readLine()) != null) {
            String[] elems = line.split("\\s");
            this.endCharLexicon.put(elems[0], elems[1]);
        }

        BufferedReader ruleReader = new BufferedReader(new FileReader(new File(rules)));
        line = null;
        while ((line = ruleReader.readLine()) != null) {
            String[] elems = line.split("\\s");
            this.rules.add(new Rule(elems));
        }
    }

    public BrillTagger() throws IOException {
        this("data/brill/lex.txt", "data/brill/endlex.txt", "data/brill/rulefile.txt");
    }

    public String[] tag(String sentence) throws IOException {
        Tokenizer tok = new Tokenizer(sentence);
        String[] tokens = tok.tokenize();

        String[] tags = new String[tokens.length];
        for (int i = 0; i < tags.length; i++) {
            String token = tokens[i];
            if (this.lexicon.containsKey(token)) {
                tags[i] = this.lexicon.get(token);
            }
            else {
                if (Character.isUpperCase(token.charAt(0))) {
                    tags[i] = "NN";
                } else {
                    String endChars = token.substring(Math.max(0, token.length() - 3));
                    if (this.endCharLexicon.containsKey(endChars)) {
                        tags[i] = this.endCharLexicon.get(endChars);
                    }
                }
            }
        }

        System.out.println("tags = " + Arrays.toString(tags));
        for (int i = 0; i < tags.length; i++) {
            for (Rule rule : rules) {
                rule.apply(tags, tokens, i);
            }
        }
        System.out.println("tags = " + Arrays.toString(tags));
        return tags;
    }

    public static void main(String[] args) {
        try {
            BrillTagger t = new BrillTagger();
            String[] tags = t.tag("Hey, how are you doing.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
