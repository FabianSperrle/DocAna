package reader;
import java.io.IOException;
import java.util.List;

public class Main {

	public static void main(String[] args) throws IOException{
		String filePath = "data/reviews.rtf";
		Reader reader = new Reader(filePath);
		
		List<Review> reviews = reader.readFile();
	}
}
