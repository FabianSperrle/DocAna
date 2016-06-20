package main;

import pos.brill.BrillTagger;
import similarity.TF_IDF;
import stemmer.KehlbeckSperrleStemmer;
import tokenizer.Tokenizer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class SimilarityReviewsNouns {
    private List<String> tagSet;
    private Tokenizer tokenizer;
    private BrillTagger bt;

    public SimilarityReviewsNouns() {
        try {
            tagSet = Files.readAllLines(Paths.get("data/brill/taglist.txt"));
            bt = new BrillTagger();
            tokenizer = new Tokenizer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public double[][] getNounSimilarity(List<String> texts) {
        KehlbeckSperrleStemmer st = new KehlbeckSperrleStemmer();
        final List<String> np = texts.stream()
                .map(text -> {
                    String[] tokens = tokenizer.tokenize(text);
                    String[] tags = bt.tag(tokens);

                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < tokens.length; i++) {
                        if (tags[i] == null) {
                            continue;
                        }
                        if (tags[i].contains("nn")) {
                            sb.append(st.stem(tokens[i]));
                            sb.append(" ");
                        }
                    }

                    System.out.println("sb.toString() = " + sb.toString());
                    return sb.toString();
                })
                .collect(Collectors.toList());

        return new TF_IDF(np).tf_idf(np);
    }
}
