package main;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import de.tudarmstadt.ukp.jwktl.parser.WiktionaryArticleParser;
import reader.Reader;
import reader.Review;
import stemmer.KehlbeckSperrleStemmer;
import stemmer.Stemmer;
import tokenizer.SplitSentences;
import tokenizer.Tokenizer;

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
		
		// Iterator with respective file names
		Iterator<String> movies = Arrays.asList("Harry", "X-Men", "Sherlock").iterator();
		
		// Tokenize the reviews
		for (StringBuilder stringBuilder : builders) {
			String collectedReviews = stringBuilder.toString();
			Tokenizer tokenizer = new Tokenizer(collectedReviews);
			try {
				String[] tokens = tokenizer.tokenize();
				
				Stemmer stemmer = new KehlbeckSperrleStemmer();
				String[] stems = new String[tokens.length];
				for (int i = 0; i < tokens.length; i++) {
					stems[i] = stemmer.stem(tokens[i]);
				}
				
				// Save results to file
				//Files.write(Paths.get(String.format("data/%s.txt", movies.next())), Arrays.asList(stems));
				WordCloudCreator creator = new WordCloudCreator(String.format("data/%s.png", movies.next()), "data/whale.png", String.format("data/%s.txt", movies.next()));
				creator.createWordCloud();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		// Demo the sentence splitter
		SplitSentences sentence = new SplitSentences(reviews.get(1).getText());
		
		try {
			String[] result = sentence.tokenize();
			System.out.println(Arrays.toString(result));
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
}
