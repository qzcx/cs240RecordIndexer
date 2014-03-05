package shared.comm;

/**
 * Parent class of Result comm classes to check success of request
 * @author jageorge
 *
 */
public class Result {
	
	private boolean successful;
	
	public Result(){
		successful = false;
	}
	
	public Result(boolean successful) {
		super();
		this.successful = successful;
	}
	
	public boolean isSuccess() {
		return successful;
	}
	
	public void setSuccess(boolean success) {
		this.successful = success;
	}

	public String toString(){
		if(isSuccess())
			return "TRUE\n";
		else
			return "FAILED\n";
	}
	
}
