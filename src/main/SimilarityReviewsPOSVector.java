package main;

import pos.brill.BrillTagger;
import reader.Reader;
import reader.Review;
import similarity.TF_IDF;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
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
        List<int[]> tags = top20.stream()
                .map(review -> bt.tag(review))
                .map(SimilarityReviewsPOSVector::getPosTagHistogram)
                .collect(Collectors.toList());
        System.out.println("tags = " + tags.size());

        TF_IDF tf_idf = new TF_IDF(new LinkedList<>());

    }

    public static int[] getPosTagHistogram(String[] tags) {
        final Map<String, Integer> counts = Arrays.asList(tags).stream()
                .collect(Collectors.toMap(tag -> tag,
                        count -> 1,
                        (c1, c2) -> c1 + c2));

        int[] hist = new int[tagSet.size()];
        for (int i = 0; i < hist.length; i++) {
            hist[i] = counts.containsKey(tagSet.get(i)) ? counts.get(tagSet.get(i)) : 0;
        }
        return hist;
    }
    private static boolean filter(String id) {
        switch (id) {
            case "B002LBKDYE":
            //case "B004WO6BPS":
            //case "B009NQKPUW":
                return true;
            default:
                return false;
        }
    }
}
