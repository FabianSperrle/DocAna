package similarity;

import pos.brill.BrillTagger;
import stemmer.KehlbeckSperrleStemmer;
import stemmer.Stemmer;
import tokenizer.Tokenizer;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TF_IDF {
    private int corpusSize;
    private final Map<String, Double> idf;
    private Tokenizer tokenizer;
    private Stemmer stemmer;

    public TF_IDF(List<String> corpus) {
        this.corpusSize = corpus.size();
        this.tokenizer = new Tokenizer();
        this.stemmer = new KehlbeckSperrleStemmer();

        List<String[]> stems = new LinkedList<>();
        corpus.forEach(t -> stems.add(stemmer.stem(tokenizer.tokenize(t))));

        idf = this.idf(stems);
    }

    /**
     * Counts the occurences of each stem in each text.
     *
     * @param texts The List of List of stems
     * @return A List of HashMaps with the counts for each stem
     */
    private List<Map<String, Double>> tf(List<List<String>> texts) {
        final List<Map<String, Double>> _raw = texts.stream()
                .map(documentStems -> documentStems.stream()
                        .collect(Collectors.toMap(stem -> stem, count -> 1.0, (c1, c2) -> c1 + 1)))
                .collect(Collectors.toList());

        _raw.stream()
                .forEach(m -> m.entrySet().stream().forEach(e -> e.setValue(Math.log(e.getValue()) + 1)));

        return _raw;
    }

    /**
     * Calculates the IDF for the current corpus
     *
     * @param stems A list of stems for the corpus
     * @return The number of documents each stem appears in
     */
    private Map<String, Double> idf(List<String[]> stems) {
        Map<String, Double> termCount = stems.stream()
                // Get distinct list of stems per document
                .flatMap(t -> Arrays.asList(t).stream().distinct())
                // Add to the result map. Resolve key conflicts (i.e. the same stem for the second time) by
                // incrementing the counter.
                .collect(Collectors.toMap(stem -> stem, count -> 1.0, (count1, count2) -> count1 + 1));
        // Normalise all values
        termCount.entrySet().stream().forEach(entry -> termCount.put(entry.getKey(), Math.log(this.corpusSize / entry.getValue()) + 1));

        return termCount;
    }

    public double[][] tf_idf(List<String> texts) {
        return this.tf_idf(texts, null);
    }

    public double[][] tf_idf(List<String> texts, List<String> posTagsToIgnore) {
        List<List<String>> stems = new LinkedList<>();
        try {
            BrillTagger bt = null;
            if (posTagsToIgnore != null) {
                bt = new BrillTagger("data/brill/lex.txt", "data/brill/endlex.txt", "data/brill/rules.txt");
            }
            for (String text : texts) {
                final String[] tokens = this.tokenizer.tokenize(text);
                String[] stem = this.stemmer.stem(tokens);
                if (posTagsToIgnore != null) {
                    String[] tags = bt.tag(tokens);

                    for (int i = 0; i < tags.length; i++) {
                        if (posTagsToIgnore.contains(tags[i])) {
                            stem[i] = null;
                        }
                    }
                    stem = Arrays.asList(stem).stream().filter(r -> r != null).collect(Collectors.toList()).toArray(new String[0]);
                }
                stems.add(Arrays.asList(stem));

            }
            final List<Map<String, Double>> tf = tf(stems);

            double[][] tf_idf = new double[texts.size()][idf.size()];
            final LinkedList<Map.Entry<String, Double>> idfEntries = new LinkedList<>(idf.entrySet());
            for (int i = 0; i < tf.size(); i++) {
                Map<String, Double> tfMap = tf.get(i);
                for (int j = 0; j < idfEntries.size(); j++) {
                    Map.Entry<String, Double> idfEntry = idfEntries.get(j);
                    String key = idfEntry.getKey();
                    if (tfMap.containsKey(key)) {
                        tf_idf[i][j] = tfMap.get(key) * idfEntry.getValue();
                    } else {
                        tf_idf[i][j] = 0;
                    }
                }
            }

            double[][] similarityMatrix = new double[texts.size()][texts.size()];
            for (int i = 0; i < texts.size(); i++) {
                for (int j = 0; j <= i; j++) {
                    if (i == j) {
                        similarityMatrix[i][j] = 1;
                        similarityMatrix[j][i] = 1;
                        continue;
                    }
                    double sim = cosineSimilarity(tf_idf[i], tf_idf[j]);
                    similarityMatrix[i][j] = sim;
                    similarityMatrix[j][i] = sim;
                }
            }
            return similarityMatrix;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private double cosineSimilarity(double[] vector1, double[] vector2) {
        double dot = 0;
        double e1 = 0;
        double e2 = 0;
        for (int i = 0; i < vector1.length; i++) {
            dot += vector1[i] * vector2[i];
            e1 += Math.pow(vector1[i], 2);
            e2 += Math.pow(vector2[i], 2);
        }
        e1 = Math.sqrt(e1);
        e2 = Math.sqrt(e2);

        return dot / (e1 * e2);
    }
}
