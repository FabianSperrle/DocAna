package reader;

import java.util.Date;

public class Review {
	private Product product;
	private User user;
	private String helpfulness;
	private double score;
	private Date time;
	private String summary;
	private String text;
	
	public Review (Product product, User user, String helpfulness, int score, Date time, 
			String summary, String text) {
		this.product = product;
		this.user = user;
		this.helpfulness = helpfulness;
		this.score = score;
		this.time = time;
		this.summary = summary;
		this.text = text;
	}
	
	public Review () {}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getHelpfulness() {
		return helpfulness;
	}

	public void setHelpfulness(String helpfulness) {
		this.helpfulness = helpfulness;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	
	
}