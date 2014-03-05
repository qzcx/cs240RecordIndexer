package shared.comm;
/**
 * Contains the Parameters for the GetProjects function
 * @author jageorge
 *
 */
public class Project_Params extends LogIn_Params{
	private int projectId;
	
	public Project_Params(String username, String password) {
		this(username,password,-1);
	}
	
	public Project_Params(String username, String password, int projectId) {
		super(username,password);
		this.projectId = projectId;
	}

	public int getProjectId() {
		return projectId;
	}

	@Override
	public String toString() {
		return "Project_Params [projectId=" + projectId + ", username="
				+ getUsername() + ", password=" + getPassword() + "]";
	}
	
	
	
}
