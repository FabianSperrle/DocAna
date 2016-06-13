package main;

import pos.brill.BrillTagger;
import reader.Reader;
import reader.Review;
import similarity.TF_IDF;
import tokenizer.SentenceSplitter;
import tokenizer.Tokenizer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReviewToAuthor {
    public static void main(String[] args) throws IOException {

        class Tuple {
            private String user;
            private String review;
            private Double[] stats;

            private Tuple(String user, String review) {
                this.user = user;
                this.review = review;
            }

            public String getUser() {
                return user;
            }

            public void setUser(String user) {
                this.user = user;
            }

            public String getReview() {
                return review;
            }

            public void setReview(String review) {
                this.review = review;
            }

            private Double[] getStats() {
                return stats;
            }

            private void setStats(Double[] stats) {
                this.stats = stats;
            }
        }

        String filePath = "data/reviews.rtf";
        Reader reader = new Reader(filePath);

        // Read the input and clean the data
        List<Review> reviews = reader.readFile();
        reviews.remove(0);

        final List<Tuple> filtered = reviews.stream()
                //.filter(r -> ReviewToAuthor.filter(r.getProduct().getProductID()))
                .map(review -> new Tuple(review.getUser().getUserID(), review.getText()))
                .collect(Collectors.toList());

        Tokenizer tok = new Tokenizer();
        SentenceSplitter sp = new SentenceSplitter();
        BrillTagger bt = new BrillTagger();
        final Map<String, Double[]> userProfile = filtered.stream()
                .peek(tuple -> {
                    Double[] features = new Double[6];
                    List<String> tokens = Arrays.asList(tok.tokenize(tuple.getReview()));
                    List<String> sentences = Arrays.asList(sp.split(tuple.getReview()));
                    List<String> tags = Arrays.asList(bt.tag(tuple.getReview()));
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

                    tuple.setStats(features);
                })
                .collect(Collectors.toMap(
                        Tuple::getUser,
                        Tuple::getStats,
                        (s1, s2) -> {
                            Double[] temp = new Double[s1.length];
                            for (int i = 0; i < s1.length; i++) {
                                temp[i] = (s1[i] + s2[i]) / 2;
                            }
                            return temp;
                        }
                ));

        BufferedWriter bw = new BufferedWriter(new FileWriter("data/similarity/userprofiles"));
        userProfile.entrySet().forEach(t -> {
            StringBuilder sb = new StringBuilder();
            sb.append(t.getKey());
            sb.append("  ");
            for (int i = 0; i < t.getValue().length; i++) {
                sb.append(" ");
                sb.append(t.getValue()[i]);
            }
            sb.append("\n");
            try {
                bw.write(sb.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        bw.close();

        int index = (int) (Math.random() * userProfile.size());
        Tuple tup = filtered.get(index);
        String review = tup.getReview();
        String user = tup.getUser();

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

        double max = Double.MIN_VALUE;
        String maxuser = null;
        for (Map.Entry<String, Double[]> profile : userProfile.entrySet()) {
            double tmp = TF_IDF.cosineSimilarity(features, profile.getValue());
            if (tmp > max) {
                max = tmp;
                maxuser = profile.getKey();
            }
        }
        System.out.println("user = " + user);
        System.out.println("maxuser = " + maxuser);
    }


    private static boolean filter(String id) {
        switch (id) {
            case "B000KKQNRO":
            case "B004WO6BPS":
            case "B009NQKPUW":
                return true;
            default:
                return false;
        }
    }
}
