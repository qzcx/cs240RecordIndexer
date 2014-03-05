package shared.comm;

import java.util.List;

import shared.model.Field;
/**
 * Contains the parameters for the search function
 * @author jageorge
 *
 */
public class Search_Params extends LogIn_Params {
	private String fieldIds;
	private String searchValues;
	
//	public Search_Params(String username, String password, 
//			List<Integer> fields, List<String> searchValues) {
//		super(username, password);
//		this.fieldIds = fields;
//		this.searchValues = searchValues;
//	}

	public Search_Params(String username, String password, String fieldIds,
			String searchValues) {
		super(username, password);
		this.fieldIds = fieldIds;
		this.searchValues = searchValues;
	}

	public String getFields() {
		return fieldIds;
	}

	public String getSearchValues() {
		return searchValues;
	}

	@Override
	public String toString() {
		return "Search_Params [fieldIds=" + fieldIds + ", searchValues="
				+ searchValues + "]";
	}

	
	
}
