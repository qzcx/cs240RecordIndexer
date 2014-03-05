package shared.comm;

import java.util.List;

import shared.model.Record;
/**
 * Contains the parameters for the submitBatch function
 * @author jageorge
 *
 */
public class Submit_Params extends LogIn_Params {
	private int batchId;
	private String fieldValues;
	
	public Submit_Params(String username, String password, 
			int batchId, String fieldValues) {
			super(username, password);
			this.batchId = batchId;
			this.fieldValues = fieldValues;
		}
	

	
	public String getFieldValues() {
		return fieldValues;
	}
	
	public int getBatchId() {
		return batchId;
	}



	@Override
	public String toString() {
		return "Submit_Params [batchId=" + batchId + ", fieldValues="
				+ fieldValues + "]";
	}

	
}
