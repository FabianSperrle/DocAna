package pos.brill;

import pos.MapUpdaterHelper;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class LexiconCreator {
    public static void createLexiconFromBrownCorpus() throws IOException {
        Path path = Paths.get("data/brown");
        List<File> corpusFiles = Files.walk(path).filter(Files::isRegularFile).map(Path::toFile)
                //.filter(file -> file.getName().startsWith("cc")) // only reviews
                .collect(Collectors.toList());

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
    }
}

