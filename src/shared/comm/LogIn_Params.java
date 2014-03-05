package shared.comm;

/**
 * Contains the parameters to be passed to the server for validating the user.
 * @author jageorge
 *
 */

public class LogIn_Params {
	private String username;
	private String password;
	
	public LogIn_Params(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}
	//getters and setters
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	@Override
	public String toString() {
		return "LogIn_Params [username=" + username + ", password=" + password
				+ "]";
	}
	
	
	
}
