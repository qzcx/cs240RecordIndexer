package shared.comm;

import java.util.List;

import shared.model.Field;
/**
 * Results from the GetFields functions
 * Contains the fields returned 
 * @author jageorge
 *
 */

public class GetFields_Results extends Result{
	
	private List<Field> fields;

	public GetFields_Results(List<Field> fields) {
		super();
		this.fields = fields;
		setSuccess(true);
	}

	public GetFields_Results() {
		setSuccess(false);
	}

	public List<Field> getFields() {
		return fields;
	}
	
	public void AppendHostPort(String url){
		for(Field f :fields){
			f.setHelpHtml(url + f.getHelpHtml());
		}
	}
	
	public String toString(){
		if(isSuccess()){
			if(fields.size() == 0)
				return "FAILED\n";
			
			StringBuilder ret = new StringBuilder();
			for(Field f : fields){
				ret.append(f.getProjectId() +"\n");
				ret.append(f.getId() +"\n");
				ret.append(f.getTitle() +"\n");
			}
			
			return ret.toString();
		}else
			return "FAILED\n";
	}
}
