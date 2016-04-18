package reader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jsoup.Jsoup;

public class TextReader {
	private String path;
	

	
	public TextReader(String filePath) {
		path = filePath;
	}

	public static void main(String[] args) throws IOException{
		String filePath = "data/reviews.rtf";
		Reader textReader = new Reader(filePath);
		
		System.out.println(Jsoup.parse("\\").text());
		
		List<Review> reviews = textReader.readFile();
		
		for (Review review : reviews) {
			System.out.println(review.getTime());
		} 
	}

}
