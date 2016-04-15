
public class reviewData {
	//andere tollere Daten typen, vllt auch extra klasse fuer produkte?
	private String productID;
	private String userID;
	private String profileName;
	private String helpfulness;
	private String score;
	private String time;
	private String summary;
	private String text;
	
	public reviewData (String pProductID, String pUserID, String pProfileName, String pHelpfullness, String pScore, String pTime, String pSummary, String pText) {
		productID = pProductID;
		userID = pUserID;
		profileName = pProfileName;
		helpfulness = pHelpfullness;
		score = pScore;
		time = pTime;
		summary = pSummary;
		text = pText;
	}

	public String getProductID() {
		return productID;
	}

	public void setProductID(String productID) {
		this.productID = productID;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
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
