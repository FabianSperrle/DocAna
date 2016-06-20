package ui;

import pos.brill.BrillTagger;
import pos.hmm.ViterbiTagger;
import reader.Reader;
import reader.Review;
import similarity.TF_IDF;
import stemmer.KehlbeckSperrleStemmer;
import stemmer.Stemmer;
import tokenizer.SentenceSplitter;
import tokenizer.Tokenizer;

import javax.swing.*;

import main.Similarity;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CompareReviews extends JFrame {
	private JSplitPane splitPaneH;
	private JSplitPane splitPaneV;
	private JPanel panel3;
	private CompareJPanel panelLeft;
	private CompareJPanel panelRight;
	private StringBuilder textInput;
	static List<Review> reviews;

	Tokenizer tok;
	SentenceSplitter sp;
	BrillTagger bt;

	JLabel overall;
	JLabel nouns;
	JLabel style;
	JLabel own;


	public CompareReviews(Integer[] reviewsList) {

		setTitle("Compare Reviews");
		setBackground(Color.gray);

		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());
		getContentPane().add(topPanel);

		// Create the panels
		panelLeft = new CompareJPanel(reviewsList, reviews);
		panelLeft.setLayout(new BoxLayout(panelLeft,
				BoxLayout.PAGE_AXIS));

		panelRight = new CompareJPanel(reviewsList, reviews);
		panelRight.setLayout(new BoxLayout(panelRight,
				BoxLayout.PAGE_AXIS));

		splitPaneV = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        topPanel.add(splitPaneV, BorderLayout.CENTER);
        
        //Panel for values, need to change
        createPanel3();

		// Create a splitter pane
		splitPaneH = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelLeft, panelRight);
		splitPaneV.setLeftComponent(splitPaneH);
        splitPaneV.setRightComponent(panel3);

		
	}
	
	private void createPanel3(){
		panel3 = new JPanel();
		JButton compute = new JButton("Compute Similarity!");
		panel3.add(compute);
		compute.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				List<String> list= new ArrayList<String>();
				list.add(reviews.get(panelLeft.getComboBox().getSelectedIndex()).getText());
				list.add(reviews.get(panelRight.getComboBox().getSelectedIndex()).getText());
				double[][] results = calculateDistance(list);
				String ov = String.valueOf(results[0][1]);
				overall.setText(ov);

				
				panelLeft.getComboBox().getSelectedItem();
			}
		});

		//Create the UI for displaying result.
		JLabel overallLabel = new JLabel("Overall Content Similarity",
				JLabel.LEADING); //== LEFT
		overall = new JLabel(" ");
		overall.setName("Overall");
		overall.setForeground(Color.black);
		panel3.add(overallLabel);
		panel3.add(overall);

		JLabel nounsLabel = new JLabel("Noun Similarity",
				JLabel.LEADING); //== LEFT
		nouns = new JLabel(" ");
		nouns.setForeground(Color.black);
		panel3.add(nounsLabel);
		panel3.add(nouns);

		JLabel styleLabel = new JLabel("Writing Style Similarity",
				JLabel.LEADING); //== LEFT
		style = new JLabel(" ");
		style.setForeground(Color.black);
		panel3.add(styleLabel);
		panel3.add(style);

		JLabel ownLabel = new JLabel("Our own similarity measure",
				JLabel.LEADING); //== LEFT
		own = new JLabel(" ");
		own.setForeground(Color.black);
		panel3.add(ownLabel);
		panel3.add(own);

		tok = new Tokenizer();
		sp = new SentenceSplitter();
		bt = null;
		try {
			bt = new BrillTagger();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private double[][] calculateDistance(List<String> reviewsToCompare){

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


	public static void main(String args[]) throws IOException{
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception evt) {
		}
		String filePath = "data/docAnaTextSample.rtf";
		Reader reader = new Reader(filePath);

		// Read the input and clean the data
		reviews = reader.readFile();
		reviews.remove(0);

		Integer[] reviewsList = IntStream.range(1, reviews.size() + 1).boxed().toArray( Integer[]::new );

		// Create an instance of the test application
		CompareReviews mainFrame = new CompareReviews(reviewsList);
		mainFrame.pack();
		mainFrame.setVisible(true);
	}
}
