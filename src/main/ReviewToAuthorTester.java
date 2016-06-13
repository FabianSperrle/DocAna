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

public class ReviewToAuthorTester {
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

        String filePath = "data/reviews_small.rtf";
        Reader reader = new Reader(filePath);

        // Read the input and clean the data
        List<Review> reviews = reader.readFile();
        reviews.remove(0);
        final List<Tuple> filtered = reviews.stream()
                //.filter(r -> ReviewToAuthor.filter(r.getProduct().getProductID()))
                .map(review -> new Tuple(review.getUser().getUserID(), review.getText()))
                .collect(Collectors.toList());

        List<String> p = Files.readAllLines(Paths.get("data/similarity/userprofiles"));
        Map<String, Double[]> userProfile = new HashMap<>();
        for (String profile : p) {
            String[] elems = profile.split("\\s+");
            Double[] features = new Double[6];

            for (int i = 0; i < 6; i++) {
                features[i] = Double.valueOf(elems[i + 1]);
            }

            userProfile.put(elems[0], features);
        }


        int index = (int) (Math.random() * filtered.size());
        Tuple tup = filtered.get(index);
        String review = tup.getReview();
        String user = tup.getUser();
        System.out.println("Welcome to 'Guess the User'!");
        System.out.println("I picked the following review at random: " + review);
        System.out.println("Looking for " + user);

        String guess = "";

        int i = 0;
        while (!guess.equals(user)) {
            if (!guess.equals("")) {
                System.out.println("Well that didn't work... Picking a new review!");
                index = (int) (Math.random() * filtered.size());
                tup = filtered.get(index);
                review = tup.getReview();
                user = tup.getUser();
                System.out.println("I picked the following review at random: " + review);
                System.out.println("Looking for " + user);
            }
            Tokenizer tok = new Tokenizer();
            SentenceSplitter sp = new SentenceSplitter();
            BrillTagger bt = new BrillTagger();

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
            for (Map.Entry<String, Double[]> profile : userProfile.entrySet()) {
                double tmp = TF_IDF.cosineSimilarity(features, profile.getValue());
                if (tmp > max) {
                    max = tmp;
                    guess = profile.getKey();
                }
            }
            System.out.println("My guess is " + guess);
            i++;
        }
        System.out.println("Found the correct user " + user + " after " + i + " guesses");

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
