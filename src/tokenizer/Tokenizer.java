package tokenizer;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import pos.brill.BrillTagger;
import pos.hmm.ViterbiTagger;
import stemmer.KehlbeckSperrleStemmer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by fabian on 10.06.2016.
 */
public class Tokenizer {
    Logger logger = LogManager.getLogger(Tokenizer.class);

    public static void main(String[] args) throws IOException {
        Tokenizer tok = new Tokenizer();
        String sentence = "I spent my entire life from the age of six hearing great things about this movie. Things like 'Dont watch this movie alone' and 'This is one of the best horror films ever made,'so as you can imagine I took the first chance I could to buy it when I saw a colour version for \\'a34 at a market stall. I just couldn't wait to go home and put it into my VCR. As soon as the film started it hit me that I didnt have a clue what anyone was saying. By the end of the film I was ready to go to sleep. The zombies were just random people with a bit of face paint on and weren't scary at all, but then again what were they to for special effects back then. I just thaught never mind and put it away to the back of my video collection. A couple of years later while being really bored in the school holidays I decided to play it again. This time it was later on at night, so I drew the curtains and turned all of the lights off (its the only way to watch this film really). This time around I found it a little less boring. I refrained myself from falling asleep listening to the ramblings and bad acting and found that this time around I enjoyed it a lot more and in the dark the atmosphere seemed to grow on me. I started to undestand then why it was considered to be a 'great' horror movie. These days I would not call it that though seeing as I am a victim of modern standards addicted to million dollar special effects and computer generated images but for any collector of classic horror its a must. My advice would be to pick up a more recent copy like the 30th anniversary addition as hopefully the sound and picture have been tweaked slightly so to erase that feeling of 'what the hell is going on'. If any horror film (or any film) fans want to E-Mail me and talk im at ...\\";
        String[] tokens = tok.tokenize(sentence);
        System.out.println("Arrays.toString(tokens) = " + Arrays.toString(tokens));

        KehlbeckSperrleStemmer stemmer = new KehlbeckSperrleStemmer();
        String[] stems = stemmer.stem(tokens);
        System.out.println("Arrays.toString(stems) = " + Arrays.toString(stems));

        SentenceSplitter splitter = new SentenceSplitter();
        String[] sentences = splitter.split(sentence);
        for (String s : sentences) {
            System.out.println("s = " + s);
        }

        BrillTagger bt = new BrillTagger("data/brill/lex.txt", "data/brill/endlex.txt", "data/brill/rules.txt");
        for (String s : sentences) {
            String[] tags = bt.tag(s);
            System.out.println("Arrays.toString(tags) = " + Arrays.toString(tags));
        }

        ViterbiTagger vt = new ViterbiTagger("data/brown");
        for (String s : sentences) {
            String[] tags = vt.getTagList(s);
            System.out.println("Arrays.toString(tags) = " + Arrays.toString(tags));
        }
    }

    public String[] tokenize(String text) {
        // https://regex101.com/r/dM0yY9/4
        text = text.replaceAll("<.*?>", "");
        System.out.println("text = " + text);
        Pattern stuff = Pattern.compile("(?:(?<=[\"])(?:\\w+\\s){1,3}\\w+(?=[\"])|'\\d+|[a-zA-Z']+|(?<=[a-zA-Z])-(?=[a-zA-Z])|(?<=[a-zA-Z])&(?=[a-zA-Z]))+|(?:[-.\\p{Sc}]?\\d+(?:\\.\\d+)?)[%\\p{Sc}]?|[.,!?;():\"]+|--");
        Matcher matcher = stuff.matcher(text);
        List<String> tokens = new ArrayList<String>();
        while (matcher.find()) {
            tokens.add(matcher.group(0)); // add match to the list
        }

        for (int i = 0; i < tokens.size(); i++) {
            try {
                String token = tokens.get(i);
                if (Character.isUpperCase(token.charAt(0))) {
                    String next;
                    while (Character.isUpperCase((next = tokens.get(i + 1)).charAt(0))) {
                        token += " " + tokens.get(i + 1);
                        tokens.remove(i + 1);
                    }
                }

                if (tokens.get(i + 1).equals(".") && tokens.get(i + 3).startsWith(".")) {
                    token = tokens.get(i) + "." + tokens.get(i + 2) + ".";
                    tokens.remove(i + 1);
                    tokens.remove(i + 1);
                    tokens.remove(i + 1);
                }

                switch (token) {
                    case "aren't":
                    case "can't":
                    case "couldn't":
                    case "didn't":
                    case "doesn't":
                    case "don't":
                    case "hadn't":
                    case "hasn't":
                    case "isn't":
                    case "mightn't":
                    case "mustn't":
                    case "shan't":
                    case "shouldn't":
                    case "weren't":
                    case "wouldn't":
                    case "haven't":
                        token = token.substring(0, token.length() - 3);
                        tokens.add(i + 1, "not");
                        break;

                    case "won't":
                        token = "will";
                        tokens.add(i + 1, "not");
                        break;

                    case "ain't":
                        token = "are";
                        tokens.add(i + 1, "not");
                        break;

                    case "he'd":
                    case "I'd":
                    case "she'd":
                    case "they'd":
                    case "we'd":
                    case "you'd":
                        token = token.substring(0, token.length() - 2);
                        tokens.add(i + 1, "would");
                        break;

                    case "he'll":
                    case "I'll":
                    case "she'll":
                    case "they'll":
                    case "what'll":
                    case "who'll":
                    case "you'll":
                        token = token.substring(0, token.length() - 3);
                        tokens.add(i + 1, "will");
                        break;

                    case "he's":
                    case "she's":
                    case "that's":
                    case "there's":
                    case "what's":
                    case "let's":
                    case "where's":
                    case "who's":
                    case "here's":
                        token = token.substring(0, token.length() - 2);
                        tokens.add(i + 1, "is");
                        break;

                    case "I'm":
                        token = "I";
                        tokens.add(i + 1, "am");
                        break;

                    case "we're":
                    case "who're":
                    case "they're":
                    case "what're":
                    case "you're":
                        token = token.substring(0, token.length() - 3);
                        tokens.add(i + 1, "are");
                        break;

                    case "I've":
                    case "they've":
                    case "we've":
                    case "what've":
                    case "who've":
                    case "you've":
                        token = token.substring(0, token.length() - 3);
                        tokens.add(i + 1, "have");
                        break;

                    case "Ave":
                        token = "avenue";
                        break;
                    case "Blvd":
                        token = "boulevard";
                        break;
                    case "Ln":
                        token = "lane";
                        break;
                    case "Rd":
                        token = "road";
                        break;
                    case "St":
                        token = "street";
                        break;

                }
                tokens.set(i, token);
            } catch (IndexOutOfBoundsException e) {

            }
        }

        return tokens.toArray(new String[tokens.size()]);
    }
}
