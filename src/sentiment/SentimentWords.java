package sentiment;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SentimentWords {
    public static Set<String> positive = new HashSet<>(Arrays.asList(
            "great",
            "wonder", // wonderful
            "amaz", // amazing
            "flawless",
            "power", //powerful
            "marvel", //marvelous
            "excel", // excellent
            "awesom",
            "outstand", //outstanding
            "heartwarm", // heartwarming
            "brilliant",
            "recommend",
            "love",
            "best"

    ));
    public static Set<String> negative = new HashSet<>(Arrays.asList(
            "absurd",
            "unbeliev", // unbelievable
            "worst",
            "ridicul", // ridiculous
            "disappoint", // disappointed
            "lack", // lacking
            "terribl",
            "hate",
            "weird",
            "stai", // Stay away
            "predictabl",
            "suck",
            "awful",
            "unwatchabl",
            "bor", // boring
            "stupid",
            "wast" // waste
    ));

    public static Set<String> positive() {
        return positive;
    }

    public static Set<String> negative() {
        return negative;
    }
}
