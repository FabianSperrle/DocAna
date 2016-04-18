package reader;

public class Review {
	//andere tollere Daten typen, vllt auch extra klasse fuer produkte?
	private Product product;
	private User user;
	private String helpfulness;
	private String score;
	private String time;
	private String summary;
	private String text;
	
	public Review (Product product, User user, String helpfulness, String score, String time, 
			String summary, String text) {
		this.product = product;
		this.user = user;
		this.helpfulness = helpfulness;
		this.score = score;
		this.time = time;
		this.summary = summary;
		this.text = text;
	}

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

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
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