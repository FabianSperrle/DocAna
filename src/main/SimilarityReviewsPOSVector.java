package main;

import pos.brill.BrillTagger;
import reader.Reader;
import reader.Review;
import similarity.TF_IDF;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SimilarityReviewsPOSVector {
    private static List<String> tagSet;
    public static void main(String[] args) throws IOException {

        String filePath = "data/reviews.rtf";
        Reader reader = new Reader(filePath);

        tagSet = Files.readAllLines(Paths.get("data/brill/taglist.txt"));

        // Read the input and clean the data
        List<Review> reviews = reader.readFile();
        reviews.remove(0);

        final List<String> top20 = reviews.stream()
                .filter(r -> SimilarityReviewsPOSVector.filter(r.getProduct().getProductID()))
                .map(r -> r.getText())
                .collect(Collectors.toList());

        BrillTagger bt = new BrillTagger("data/brill/lex.txt", "data/brill/endlex.txt", "data/brill/rules.txt");
        List<Double[]> tags = top20.stream()
                .map(review -> bt.tag(review))
                .map(SimilarityReviewsPOSVector::getPosTagHistogram)
                .filter(r -> r != null)
                .collect(Collectors.toList());

        double[][] similarity = new double[tags.size()][tags.size()];
        for (int i = 0; i < tags.size(); i++) {
            for (int j = 0; j < tags.size(); j++) {
                Double[] rev1 = tags.get(i);
                Double[] rev2 = tags.get(j);

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

    }

    public static Double[] getPosTagHistogram(String[] tags) {
        final Map<String, Integer> counts = Arrays.asList(tags).stream()
                .collect(Collectors.toMap(tag -> tag,
                        count -> 1,
                        (c1, c2) -> c1 + c2));

        Double[] hist = new Double[tagSet.size()];
        for (int i = 0; i < hist.length; i++) {
            hist[i] = (counts.containsKey(tagSet.get(i)) ? (double) counts.get(tagSet.get(i)) : 0.0);
        }
        if (Arrays.asList(hist).stream().mapToDouble(Double::doubleValue).sum() == 0) {
            return null;
        }
        return hist;
    }
    private static boolean filter(String id) {
        switch (id) {
            case "B000KKQNRO":
            //case "B004WO6BPS":
            //case "B009NQKPUW":
                return true;
            default:
                return false;
        }
    }
}
