package shared.comm;

import java.util.List;

import shared.model.Field;
/**
 * Contains the information returned from the server when the downloadBatch function is called.
 * 
 * @author jageorge
 */
public class Download_Results extends Result{
	private int projectId;
	private int batchId;
	private String imageURL;
	private int firstYCoord;
	private int RecordHeight;
	private int numRecords;
	private List<Field> fields;
	
	
	public Download_Results(int projectId, int batchId, String imageURL, int firstYCoord,
			int recordHeight, int numRecords, List<Field> fields) {
		setSuccess(true);
		this.projectId = projectId;
		this.batchId = batchId;
		this.imageURL = imageURL;
		this.firstYCoord = firstYCoord;
		RecordHeight = recordHeight;
		this.numRecords = numRecords;
		this.fields = fields;
	}
	
	public Download_Results(){
		setSuccess(false);
	}
	
	public void AppendHostPort(String url){
		imageURL = url +imageURL;
		if(fields == null)
			return;
		for(Field f :fields){
			f.setHelpHtml(url + f.getHelpHtml());
			if(!f.getKnownDataUrl().equals(""))
				f.setKnownDataUrl(url + f.getKnownDataUrl());
		}
	}
	
	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

	//getters for this class
	public int getProjectId() {
		return projectId;
	}
	public int getBatchId() {
		return batchId;
	}
	public String getImageURL() {
		return imageURL;
	}
	public int getFirstYCoord() {
		return firstYCoord;
	}
	public int getRecordHeight() {
		return RecordHeight;
	}
	public int getNumRecords() {
		return numRecords;
	}
	public List<Field> getFields() {
		return fields;
	}
	
	public String toString(){
		if(isSuccess()){
			StringBuilder ret = new StringBuilder();
			ret.append(batchId + "\n");
			ret.append(projectId + "\n");
			ret.append(imageURL + "\n");
			ret.append(firstYCoord + "\n");
			ret.append(RecordHeight + "\n");
			ret.append(numRecords + "\n");
			ret.append(fields.size() + "\n");
			for(int i=0; i< fields.size(); i++){
				Field f = fields.get(i);
				ret.append(f.getId() + "\n");
				ret.append(i+ "\n");
				ret.append(f.getTitle() + "\n");
				ret.append(f.getHelpHtml() + "\n");
				ret.append(f.getxCoord() + "\n");
				ret.append(f.getWidth() + "\n");
				if(!f.getKnownDataUrl().equals(""))
					ret.append(f.getKnownDataUrl() + "\n");
			}
			return ret.toString();
		}else{
			return "FAILED\n";
		}
	}
	
}
