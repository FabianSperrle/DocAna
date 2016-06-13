package main;

import pos.brill.BrillTagger;
import reader.Reader;
import reader.Review;
import similarity.TF_IDF;
import tokenizer.SentenceSplitter;
import tokenizer.Tokenizer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.summingInt;

public class SimilarityReviews {
    public static void main(String[] args) throws IOException {

        String filePath = "data/reviews.rtf";
        Reader reader = new Reader(filePath);

        // Read the input and clean the data
        List<Review> reviews = reader.readFile();
        reviews.remove(0);

        final List<String> filtered = reviews.stream()
                .filter(r -> SimilarityReviews.filter(r.getProduct().getProductID()))
                .map(r -> r.getText())
                .collect(Collectors.toList());

        Tokenizer tok = new Tokenizer();
        SentenceSplitter sp = new SentenceSplitter();
        BrillTagger bt = new BrillTagger();
        final List<Double[]> featuresVectors = filtered.stream()
                .map(review -> {
                    Double[] features = new Double[6];
                    List<String> tokens = Arrays.asList(tok.tokenize(review));
                    List<String> sentences = Arrays.asList(sp.split(review));
                    List<String> tags = Arrays.asList(bt.tag(review));
                    // Average word length
                    features[0] = 11 * tokens.stream()
                            .mapToInt(String::length)
                            .average().orElse(0);
                    // percentage of distinct words
                    features[1] = 33 * ((double) tokens.stream().distinct().count()) / tokens.size();
                    // Percentage of words that appear exactly once
                    features[2] = 100 * (double) tokens.stream()
                            .filter(t -> Collections.frequency(tokens, t) == 1)
                            .count() / tokens.size();
                    // Average sentence length
                    features[3] = 0.1 * sentences.stream()
                            .map(tok::tokenize)
                            .mapToInt(t -> t.length)
                            .average().orElse(0);
                    // Phrases per sentence
                    features[4] = 20 * ((double) sentences.stream()
                            .flatMap(s -> Arrays.asList(tok.tokenize(s)).stream())
                            .filter(t -> t.equals(",") || t.equals(";") || t.equals(":") || t.equals("\""))
                            .count()) / (sentences.size() == 0 ? 1 : sentences.size());
                    // Distinct tags
                    features[5] = 200 * ((double) tags.stream().distinct().count()) / tags.size();

                    return features;

                })
                .collect(Collectors.toList());

        double[][] similarity = new double[featuresVectors.size()][featuresVectors.size()];
        for (int i = 0; i < featuresVectors.size(); i++) {
            for (int j = 0; j < featuresVectors.size(); j++) {
                Double[] rev1 = featuresVectors.get(i);
                Double[] rev2 = featuresVectors.get(j);

                double cos = TF_IDF.cosineSimilarity(rev1, rev2);
                similarity[i][j] = cos;
            }
        }

        double[][] groupedSimilarity = new double[5][5];
        int[][] groupCount = new int[5][5];

        for (int i = 0; i < similarity.length; i++) {
            for (int j = 0; j < similarity.length; j++) {
                int score1 = (int) reviews.get(i).getScore() - 1;
                int score2 = (int) reviews.get(j).getScore() - 1;

                groupedSimilarity[score1][score2] += similarity[i][j];
                System.out.println(similarity[i][j]);
                groupCount[score1][score2]++;
            }
        }

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                groupedSimilarity[i][j] /= groupCount[i][j];
            }
        }

        for (double[] doubles : groupedSimilarity) {
            System.out.println("doubles = " + Arrays.toString(doubles));
        }


        // word length
        // distinct words / word count
        // hapax legomana
        // Sentence length
        // sentence complexity (, before .)

        // no of pos tags / word count

        // weights = [11, 33, 50, 0.4, 4]



    }

    private static boolean filter(String id) {
        switch (id) {
            case "B000KKQNRO":
                return true;
            default:
                return false;
        }
    }
}
