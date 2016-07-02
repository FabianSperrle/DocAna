package sentiment;

import pos.brill.BrillTagger;
import sentiment.util.Sentiment;
import stemmer.KehlbeckSperrleStemmer;
import stemmer.Stemmer;
import tokenizer.Tokenizer;

import java.io.IOException;

public class ImprovedSentimentClassifier {

    // Returns true iff a movie is classified as good, and false otherwise
    public static Sentiment classify(String review) throws IOException {
        Tokenizer tokenizer = new Tokenizer();
        Stemmer stemmer = new KehlbeckSperrleStemmer();
        BrillTagger tagger = new BrillTagger();

        String[] tokens = tokenizer.tokenize(review);
        String[] stems = stemmer.stem(tokens);
        String[] tags = tagger.tag(tokens);

        int positiveCount = 0;
        int negativeCount = 0;

        boolean notFlag = false;
        for (int i = 0; i < stems.length; i++) {
            String stem = stems[i];
            String tag = tags[i];

            // Some tags might be null if they word never appeared in the training corpus and can not be induced
            if (tag == null) continue;

            if (tag.contains("*")) notFlag = true;
            else if (notFlag && stopNotInfluence(tag)) notFlag = false;

            if (SentimentWords.positive.contains(stem)) {
                if (notFlag) negativeCount++;
                else positiveCount++;
            }
            if (SentimentWords.negative.contains(stem)) {
                if (notFlag) positiveCount++;
                else negativeCount++;
            }
        }

        if (positiveCount > negativeCount) return Sentiment.GOOD;
        if (positiveCount < negativeCount) return Sentiment.BAD;
        return Sentiment.UNDEFINED;
    }

    /**
     * Checks whether the current tag stops the influence of a possible preceding negation
     *
     * @param tag The current tag
     * @return whether the tag stops the influence
     */
    private static boolean stopNotInfluence(String tag) {
        return tag.equals(".") || tag.equals(",") || tag.equals(":") || tag.equals(";");
    }
}
