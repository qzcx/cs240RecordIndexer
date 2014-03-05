package shared.model;

/**
 * Model for a Record 
 * @author jageorge
 *
 */
public class Record {
	private int batchId;
	private int fieldId;
	private int recordNum;
	private String value;

	public Record(int batchId, int fieldId, int recordNum, String value) {
		super();
		this.batchId = batchId;
		this.fieldId = fieldId;
		this.recordNum = recordNum;
		this.value = value;
	}

	@Override
	public String toString() {
		return "Record [batchId=" + batchId + ", fieldId=" + fieldId
				+ ", recordNum=" + recordNum + ", value=" + value + "]";
	}
	
	//Getters and Setters
	public int getBatchId() {
		return batchId;
	}
	public void setBatchId(int batchId) {
		this.batchId = batchId;
	}
	public int getFieldId() {
		return fieldId;
	}
	public void setFieldId(int fieldId) {
		this.fieldId = fieldId;
	}
	public int getRecordNum() {
		return recordNum;
	}
	public void setRecordNum(int recordNum) {
		this.recordNum = recordNum;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

	
	
}
