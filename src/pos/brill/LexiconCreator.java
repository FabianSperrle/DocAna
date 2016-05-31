package pos.brill;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import pos.MapUpdaterHelper;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class LexiconCreator {
    private static List<File> getCorpusFiles() throws IOException {
        Path path = Paths.get("data/brown");
        List<File> corpusFiles = Files.walk(path).filter(Files::isRegularFile).map(Path::toFile)
                //.filter(file -> file.getName().startsWith("cc")) // only reviews
                .limit(20)
                .collect(Collectors.toList());
        return corpusFiles;
    }

    private static void learnRules() throws IOException {
        final List<File> corpusFiles = getCorpusFiles();
        final List<String> lines = new LinkedList<>();
        for (File file : corpusFiles) {
            lines.addAll(Files.readAllLines(file.toPath())
                    .stream()
                    .filter(line -> !line.equals(""))
                    .collect(Collectors.toList()));
        }
        System.out.println("Lines has " + lines.size() + " elements");

        List<String[]> tokens = new LinkedList<>();
        List<String[]> tags = new LinkedList<>();
        for (String line : lines) {
            System.out.println("line = " + line);
            String[] elems = line.split("\\s");
            System.out.println("elems = " + Arrays.toString(elems));
            List<String> toks = new LinkedList<>();
            List<String> taggs = new LinkedList<>();

            for (int i = 0; i < elems.length; i++) {
                String combination = elems[i];
                if (combination.equals("")) {
                    continue;
                }
                String[] results = combination.split("/");
                String token = results[0];
                String tag = results[1];

                // Handle special case where token contains /
                if (results.length > 2) {
                    for (int j = 1; j < results.length; j++) {
                        token += results[j];
                    }
                    tag = results[results.length - 1];
                }

                toks.add(token);
                taggs.add(tag);
            }

            System.out.println("taggs = " + taggs);
            System.out.println("toks = " + toks);

            tokens.add(toks.toArray(new String[0]));
            tags.add(taggs.toArray(new String[0]));
        }

        BrillTagger bt = new BrillTagger();
        Map<String, Integer> errors = new HashMap<>();

        getParsingErrors(tokens, tags, bt, errors, null);

        List<String> tagList = Files.readAllLines(Paths.get("data/brill/taglist.txt"));
        List<Rule> learnedRules = new LinkedList<>();

        for (Map.Entry<String, Integer> entry : errors.entrySet()) {
            System.out.println("entry = " + entry.getKey());
            String[] s = entry.getKey().split("#");
            String tag_a = s[0];
            String tag_b = s[1];

            int error = entry.getValue();

            int maxImprovement = 0;
            Rule maxRule = null;
            for (String tag : tagList) {
                System.out.println("tag = " + tag);

                for (PredicateName pred : PredicateName.values()) {
                    Rule r = new Rule(tag_a, tag_b, pred.name(), tag, null);
                    HashMap<String, Integer> ruleErrors = new HashMap<>();
                    getParsingErrors(tokens, tags, bt, ruleErrors, r);

                    int errorNow = 0;
                    int additionalError = 0;
                    String key = tag_a + "#" + tag_b;
                    String revKey = tag_b + "#" + tag_a;
                    if (ruleErrors.containsKey(key)) {
                        errorNow = ruleErrors.get(key);
                    }
                    if (ruleErrors.containsKey(revKey)) {
                        additionalError = ruleErrors.get(revKey);
                    }

                    int improvement = error - errorNow + additionalError;
                    if (improvement > maxImprovement) {
                        maxImprovement = improvement;
                        maxRule = r;
                    }
                }
            }

            if (maxRule != null) {
                learnedRules.add(maxRule);
                System.out.println("maxRule = " + maxRule);
            }
        }

        for (Rule learnedRule : learnedRules) {
            System.out.println("learnedRule = " + learnedRule.toString());
        }
    }

    private static void getParsingErrors(List<String[]> tokens, List<String[]> tags, BrillTagger bt, Map<String, Integer> errors, Rule rule) throws IOException {
        for (int i = 0; i < tokens.size(); i++) {
            String[] sentence = tokens.get(i);
            String[] corpusTags = tags.get(i);

            String[] result;
            if (rule == null) {
                result = bt.tag(sentence);
            } else {
                result = bt.tag(sentence, rule);
            }

            for (int j = 0; j < result.length; j++) {
                String corpusTag = corpusTags[j];
                // Simplify the tag to speed up the calculations
                if (corpusTag.endsWith("-hl") || corpusTag.endsWith("-tl") || corpusTag.endsWith("-nc")) {
                    corpusTag = corpusTag.substring(0, corpusTag.length() - 3);
                }

                // Don't store ' '' and ` etc as they are not produced by our tokenizer
                if (corpusTag.equals("\"") || corpusTag.equals("'") || corpusTag.equals("`") || corpusTag.equals("``") || corpusTag.equals("''")
                        || corpusTag.equals("(") || corpusTag.equals(")") || corpusTag.equals(".") || corpusTag.equals(":")
                        || corpusTag.equals(";") || corpusTag.equals(",") || corpusTag.equals("--")) {
                    continue;
                }
                if (!result[j].equals(corpusTag)) {
                    String k = result[j] + "#" + corpusTag;
                    if (errors.containsKey(k)) {
                        errors.put(k, errors.get(k) + 1);
                    } else {
                        errors.put(k, 1);
                    }
                }

            }
        }
    }

    private static void createLexiconFromBrownCorpus() throws IOException {
        final List<File> corpusFiles = getCorpusFiles();
        // Store how often each word occurs under each tag
        // <token, <tag, count>>
        Map<String, Map<String, Integer>> countTokensPerTag = new HashMap<>();

        for (File file : corpusFiles) {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            String line = reader.readLine();
            while (line != null) {
                // Split the line into token/tag pairs
                ArrayList<String> tokensWithTages = new ArrayList<String>(Arrays.asList(line.split("\\s")));

                // Count the occurences of each tag
                for (String combination : tokensWithTages) {
                    if (combination.equals("")) {
                        continue;
                    }
                    String[] results = combination.split("/");
                    String token = results[0];
                    String tag = results[1];

                    // Handle special case where token contains /
                    if (results.length > 2) {
                        for (int i = 1; i < results.length; i++) {
                            token += results[i];
                        }
                        tag = results[results.length - 1];
                    }

                    // Simplify the tag to speed up the calculations
                    if (tag.endsWith("-hl") || tag.endsWith("-tl") || tag.endsWith("-nc")) {
                        tag = tag.substring(0, tag.length() - 3);
                    }

                    // Don't store ' '' and ` etc as they are not produced by our tokenizer
                    if (tag.equals("\"") || tag.equals("'") || tag.equals("`") || tag.equals("``") || tag.equals("''")
                            || tag.equals("(") || tag.equals(")") || tag.equals(".") || tag.equals(":")
                            || tag.equals(";") || tag.equals(",") || tag.equals("--")) {
                        tokensWithTages.remove(tag);
                        continue;
                    }

                    // Update the counter for each token/tag pair
                    MapUpdaterHelper.updateMap(countTokensPerTag, token, tag);

                }
                line = reader.readLine();
            }
            reader.close();
        }

        List<String> lexicon = new LinkedList<>();
        Map<String, Map<String, Integer>> endCharLexiconMap = new HashMap<>();
        for (Map.Entry<String, Map<String, Integer>> entry : countTokensPerTag.entrySet()) {
            String token = entry.getKey();
            Map<String, Integer> tagMap = entry.getValue();

            int max = 0;
            String maxTag = "";
            for (Map.Entry<String, Integer> e : tagMap.entrySet()) {
                if (e.getValue() > max) {
                    max = e.getValue();
                    maxTag = e.getKey();
                }
            }
            lexicon.add(token + " " + maxTag);

            String sub = token.substring(Math.max(0, token.length() - 3));
            MapUpdaterHelper.updateMap(endCharLexiconMap, sub, maxTag);
        }
        Files.write(Paths.get("data/brill/lex.txt"), lexicon);

        List<String> endCharLexicon = new LinkedList<>();
        for (Map.Entry<String, Map<String, Integer>> entry : endCharLexiconMap.entrySet()) {
            String token = entry.getKey();
            Map<String, Integer> tagMap = entry.getValue();

            int max = 0;
            String maxTag = "";
            for (Map.Entry<String, Integer> e : tagMap.entrySet()) {
                if (e.getValue() > max) {
                    max = e.getValue();
                    maxTag = e.getKey();
                }
            }
            endCharLexicon.add(token + " " + maxTag);
        }
        Files.write(Paths.get("data/brill/endlex.txt"), endCharLexicon);
    }

    public static void main(String[] args) throws IOException {
        createLexiconFromBrownCorpus();
        learnRules();
    }
}

