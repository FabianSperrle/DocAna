package pos.brill;

/**
 * Created by fabian on 30/05/16.
 */
public class Predicate {
    private PredicateName name;
    private String param1;
    private String param2;

    public Predicate(String name, String param1, String param2) {
        this.name = PredicateName.valueOf(name);
        this.param1 = param1;
        this.param2 = param2;
    }

    @Override
    public String toString() {
        return name.toString() + " " + param1;
    }

    public boolean evaluate(String[] tags, String[] tokens, int pos) {
        try {
            switch (this.name) {
                case PREVTAG:
                    return this.prevtag(tags, pos);
                case NEXTTAG:
                    return this.nexttag(tags, pos);
                case PREV2TAG:
                    return this.prev2tag(tags, pos);
                case NEXT2TAG:
                    return this.next2tag(tags, pos);
                case PREV1OR2OR3TAG:
                    return this.prev1or2or3tag(tags, pos);
                case NEXT1OR2OR3TAG:
                    return this.next1or2or3tag(tags, pos);
                case PREV1OR2TAG:
                    return this.prev1or2tag(tags, pos);
                case NEXT1OR2TAG:
                    return this.next1or2tag(tags, pos);
                case SURROUNDTAG:
                    return this.surroundtag(tags, pos);
                case PREVBIGRAM:
                    return this.prevbigram(tags, pos);
                case NEXTBIGRAM:
                    return this.nextbigram(tags, pos);
                case RBIGRAM:
                    return this.rbigram(tokens, pos);
                case LBIGRAM:
                    return this.lbigram(tokens, pos);
                /*case WDAND2AFT:
                    return this.wdand2aft(tokens, pos);
                case PREV1OR2WD:
                    return this.prev1or2word(tokens, pos);
                case WDNEXTTAG:
                    return this.wdnexttag(tags, tokens, pos);
                case WDPREVTAG:
                    return this.wdprevtag(tags, tokens, pos);
                case PREVWD:
                    return this.prevwd(tokens, pos);
                case CURWD:
                    return this.curwd(tokens, pos);
                case NEXTWD:
                    return this.nextwd(tokens, pos);
                case WDAND2TAGAFT:
                    return this.wdand2tagaft(tags, tokens, pos);
                case WDAND2TAGBFR:
                    return this.wdand2tagfr(tags, tokens, pos);*/
                default:
                    return false;
                    //throw new RuntimeException("Unknown brill tagger rule " + name);
            }
        } catch (IndexOutOfBoundsException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
    }

    private boolean prevtag(String[] tags, int pos) {
        return tags[pos - 1].equals(this.param1);
    }

    private boolean nexttag(String[] tags, int pos) {
        return tags[pos + 1].equals(this.param1);
    }
    private boolean prev2tag(String[] tags, int pos) {
        return tags[pos - 2].equals(this.param1);
    }

    private boolean next2tag(String[] tags, int pos) {
        return tags[pos + 2].equals(this.param1);
    }

    private boolean prev1or2or3tag(String[] tags, int pos) {
        return tags[pos - 1].equals(this.param1) ||
                tags[pos - 2].equals(this.param1) ||
                tags[pos - 3].equals(this.param1);
    }

    private boolean next1or2or3tag(String[] tags, int pos) {
        return tags[pos + 1].equals(this.param1) ||
                tags[pos + 2].equals(this.param1) ||
                tags[pos + 3].equals(this.param1);
    }
    private boolean prev1or2tag(String[] tags, int pos) {
        return tags[pos - 1].equals(this.param1) ||
                tags[pos - 2].equals(this.param1);
    }

    private boolean next1or2tag(String[] tags, int pos) {
        return tags[pos + 1].equals(this.param1) ||
                tags[pos + 2].equals(this.param1);
    }

    private boolean wdand2aft(String[] tokens, int pos) {
        return tokens[pos].equals(this.param1) &&
                tokens[pos + 2].equals(this.param2);
    }

    private boolean prev1or2word(String[] tokens, int pos) {
        return tokens[pos - 1].equals(this.param1) ||
                tokens[pos - 2].equals(this.param1);
    }

    private boolean wdnexttag(String[] tags, String[] tokens, int pos) {
        return tokens[pos].equals(this.param1) && tags[pos + 1].equals(this.param2);
    }

    private boolean wdprevtag(String[] tags, String[] tokens, int pos) {
        return tokens[pos].equals(this.param1) && tags[pos - 1].equals(this.param2);
    }

    private boolean surroundtag(String[] tags, int pos) {
        return tags[pos - 1].equals(this.param1) && tags[pos + 1].equals(this.param2);
    }

    private boolean prevbigram(String[] tags, int pos) {
        return tags[pos - 2].equals(this.param1) && tags[pos - 1].equals(this.param2);
    }

    private boolean nextbigram(String[] tags, int pos) {
        return tags[pos + 1].equals(this.param1) && tags[pos + 2].equals(this.param2);
    }
    private boolean prevwd(String[] tokens, int pos) {
        return tokens[pos - 1].equals(this.param1);
    }

    private boolean curwd(String[] tokens, int pos) {
        return tokens[pos].equals(this.param1);
    }

    private boolean nextwd(String[] tokens, int pos) {
        return tokens[pos + 1].equals(this.param1);
    }

    private boolean wdand2tagaft(String[] tags, String[] tokens, int pos) {
        return tokens[pos].equals(this.param1) && tags[pos + 2].equals(this.param2);
    }

    private boolean wdand2tagfr(String[] tags, String[] tokens, int pos) {
        return tokens[pos].equals(this.param1) && tags[pos - 2].equals(this.param2);
    }
    private boolean rbigram(String[] tokens, int pos) {
        return tokens[pos].equals(this.param1) && tokens[pos + 1].equals(this.param2);
    }

    private boolean lbigram(String[] tokens, int pos) {
        return tokens[pos].equals(this.param1) && tokens[pos - 1].equals(this.param2);
    }
}
