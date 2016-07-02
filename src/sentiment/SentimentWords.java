package sentiment;

import java.util.*;

public class SentimentWords {
    private static Set<String> positive = new HashSet<>(Arrays.asList(
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
    private static Set<String> negative = new HashSet<>(Arrays.asList(
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

    private static Map<String, Double> modifiers;

    static {
        modifiers = new HashMap<>();

        modifiers.put("absolut", 2.0);
        modifiers.put("assured", 2.0);
        modifiers.put("certainli", 2.0);
        modifiers.put("complet", 2.0);
        modifiers.put("conclus", 2.0);
        modifiers.put("definit", 2.0);
        modifiers.put("desper", 2.0);
        modifiers.put("distinct", 2.0);
        modifiers.put("entir", 2.0);
        modifiers.put("extrem", 2.0);
        modifiers.put("fulli", 2.0);
        modifiers.put("high", 2.0);
        modifiers.put("incredibbl", 2.0);
        modifiers.put("indisputabbl", 2.0);
        modifiers.put("particular", 2.0);
        modifiers.put("perfect", 2.0);
        modifiers.put("realli", 2.0);
        modifiers.put("respect", 2.0);
        modifiers.put("signific", 2.0);
        modifiers.put("sole", 2.0);
        modifiers.put("specif", 2.0);
        modifiers.put("sure", 2.0);
        modifiers.put("total", 2.0);
        modifiers.put("thorough", 2.0);
        modifiers.put("truli", 2.0);
        modifiers.put("unbelievabbl", 2.0);
        modifiers.put("undeniabbl", 2.0);
        modifiers.put("undisput", 2.0);
        modifiers.put("undoubted", 2.0);
        modifiers.put("unquestionabbl", 2.0);
        modifiers.put("utter", 2.0);
    }

    public static Set<String> positive() {
        return positive;
    }

    public static Set<String> negative() {
        return negative;
    }

    public static Double getModifiers(String tag) {
        if (modifiers.containsKey(tag)) {
            return modifiers.get(tag);
        }
        return 1.0;
    }
}
