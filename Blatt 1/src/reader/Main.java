package reader;
import java.io.IOException;
import java.util.List;

public class Main {

	public static void main(String[] args) throws IOException{
	//we have truncated the data file for easier upload in ilias, as there is a 40 MB size limit
		String filePath = "data/reviews.rtf";
		Reader reader = new Reader(filePath);
		
		List<Review> reviews = reader.readFile();
	}
}
