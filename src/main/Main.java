package main;

import pos.hmm.ViterbiTagger;
import reader.Reader;
import reader.Review;
import stemmer.KehlbeckSperrleStemmer;
import stemmer.Stemmer;
import tokenizer.SentenceSplitter;
import tokenizer.Tokenizer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Main {

	public static void main(String[] args) throws IOException{
		//we have truncated the data file for easier upload in ilias, as there is a 40 MB size limit
		String filePath = "data/docAnaTextSample.rtf";
		Reader reader = new Reader(filePath);
		
		// Read the input and clean the data
		List<Review> reviews = reader.readFile();
		reviews.remove(0);
		
		// prepare string builders to collect all reviews of the 3 movies
		StringBuilder[] builders = new StringBuilder[3];
		builders[0] = new StringBuilder();
		builders[1] = new StringBuilder();
		builders[2] = new StringBuilder();

		for (Review review : reviews) {
			String productID = review.getProduct().getProductID();
			//Harry Potter 7.1
			if (productID.equals("B004WO6BPS")) {
				builders[0].append(review.getText());
			} // X-Men
			else if (productID.equals("B000IMM3XW")) {
				builders[1].append(review.getText());
			} // Sherlock
			else if (productID.equals("B00005YTR8")) {
				builders[2].append(review.getText());
			}
		}
		ViterbiTagger v = new ViterbiTagger("data/brown");
		// Iterator with respective file names
		Iterator<String> movies = Arrays.asList("Harry", "X-Men", "Sherlock").iterator();
		
		// Tokenize the reviews
		for (StringBuilder stringBuilder : builders) {
			String collectedReviews = stringBuilder.toString();
			Tokenizer tokenizer = new Tokenizer();
			try {
				String[] tokens = tokenizer.tokenize(collectedReviews);
				
				Stemmer stemmer = new KehlbeckSperrleStemmer();
				String[] stems = new String[tokens.length];
				String[] pos = new String[tokens.length];
				for (int i = 0; i < tokens.length; i++) {
					stems[i] = stemmer.stem(tokens[i]);
				}
				ArrayList<String> NN =new ArrayList<String>();
				ArrayList<String> AP =new ArrayList<String>();
				ArrayList<String> VB =new ArrayList<String>();
				for (int i = 0; i < tokens.length; i++) {
					if (pos[i] != null) {
						if (pos[i].equals("nn")){
							NN.add(stems[i]);
						}else if (pos[i].equals("ap")){
							AP.add(stems[i]);
						}else if (pos[i].equals("vb")){
							VB.add(stems[i]);
						}
					}
				}
				// Save results to file, create Word Clouds
				String curr = movies.next();
				Files.write(Paths.get(String.format("data/%sNN.txt", curr)), NN);
				WordCloudCreator creator = new WordCloudCreator(String.format("data/%sNN.png", curr), "data/whale.png", String.format("data/%sNN.txt", curr));
				
				Files.write(Paths.get(String.format("data/%sAP.txt", curr)), AP);
				WordCloudCreator creator2 = new WordCloudCreator(String.format("data/%sAP.png", curr), "data/whale.png", String.format("data/%sAP.txt", curr));
				
				Files.write(Paths.get(String.format("data/%sVB.txt", curr)), VB);
				WordCloudCreator creator3 = new WordCloudCreator(String.format("data/%sVB.png", curr), "data/whale.png", String.format("data/%sVB.txt", curr));
				
				creator.createWordCloud();
				creator2.createWordCloud();
				creator3.createWordCloud();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		// Demo the sentence splitter
		SentenceSplitter sentence = new SentenceSplitter();
		
		try {
			String[] result = sentence.split(reviews.get(1).getText());
			//System.out.println(Arrays.toString(result));
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
}
