package main;

import pos.brill.BrillTagger;
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
import java.util.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

	public static void main(String[] args) throws IOException{
		//we have truncated the data file for easier upload in ilias, as there is a 40 MB size limit
		String filePath = "data/docAnaTextSample.rtf";
		Reader reader = new Reader(filePath);

		// Read the input and clean the data
		List<Review> reviews = reader.readFile();
		reviews.remove(0);

		// prepare string builders to collect all reviews 
		StringBuilder[] builders = new StringBuilder[13];
		for (int i = 0; i < builders.length; i++){
			builders[i] = new StringBuilder();
		}
		List<String> mov = new ArrayList<String>();
		String productID = null;



		for (Review review : reviews) {
			productID = review.getProduct().getProductID();
			Date date = new Date(Long.parseLong("1299110400")*1000);
			if(review.getTime().equals(date)){
				//System.out.println(review.getTime());
				System.out.println(productID);
			}
			//
			//			switch (productID) {
			//            case "B001NFNFMQ":
			//            case "B000KKQNRO":
			//            case "B009NQKPUW":
			//            case "B001NFNFMQ":
			//            case "B008PZZND6":
			//            case "B001NFNFMQ":
			//            case "B006TTC57C":
			//            case "7883704540":
			//            case "B006TTC57C":
			//            case "B006TTC57C":
			//            case "B000067JG4":
			//            case "7883704540":
			//				builders[0].append(review.getText());
			//				mov.add(productID);
			//				break;
			//            default:
			//                break;
			//        }
			if (productID.equals("B001NFNFMQ")) {
				builders[0].append(review.getText());
				mov.add(productID);
			} // 
			else if (productID.equals("B000KKQNRO")) {
				builders[1].append(review.getText());
				mov.add(productID);
			} // 
			else if (productID.equals("B009NQKPUW")) {
				builders[2].append(review.getText());
				mov.add(productID);

			}else if (productID=="B001NFNFMQ") {
				builders[3].append(review.getText());
				mov.add(productID);
				System.out.println(productID);
			} // 
			else if (productID.equals("B008PZZND6")) {
				builders[4].append(review.getText());
				mov.add(productID);
			}else if (productID.equals("B001NFNFMQ")) {
				builders[5].append(review.getText());
				mov.add(productID);
			} // 
			else if (productID.equals("B006TTC57C")) {
				builders[6].append(review.getText());
				mov.add(productID);
			}else if (productID.equals("7883704540")) {
				builders[7].append(review.getText());
				mov.add(productID);
			} // 
			else if (productID.equals("B000VBJEFK")) {
				builders[8].append(review.getText());
				mov.add(productID);
			}else if (productID.equals("B006TTC57C")) {
				builders[9].append(review.getText());
				mov.add(productID);
			} // 
			else if (productID.equals("B000067JG4")) {
				builders[10].append(review.getText());
				mov.add(productID);
			}else if (productID.equals("7883704540")) {
				builders[11].append(review.getText());
				mov.add(productID);
			} 
		}
		mov = mov.stream().distinct().collect(Collectors.toList());

		// Iterator with respective file names
		Iterator<String> movies = mov.iterator();

		int j=0;
		BrillTagger br = new BrillTagger();
		// Tokenize the reviews
		for (StringBuilder stringBuilder : builders) {
			if(stringBuilder.length() >0){
				System.out.println(j);
				String collectedReviews = stringBuilder.toString();
				Tokenizer tokenizer = new Tokenizer();
				try {
					String[] tokens = tokenizer.tokenize(collectedReviews);
					//Stemmer stemmer = new KehlbeckSperrleStemmer();
					//String[] stems = new String[tokens.length];
					String[] pos = new String[tokens.length];
					//for (int i = 0; i < tokens.length; i++) {
					//	stems[i] = stemmer.stem(tokens[i]);
					//}
					pos = br.tag(tokens);
					//pos = br.tag(tokens);
					ArrayList<String> NN =new ArrayList<String>();
					ArrayList<String> AP =new ArrayList<String>();
					ArrayList<String> VB =new ArrayList<String>();
					
					for (int i = 0; i < tokens.length; i++) {
						if (pos[i] != null) {
							if (pos[i].startsWith("nn")){
								NN.add(tokens[i]);
							}else if (pos[i].startsWith("ap")){
								AP.add(tokens[i]);
							}else if (pos[i].startsWith("vb")){
								VB.add(tokens[i]);
							}
						}
					}
					// Save results to file, create Word Clouds
					String curr = movies.next();
					Files.write(Paths.get(String.format("data/%sNN.txt", curr)), NN);
					WordCloudCreator creator = new WordCloudCreator(String.format("data/img/%sNN.png", curr), "data/img/whale.png", String.format("data/img/%sNN.txt", curr));

					Files.write(Paths.get(String.format("data/%sAP.txt", curr)), AP);
					WordCloudCreator creator2 = new WordCloudCreator(String.format("data/img/%sAP.png", curr), "data/img/whale.png", String.format("data/img/%sAP.txt", curr));

					Files.write(Paths.get(String.format("data/%sVB.txt", curr)), VB);
					WordCloudCreator creator3 = new WordCloudCreator(String.format("data/img/%sVB.png", curr), "data/img/whale.png", String.format("data/img/%sVB.txt", curr));

					creator.createWordCloud();
					creator2.createWordCloud();
					creator3.createWordCloud();
				} catch (IOException e) {
					e.printStackTrace();
				}
				j+=1;
			}
		}
	}
}
