package sentiment;

import sentiment.util.Sentiment;
import stemmer.KehlbeckSperrleStemmer;
import stemmer.Stemmer;
import tokenizer.Tokenizer;

/**
 * Created by fabian on 02.07.2016.
 */
public class SentimentClassifier {

    // Returns true iff a movie is classified as good, and false otherwise
    public static Sentiment classify(String review) {
        Tokenizer tokenizer = new Tokenizer();
        Stemmer stemmer = new KehlbeckSperrleStemmer();

        String[] stems = stemmer.stem(tokenizer.tokenize(review));

        int positiveCount = 0;
        int negativeCount = 0;
        for (String stem : stems) {
            if (SentimentWords.positive().contains(stem)) positiveCount++;
            if (SentimentWords.negative().contains(stem)) negativeCount++;
        }

        if (positiveCount > negativeCount) return Sentiment.GOOD;
        if (positiveCount < negativeCount) return Sentiment.BAD;
        return Sentiment.UNDEFINED;
    }
}
