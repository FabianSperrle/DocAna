package reader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class textReader {
	private String path;
	
	// match all lines that contain either review/ or product/
	private static final String regexLines = "^.*?((?=review/)|(?=product/)).*";
	
	public textReader(String filePath) {
		path = filePath;
	}
	
	public List<String> OpenFile() throws IOException {
		List<String> list = new ArrayList<>();
		try (Stream<String> stream = Files.lines(Paths.get(path))) {
			//1. filter for regex
			//2. convert it into a List
			list = stream 
					.filter(line -> line.matches(regexLines))
					.map(line -> line.replaceAll("&amp;", "\""))
					.map(line -> line.replaceAll("<.*>.*<.*/>", ""))
					.collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
		}

		return list;
		
	}
	

	public static void main(String[] args) throws IOException{
		String filePath = "D:/Uni/MS02/Document Analysis/docAnaTextSample.rtf";
		
		try {
			textReader file = new textReader(filePath);
			List<Review> reviews = new ArrayList<>();
			List<String> array = file.OpenFile();
			for (int i = 0; i < array.size(); i=i+8) {
				Review data = new Review(array.get(i).substring(0,array.get(i).length()-1).replaceAll(".*product/productId: ", ""),
								array.get(i+1).substring(0,array.get(i+1).length()-1).replaceAll(".*review/userId: ", ""),
								array.get(i+2).substring(0,array.get(i+2).length()-1).replaceAll(".*review/profileName: ", ""), 
								array.get(i+3).substring(0,array.get(i+3).length()-1).replaceAll(".*review/helpfulness: ", ""), 
								array.get(i+4).substring(0,array.get(i+4).length()-1).replaceAll(".*review/score: ", ""),
								array.get(i+5).substring(0,array.get(i+5).length()-1).replaceAll(".*review/time: ", ""),
								array.get(i+6).substring(0,array.get(i+6).length()-1).replaceAll(".*review/summary: ", ""),
								array.get(i+7).substring(0,array.get(i+7).length()-1).replaceAll(".*review/text: ", ""));
				// System.out.println(data.getScore());
				reviews.add(data);
			}
		}
		catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

}
