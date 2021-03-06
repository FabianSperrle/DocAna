package pos.hmm;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pos.MapUpdaterHelper;
import tokenizer.Tokenizer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class ViterbiTagger {
    private Logger logger = LogManager.getLogger(ViterbiTagger.class);

    // <token, <tag, probability>>
    private Map<String, Map<String, Double>> emissionParameters = new HashMap<>();
    private Map<String, Map<String, Double>> trigramParameters = new HashMap<>();
    private String[] tags;
    private String pathString;

    public ViterbiTagger(String pathString) throws IOException {
        this.pathString = pathString;
        this.learnCorpus();
    }

    private void learnCorpus() throws IOException {
        Path path = Paths.get(this.pathString);
        List<File> corpusFiles = Files.walk(path).filter(Files::isRegularFile).map(Path::toFile)
                //.filter(file -> file.getName().startsWith("cc")) // only reviews
                .collect(Collectors.toList());

        // Store how often each word occurs under each tag
        // <tag, <token, count>>
        Map<String, Map<String, Integer>> countTokensPerTag = new HashMap<>();
        // <<tag, tag>, <tag, count>>
        Map<String, Map<String, Integer>> ngramCount = new HashMap<>();

        for (File file : corpusFiles) {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            String line = reader.readLine();
            while (line != null) {
                // Split the line into token/tag pairs
                ArrayList<String> tokensWithTages = new ArrayList<String>(Arrays.asList(line.split("\\s")));

                // Fill index positions -2 and -1
                List<String> tagList = new ArrayList<>();
                tagList.add("�");
                tagList.add("�");

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
                    token = token.trim();
                    tag = tag.trim();


                    // Simplify the tag to speed up the calculations
                    if (tag.endsWith("-hl") || tag.endsWith("-nc")) {
                        tag = tag.substring(0, tag.length() - 3);
                    }
                    if (tag.endsWith("-tl")) {
                        tag = tag.substring(0, tag.length() - 3);
                    }

                    if (tag.startsWith("fw-")) {
                        tag = tag.substring(3, tag.length());
                    }


                    // Don't store ' '' and ` etc as they are not produced by our tokenizer
                    if (tag.endsWith("*") || tag.equals("\"") || tag.equals("'") || tag.equals("`") || tag.equals("``") || tag.equals("''")) {
                        tokensWithTages.remove(tag);
                        continue;
                    }

                    // Update the counter for each token/tag pair
                    MapUpdaterHelper.updateMap(countTokensPerTag, tag, token);

                    // Update taglist for the sentence
                    tagList.add(tag);
                }
                tagList.add("STOP");

                // Taglist only contains �,�,STOP (i.e. empty line in the corpus file)
                if (tagList.size() == 3) {
                    line = reader.readLine();
                    continue;
                }

                // Entire line read; do something with the tag list!
                for (int i = 2; i < tagList.size(); i++) {
                    String tag = tagList.get(i);
                    String predecessors = tagList.get(i - 2) + "#" + tagList.get(i - 1);
                    // Check if we have seen that predecessor pair before
                    MapUpdaterHelper.updateMap(ngramCount, predecessors, tag);
                }

                line = reader.readLine();
            }
            reader.close();
        }

        // Now that we have the occurrences, convert them to emission parameters
        for (Map.Entry<String, Map<String, Integer>> entry : countTokensPerTag.entrySet()) {
            String tag = entry.getKey();
            Map<String, Integer> tokenCountMap = entry.getValue();
            double sumOfOccurrences = 0;
            for (Integer count : tokenCountMap.values()) {
                sumOfOccurrences += count;
            }
            for (Map.Entry<String, Integer> tokenCount : tokenCountMap.entrySet()) {
                String token = tokenCount.getKey();
                int count = tokenCount.getValue();

                if (this.emissionParameters.containsKey(token)) {
                    Map<String, Double> map = this.emissionParameters.get(token);
                    map.put(tag, count / sumOfOccurrences);
                } else {
                    Map<String, Double> map = new HashMap<>();
                    map.put(tag, count / sumOfOccurrences);
                    this.emissionParameters.put(token, map);
                }
            }
        }

        // Now that we have the occurrences, convert them to trigram parameters
        for (Map.Entry<String, Map<String, Integer>> entry : ngramCount.entrySet()) {
            String predecessors = entry.getKey();
            Map<String, Integer> tagCountMap = entry.getValue();
            double sumOfOccurences = 0;
            for (Integer count : tagCountMap.values()) {
                sumOfOccurences += count;
            }
            for (Map.Entry<String, Integer> tagCount : tagCountMap.entrySet()) {
                String tag = tagCount.getKey();
                int count = tagCount.getValue();

                if (this.trigramParameters.containsKey(predecessors)) {
                    Map<String, Double> map = this.trigramParameters.get(predecessors);
                    map.put(tag, count / sumOfOccurences);
                } else {
                    Map<String, Double> map = new HashMap<>();
                    map.put(tag, count / sumOfOccurences);

                    this.trigramParameters.put(predecessors, map);
                }
            }
        }
        this.tags = countTokensPerTag.keySet().toArray(new String[0]);
        Arrays.sort(this.tags);
        System.out.println("Arrays.toString(this.tags) = " + Arrays.toString(this.tags));
        System.out.println("this.tags.length = " + this.tags.length);
    }

    public String[] getTagList(String sentence) throws IOException {
        Tokenizer tok = new Tokenizer();
        String[] tokens = tok.tokenize(sentence);
        for (int i = 0; i < tokens.length; i++) {
            tokens[i] = tokens[i].toLowerCase();
        }

        double[][][] pi = new double[tokens.length][this.tags.length][this.tags.length];
        int[][][] backpointer = new int[tokens.length][this.tags.length][this.tags.length];

        for (int k = 0; k < tokens.length; k++) {
            for (int u = 0; u < this.tags.length; u++) {
                for (int w = 0; w < this.tags.length; w++) {
                    pi[k][u][w] = 0;
                    backpointer[k][u][w] = 0;
                }
            }
        }

        for (int k = 0; k < tokens.length; k++) {
            for (int u = 0; u < this.tags.length; u++) {
                for (int w = 0; w < this.tags.length; w++) {

                    double pik_1wu = k == 0 ? 1 : pi[k - 1][w][u];

                    if (pik_1wu == 0)
                        continue;

                    for (int v = 0; v < this.tags.length; v++) {
                        double e = 0.0001;
                        if (this.emissionParameters.containsKey(tokens[k])) {
                            if (this.emissionParameters.get(tokens[k]).containsKey(tags[v])) {
                                e = this.emissionParameters.get(tokens[k]).get(tags[v]);
                            }
                        }
                        if (e == 0)
                            continue;

                        double q = 0.0001;
                        if (k == 0 && u == 0 && w == 0) {
                            if (this.trigramParameters.get("�#�").containsKey(tags[v])) {
                                q = this.trigramParameters.get("�#�").get(tags[v]);
                            }
                        } else if (k == 1 && w == 0) {
                            if (this.trigramParameters.containsKey("�#" + tags[u])) {
                                if (this.trigramParameters.get("�#" + tags[u]).containsKey(tags[v])) {
                                    q = this.trigramParameters.get("�#" + tags[u]).get(tags[v]);
                                }
                            }
                        } else {
                            if (this.trigramParameters.containsKey(tags[w] + "#" + tags[u])) {
                                if (this.trigramParameters.get(tags[w] + "#" + tags[u]).containsKey(tags[v])) {
                                    q = this.trigramParameters.get(tags[w] + "#" + tags[u]).get(tags[v]);
                                }
                            }
                        }
                        if (q == 0)
                            continue;

                        double prob = pik_1wu * q * e;

                        if (prob > pi[k][u][v]) {
                            pi[k][u][v] = prob;
                            backpointer[k][u][v] = w;
                        }
                    }
                }
            }
        }

        int[] resultIDs = new int[tokens.length];

        double maxStopProb = 0;
        int n = tokens.length - 1;
        for (int u = 0; u < this.tags.length; u++) {
            for (int v = 0; v < this.tags.length; v++) {
                double q = 0;
                if (this.trigramParameters.containsKey(tags[u] + "#" + tags[v])) {
                    if (this.trigramParameters.get(tags[u] + "#" + tags[v]).containsKey("STOP")) {
                        q = this.trigramParameters.get(tags[u] + "#" + tags[v]).get("STOP");
                    }
                }
                double prob = pi[n][u][v] * q;

                if (prob > maxStopProb) {
                    maxStopProb = prob;
                    resultIDs[n] = v;
                    if (resultIDs.length > 1) {
                        resultIDs[n - 1] = u;
                    }
                }
            }
        }

        for (int k = tokens.length - 3; k >= 0; k--) {
            int b = backpointer[k + 2][resultIDs[k + 1]][resultIDs[k + 2]];
            resultIDs[k] = b;
        }

        String[] result = new String[n + 1];
        for (int i = 0; i < result.length; i++) {
            result[i] = this.tags[resultIDs[i]];
        }
        this.logger.debug(Arrays.toString(result));

        return result;
    }
}
