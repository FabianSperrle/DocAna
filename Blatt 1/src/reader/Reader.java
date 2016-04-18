package reader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;

public class Reader {
	private final String path;
	
	private final String PRODUCTID = "product/productId: ";
	private final String USERID = "review/userId: ";
	private final String PROFILENAME = "review/profileName: ";
	private final String HELPFULNESS = "review/helpfulness: ";
	private final String SCORE = "review/score: ";
	private final String TIME = "review/time: ";
	private final String SUMMARY = "review/summary: ";
	private final String TEXT = "review/text: ";
	
	public Reader(String path) {
		this.path = path;
	}
	
	public List<Review> readFile() throws IOException {
		List<Review> list = new ArrayList<>();
		BufferedReader reader = new BufferedReader(new FileReader(path));
		
		String line;
		User tempUser = new User();
		Review tempReview = new Review();
		// Read  next line
		while ((line = reader.readLine()) != null) {
			// Remove all HTML tags and parse stuff like &amph; with the Jsoup library
			line = Jsoup.parse(line).text();
			// Remove trailing backslash
			if (line.length() > 0)
				line = line.substring(0, line.length()-1);

			// Check for each line whether it starts with one of the known prefixes.
			// If yes, set the respective property. 
			if (line.contains(PRODUCTID)) {
				line = line.replaceAll(PRODUCTID, "");
				tempReview.setProduct(new Product(line));
			}
			if (line.contains(USERID)) {
				line = line.replaceAll(USERID, "");
				tempUser.setUserID(line);
			}
			if (line.contains(PROFILENAME)) {
				line = line.replaceAll(PROFILENAME, "");
				tempUser.setUsername(line);
			}
			if (line.contains(HELPFULNESS)) {
				line = line.replaceAll(HELPFULNESS, "");
				tempReview.setHelpfulness(line);
			}
			if (line.contains(SCORE)) {
				line = line.replaceAll(SCORE, "");
				double score = Double.parseDouble(line);
				tempReview.setScore(score);
			}
			if (line.contains(TIME)) {
				line = line.replaceAll(TIME, "");
				Date date = new Date(Long.parseLong(line)*1000);
				tempReview.setTime(date);
			}
			if (line.contains(SUMMARY)) {
				line = line.replaceAll(SUMMARY, "");
				tempReview.setSummary(line);
			}
			if (line.contains(TEXT)) {
				line = line.replaceAll(TEXT, "");
				tempReview.setText(line);
			}
			
			// Reviews are delimeted by empty lines. Finish the Review object and add it
			// to the result list. Renew the temporary objects.
			if (line.equals("")) {
				tempReview.setUser(tempUser);
				list.add(tempReview);
				
				tempReview = new Review();
				tempUser = new User();
			}
		}
		
		reader.close();
		return list;
	}

}
