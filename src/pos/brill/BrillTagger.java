package pos.brill;

import jdk.nashorn.internal.objects.NativeArray;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by fabian on 27.05.2016.
 */
public class BrillTagger {
    private Map<String,String> lexicon;
    private List<Rule> rules;

    public BrillTagger(String lexicon, String rules) throws IOException {
        this.lexicon = new HashMap<>();
        this.rules = new LinkedList<>();

        BufferedReader lexiconReader = new BufferedReader(new FileReader(new File(lexicon)));
        String line = null;
        while ((line = lexiconReader.readLine()) != null) {
            String[] elems = line.split("\\s");
            this.lexicon.put(elems[0], elems[1]);
        }

        BufferedReader ruleReader = new BufferedReader(new FileReader(new File(rules)));
        line = null;
        while ((line = ruleReader.readLine()) != null) {
            String[] elems = line.split("\\s");
            this.rules.add(new Rule(elems));
        }
    }

    public BrillTagger() throws IOException {
        this("data/brill/lexicon.txt", "data/brill/rules.txt");
    }

    public String[] tag(String[] tokens) {
        String[] tags = new String[tokens.length];
        for (int i = 0; i < tags.length; i++) {
            String token = tokens[i];
            if (this.lexicon.containsKey(token)) {
                tags[i] = this.lexicon.get(token);
            }
            else {
                if (Character.isUpperCase(token.charAt(0))) {
                    tags[i] = "NN";
                }

            }
        }
        return tags;
    }
}
