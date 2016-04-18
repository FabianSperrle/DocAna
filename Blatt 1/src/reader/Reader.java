package reader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;

public class Reader {
	
	// match all lines that contain either review/ or product/
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
		while ((line = reader.readLine()) != null) {
			// Remove all HTML tags and parse stuff like &amph;
			line = Jsoup.parse(line).text();
			// Remove trailing backslash
			if (line.length() > 0)
				line = line.substring(0, line.length()-1);

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
				tempReview.setScore(line);
			}
			if (line.contains(TIME)) {
				line = line.replaceAll(TIME, "");
				tempReview.setTime(line);
			}
			if (line.contains(SUMMARY)) {
				line = line.replaceAll(SUMMARY, "");
				tempReview.setSummary(line);
			}
			if (line.contains(TEXT)) {
				line = line.replaceAll(TEXT, "");
				tempReview.setText(line);
			}
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
