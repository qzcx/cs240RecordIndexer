package shared.comm;

import java.util.Map;
/**
 * Contains the Results of the GetProject Functions
 * @author jageorge
 *
 */
public class GetProjects_Results extends Result{

	private Map<Integer, String> projects;

	public GetProjects_Results(){
		setSuccess(false);
	}
	
	public GetProjects_Results(Map<Integer, String> projects) {
		super();
		setSuccess(true);
		this.projects = projects;
	}

	public Map<Integer, String> getProjects() {
		return projects;
	}
	
	public String toString(){
		if(isSuccess()){
			StringBuilder ret = new StringBuilder();
			for(Integer i: projects.keySet()){
				ret.append(i + "\n");
				ret.append(projects.get(i)+"\n");
			}
			return ret.toString();
		}else{
			return "FAILED\n";
		}
	}
	
}
