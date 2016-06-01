package pos.brill;

/**
 * Created by fabian on 30/05/16.
 */
public class Rule {
    String tag1;
    String tag2;
    Predicate predicate;

    public Rule(String tag1, String tag2, String predicate, String param1, String param2) {
        this.tag1 = tag1;
        this.tag2 = tag2;
        this.predicate = new Predicate(predicate, param1, param2);
    }

    public Rule(String[] elems) {
        this.tag1 = elems[0];
        this.tag2 = elems[1];
        if (elems.length == 4)
            this.predicate = new Predicate(elems[2], elems[3], null);
        else
            this.predicate = new Predicate(elems[2], elems[3], elems[4]);
    }

    public void apply(String[] tags, String[] tokens, int pos) {
        if (tags[pos] != null && tags[pos].equals(tag1)) {
            if (this.predicate.evaluate(tags, tokens, pos)) {
                tags[pos] = tag2;
            }
        }
    }

    @Override
    public String toString() {
        return this.tag1 + " " + this.tag2 + " " + this.predicate.toString();
    }
}
