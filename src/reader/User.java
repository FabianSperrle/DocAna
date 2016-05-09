package reader;

public class User {
	private String username;
	private String userID;
	
	public User() {}
	
	public User(String userid, String username) {
		this.userID = userid;
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}
	
	
}
