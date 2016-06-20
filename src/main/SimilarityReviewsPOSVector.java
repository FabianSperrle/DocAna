package main;

import pos.brill.BrillTagger;
import similarity.TF_IDF;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SimilarityReviewsPOSVector {
    List<String> tagSet;

    public SimilarityReviewsPOSVector() {
        try {
            tagSet = Files.readAllLines(Paths.get("data/brill/taglist.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public double[][] getPOSSimilarity(List<String> texts) {
        BrillTagger bt = null;
        try {
            bt = new BrillTagger();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<Double[]> tags = texts.stream()
                .map(bt::tag)
                .map(this::getPosTagHistogram)
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

        return similarity;
    }

    public Double[] getPosTagHistogram(String[] tags) {
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
}
