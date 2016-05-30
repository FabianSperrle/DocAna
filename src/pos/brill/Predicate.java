package pos.brill;

/**
 * Created by fabian on 30/05/16.
 */
public class Predicate {
    private String name;
    private String param1;
    private String param2;

    public Predicate(String name, String param1, String param2) {
        this.name = name;
        this.param1 = param1;
        this.param2 = param2;
    }

    public boolean evaluate(String[] sentence, int pos) {
        switch (this.name) {
            case "PREVTAG":
                return this.prevtag(sentence, pos);
            default:
                throw new RuntimeException("Unknown brill tagger rule " + name);
        }
    }

    private boolean prevtag(String[] sentence, int pos) {
        return sentence[pos].equals(this.param1);
    }
}
