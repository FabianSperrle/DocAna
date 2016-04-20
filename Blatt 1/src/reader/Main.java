package reader;
import java.io.IOException;
import java.util.List;

import tokenizer.MissingInputException;
import tokenizer.Tokenizer;

public class Main {

	public static void main(String[] args) throws IOException{
		String filePath = "data/reviews.rtf";
		Reader reader = new Reader(filePath);
		
		List<Review> reviews = reader.readFile();
		
		Review r = reviews.get(1);
		System.out.println(r.getText());
		Tokenizer tokenizer = new Tokenizer(r.getText());
		try {
			String[] result = tokenizer.tokenize();
			for (String str : result) {
				System.out.println(str);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
}
