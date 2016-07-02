package ui;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;
import main.ReviewToAuthor;
import main.SimilarityReviewsNouns;
import main.SimilarityReviewsPOSVector;
import pos.brill.BrillTagger;
import reader.Reader;
import reader.Review;
import similarity.TF_IDF;
import tokenizer.SentenceSplitter;
import tokenizer.Tokenizer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CompareReviews extends JFrame {
    private static class Tuple {
        private String user;
        private String review;
        private Double[] stats;

        private Tuple(String user, String review) {
            this.user = user;
            this.review = review;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getReview() {
            return review;
        }

        public void setReview(String review) {
            this.review = review;
        }

        private Double[] getStats() {
            return stats;
        }

        private void setStats(Double[] stats) {
            this.stats = stats;
        }
    }

    private JSplitPane splitPaneH;
    private JSplitPane splitPaneV;
    private JPanel panel3;
    private CompareJPanel panelLeft;
    private CompareJPanel panelRight;
    private StringBuilder textInput;
    private static List<Review> reviews;

    private Tokenizer tok;
    private SentenceSplitter sp;
    private BrillTagger bt;

	private final Browser browser = new Browser();
    private JLabel overall;
    private JLabel nouns;
    private JLabel style;
    private JLabel own;

    public CompareReviews(Integer[] reviewsList) {
        super();
        
		setTitle("Compare Reviews");
		setBackground(Color.gray);

		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());
		
		//getContentPane().add(topPanel);

		// Create the panels
		panelLeft = new CompareJPanel(reviewsList, reviews);
		panelLeft.setLayout(new BoxLayout(panelLeft,
				BoxLayout.PAGE_AXIS));
		panelLeft.setMinimumSize(new Dimension(800, 300));

		panelRight = new CompareJPanel(reviewsList, reviews);
		panelRight.setLayout(new BoxLayout(panelRight,
				BoxLayout.PAGE_AXIS));
		panelRight.setMinimumSize(new Dimension(800, 300));

        splitPaneV = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        topPanel.add(splitPaneV, BorderLayout.CENTER);

        //Panel for values, need to change
        createPanel3();

        // Create a splitter pane
        splitPaneH = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelLeft, panelRight);
        splitPaneV.setLeftComponent(splitPaneH);
        splitPaneV.setRightComponent(panel3);
        
        BrowserView browserView = new BrowserView(browser);
        browserView.setSize(new Dimension(300,200));

        
        super.add(topPanel, BorderLayout.NORTH);
        super.add(browserView, BorderLayout.CENTER);
        URL url = null;
		try {
			url = Paths.get("data/graph.html").toUri().toURL();
		} catch (MalformedURLException e1) {
			System.out.println("error to get map.html!\n" + e1);
		}
        String filesPathAndName = url.getPath();
		browser.loadURL("file:" + filesPathAndName);


    }

    private void createPanel3() {
        tok = new Tokenizer();
        sp = new SentenceSplitter();
        bt = null;
        try {
            bt = new BrillTagger();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        final List<String> filtered = reviews.stream()
                .filter(r -> ReviewToAuthor.filter(r.getProduct().getProductID()))
                .map(Review::getText)
                .collect(Collectors.toList());
        panel3 = new JPanel();
        panel3.setLayout(new FlowLayout());
        JButton compute = new JButton("Compute Similarity!");
        panel3.add(compute);
        compute.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<String> list = new ArrayList<String>();
                list.add(reviews.get(panelLeft.getComboBox().getSelectedIndex()).getText());
                list.add(reviews.get(panelRight.getComboBox().getSelectedIndex()).getText());
                double[][] author_profile_similarity = calculateDistance(list);
                double[][] tf_idf = new TF_IDF(filtered).tf_idf(list);
                double[][] pos = new SimilarityReviewsPOSVector().getPOSSimilarity(list);
                double[][] noun = new SimilarityReviewsNouns().getNounSimilarity(list);
                String ov = String.format(Locale.ENGLISH, "%.4f", tf_idf[0][1] * 5);
                String ap = String.format(Locale.ENGLISH, "%.4f", author_profile_similarity[0][1]);
                String po = String.format(Locale.ENGLISH, "%.4f", pos[0][1]);
                String no = String.format(Locale.ENGLISH, "%.4f", noun[0][1] * 5);
                String str = new SimilarityReviewsPOSVector().getPOSTags(list);
                
                String script = " var data = [  {    className: 'germany', axes: [ ";
                script += "{axis: \"TF-IDF\", value:  " + ov + "},";
                script += "{axis: \"Author Profile\", value:  " + ap + "},";
                script += "{axis: \"POS Histogram\", value:  " + po + "},";
                script += "{axis: \"Noun TF-IDF\", value:  " + no + "},";
                script += "]}];RadarChart.draw(\".chart-container\", data);";
                
                browser.executeJavaScript(script);
                System.out.println(script);

//                overall.setText(ov);
//                style.setText(ap);
//                own.setText(po);
//                nouns.setText(no);
                overall.setText(ov);
                style.setText(ap);
                own.setText(po);
                nouns.setText(no);

                panelLeft.getComboBox().getSelectedItem();
            }
        });

        //Create the UI for displaying result.
        JLabel overallLabel = new JLabel("Overall Content Similarity",
                JLabel.LEADING); //== LEFT
        overall = new JLabel(" ");
        overall.setName("Overall");
        overall.setForeground(Color.black);
        panel3.add(overallLabel, BorderLayout.NORTH);
        panel3.add(overall, BorderLayout.CENTER);

        JLabel nounsLabel = new JLabel("Noun Similarity",
                JLabel.LEADING); //== LEFT
        nouns = new JLabel(" ");
        nouns.setForeground(Color.black);
        panel3.add(nounsLabel, BorderLayout.NORTH);
        panel3.add(nouns, BorderLayout.CENTER);

        JLabel styleLabel = new JLabel("Writing Style Similarity",
                JLabel.LEADING); //== LEFT
        style = new JLabel(" ");
        style.setForeground(Color.black);
        panel3.add(styleLabel, BorderLayout.NORTH);
        panel3.add(style, BorderLayout.CENTER);

        JLabel ownLabel = new JLabel("POS Histogram",
                JLabel.LEADING); //== LEFT
        own = new JLabel(" ");
        own.setForeground(Color.black);
        panel3.add(ownLabel, BorderLayout.NORTH);
        panel3.add(own, BorderLayout.CENTER);

    }

    private double[][] calculateDistance(List<String> reviewsToCompare) {

        final List<Double[]> featuresVectors = reviewsToCompare.stream()
                .map(review -> {
                    Double[] features = new Double[6];
                    List<String> tokens = Arrays.asList(tok.tokenize(review));
                    List<String> sentences = Arrays.asList(sp.split(review));
                    List<String> tags = null;
                    try {
                        tags = Arrays.asList(bt.tag(review));
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    // Average word length
                    features[0] = 11 * tokens.stream()
                            .mapToInt(String::length)
                            .average().orElse(0);
                    // percentage of distinct words
                    features[1] = 33 * ((double) tokens.stream().distinct().count()) / tokens.size();
                    // Percentage of words that appear exactly once
                    features[2] = 100 * (double) tokens.stream()
                            .filter(t -> Collections.frequency(tokens, t) == 1)
                            .count() / tokens.size();
                    // Average sentence length
                    features[3] = 0.1 * sentences.stream()
                            .map(tok::tokenize)
                            .mapToInt(t -> t.length)
                            .average().orElse(0);
                    // Phrases per sentence
                    features[4] = 20 * ((double) sentences.stream()
                            .flatMap(s -> Arrays.asList(tok.tokenize(s)).stream())
                            .filter(t -> t.equals(",") || t.equals(";") || t.equals(":") || t.equals("\""))
                            .count()) / (sentences.size() == 0 ? 1 : sentences.size());
                    // Distinct tags
                    features[5] = 200 * ((double) tags.stream().distinct().count()) / tags.size();

                    return features;

                })
                .collect(Collectors.toList());

        double[][] similarity = new double[featuresVectors.size()][featuresVectors.size()];
        for (int i = 0; i < featuresVectors.size(); i++) {
            for (int j = 0; j < featuresVectors.size(); j++) {
                Double[] rev1 = featuresVectors.get(i);
                Double[] rev2 = featuresVectors.get(j);
                //System.arraycopy(featuresVectors.get(i), 4, rev1, 0, 2);
                //System.arraycopy(featuresVectors.get(j), 4, rev2, 0, 2);
                double cos = TF_IDF.cosineSimilarity(rev1, rev2);
                similarity[i][j] = cos;
            }
        }

        return similarity;
    }


    public static void main(String args[]) throws IOException {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception evt) {
            evt.printStackTrace();
        }
        String filePath = "data/reviews.rtf";
        Reader reader = new Reader(filePath);

        // Read the input and clean the data
        reviews = reader.readFile();
        reviews.remove(0);

        Integer[] reviewsList = IntStream.range(1, reviews.size() + 1).boxed().toArray(Integer[]::new);

        // Create an instance of the test application
        CompareReviews mainFrame = new CompareReviews(reviewsList);
        mainFrame.pack();
        mainFrame.setVisible(true);
    }
}
