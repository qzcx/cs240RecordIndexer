package shared.comm;
/**
 * Contains the results from the validateUser function.
 * @author jageorge
 *
 */
public class ValidateUser_Results extends Result{
	private String firstName = "";
	private String lastName = "";
	private int numRecords = -1;
	
	
	public ValidateUser_Results(boolean successful, String username,
			String password, int numRecords) {
		super();
		setSuccess(successful);
		this.firstName = username;
		this.lastName = password;
		this.numRecords = numRecords;
	}

	//default constructor
	public ValidateUser_Results() {
	}


	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public int getNumRecords() {
		return numRecords;
	}

	public void setNumRecords(int numRecords) {
		this.numRecords = numRecords;
	}

	@Override
	public String toString() {
		if(isSuccess())
			return "TRUE\n" + firstName + "\n" + lastName + "\n" + numRecords +"\n";
		else
			return "FALSE";
	 	
	}
	
	
	
	
}
