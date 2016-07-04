package sentiment;

import reader.Reader;
import reader.Review;
import sentiment.util.Sentiment;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by fabian on 02.07.2016.
 */
public class SentimentExtractorAllReviews {
    public static void main(String[] args) throws IOException {
        List<String> top10MoviesByRatingVarianceList = Files.readAllLines(Paths.get("data/top10.txt"));
        Set<String> top10MoviesByRatingVariance = new HashSet<>(top10MoviesByRatingVarianceList);

        String filePath = "data/reviews.rtf";
        Reader reader = new Reader(filePath);

        // Read the input and clean the data
        List<Review> reviews = reader.readFile();
        reviews.remove(0);

        final Map<String, List<Review>> top10Movies = reviews.stream()
                .filter(review -> top10MoviesByRatingVariance.contains(review.getProduct().getProductID()))
                .collect(Collectors.groupingBy(review -> review.getProduct().getProductID()));

        for (Map.Entry<String, List<Review>> entry : top10Movies.entrySet()) {
            StringBuilder sb = new StringBuilder();
            for (Review review : entry.getValue()) {
                String text = review.getText();
                sb.append(text);
                sb.append(" ");
            }
            String text = sb.toString();
            Sentiment sentiment = SentimentClassifier.classify(text);
            //Sentiment sentiment = ImprovedSentimentClassifier.classify(text);
            System.out.println("entry = " + entry.getKey());
            System.out.println("sentiment = " + sentiment);
        }

    }
}
