package reader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import tokenizer.MissingInputException;
import tokenizer.Tokenizer;
import tokenizer.SplitSentences;

public class Main {

	public static void main(String[] args) throws IOException{
	//we have truncated the data file for easier upload in ilias, as there is a 40 MB size limit
		String filePath = "data/docAnaTextSample.rtf";
		Reader reader = new Reader(filePath);
		
		List<Review> reviews = reader.readFile();
		
		StringBuilder text1 = new StringBuilder();
		StringBuilder text2 = new StringBuilder();
		StringBuilder text3 = new StringBuilder();
		Review rtemp;
		for (int i = 1; i < reviews.size(); i++) {
			rtemp = reviews.get(i);
			if (rtemp.getProduct().getProductID().equals("B004WO6BPS")) { //Harry Potter 7.1
				text1.append(rtemp.getText());
			} else if (rtemp.getProduct().getProductID().equals("B000IMM3XW")) { //X/Men
				text2.append(rtemp.getText());
			} else if (rtemp.getProduct().getProductID().equals("B00005YTR8")) { //Sherlock ?
				text3.append(rtemp.getText());
			}
		}
		//Files.write(Paths.get("data/text1.txt"), results);
		Review r = reviews.get(1);
		System.out.println(r.getText());
		Tokenizer tokenizer = new Tokenizer(r.getText());
		try {
			String[] result = tokenizer.tokenize();

//			for (String str : result) {
//				System.out.println(str);
//			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		
		SplitSentences sentence = new SplitSentences(r.getText());
		
		try {
			String[] result = sentence.tokenize();
//			for (String str : result) {
//				System.out.println(str);
//			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
	}
}
