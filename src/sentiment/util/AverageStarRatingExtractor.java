package sentiment.util;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import reader.Reader;
import reader.Review;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class AverageStarRatingExtractor {
    public static void main(String[] args) throws IOException {
        String filePath = "data/reviews.rtf";
        Reader reader = new Reader(filePath);

        // Read the input and clean the data
        List<Review> reviews = reader.readFile();
        reviews.remove(0);

        // Get all ratings for each review
        final Map<String, List<Double>> collect = reviews.stream().collect(Collectors.toMap(
                review -> review.getProduct().getProductID(),
                // HAXX - Arrays.asList returns an immutable list, so the map merge would fail
                // Wrap in a new (mutable) LinkedList to "fix" the issue.
                review -> new LinkedList<>(Collections.singletonList(review.getScore())),
                (l1, l2) -> {
                    l1.addAll(l2);
                    return l1;
                }
        ));

        // Calculate rating variance for each movie
        SortedMap<Double, List<String>> ratingVarianceMap = new TreeMap<>();
        Map<String, Double> avgMap = new HashMap<>();

        collect.entrySet().forEach(
                entry -> {
                    String key = entry.getKey();
                    List<Double> ratings = entry.getValue();

                    // Only use movies with enough reviews
                    if (ratings.size() < 100) return;

                    // Get rating variance
                    SummaryStatistics stats = new SummaryStatistics();
                    ratings.forEach(stats::addValue);
                    double variance = stats.getVariance();
                    double avg = stats.getMean();

                    avgMap.put(key, avg);

                    // Update list of movies with that variance
                    List<String> currValueForVariance = ratingVarianceMap.getOrDefault(variance, new LinkedList<>());
                    currValueForVariance.add(key);
                    // Multiply variance with -1 as TreeMap returns values in ascending order
                    ratingVarianceMap.put(-variance, currValueForVariance);
                }
        );

        // More haxx... variables in lambdas must be immutable, so use an immutable array and only change its values
        final int[] count = {0};
        List<String> topTenMovieIDs = new LinkedList<>();
        ratingVarianceMap.entrySet().forEach(
                entry -> {
                    // Only search for top 10 movies
                    if (count[0] >= 10) return;

                    // Add movies to the result - if there were actually two movies with the exact same variance, the
                    // result will be bigger than 10.
                    List<String> ids = entry.getValue();
                    topTenMovieIDs.addAll(ids);
                    count[0] += ids.size();
                }
        );

        List<String> avgList = new LinkedList<>();
        for (String movieID : topTenMovieIDs) {
            final Double avg = avgMap.get(movieID);
            avgList.add(movieID + " " + avg);
            System.out.println("movieID = " + movieID);
            System.out.println("avg = " + avg);
        }

        // Save movies
        Files.write(Paths.get("data/top10.txt"), topTenMovieIDs);
        Files.write(Paths.get("data/top10avg.txt"), avgList);
    }
}
