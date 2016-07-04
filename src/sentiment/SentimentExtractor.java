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
public class SentimentExtractor {
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
            String movieID = entry.getKey();
            int[] positiveCount = new int[5];
            int[] negativeCount = new int[5];
            int[] undefinedCount = new int[5];

            List<String> positiveReviews = new LinkedList<>();
            List<String> negativeReviews = new LinkedList<>();
            List<String> undefinedReviews = new LinkedList<>();

            for (Review review : entry.getValue()) {
                String text = review.getText();
                //Sentiment sentiment = SentimentClassifier.classify(text);
                Sentiment sentiment = ImprovedSentimentClassifier.classify(text);
                int rating = (int) review.getScore() - 1;

                switch (sentiment) {
                    case GOOD:
                        positiveCount[rating]++;
                        positiveReviews.add(text);
                        break;
                    case BAD:
                        negativeCount[rating]++;
                        negativeReviews.add(text);
                        break;
                    case UNDEFINED:
                        undefinedCount[rating]++;
                        undefinedReviews.add(text);
                        break;
                }
            }

            Files.write(Paths.get("data/sentiment/positive_" + movieID), positiveReviews);
            Files.write(Paths.get("data/sentiment/negative_" + movieID), negativeReviews);
            Files.write(Paths.get("data/sentiment/undefined_" + movieID), undefinedReviews);

            System.out.println("movieID = " + movieID);
            System.out.println("Arrays.toString(positiveCount) = " + Arrays.toString(positiveCount));
            System.out.println("Arrays.toString(negativeCount) = " + Arrays.toString(negativeCount));
            System.out.println("Arrays.toString(undefinedCount) = " + Arrays.toString(undefinedCount));
        }
    }
}
