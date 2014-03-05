package shared.model;

import java.util.List;
/**
 * Model for a batch record
 * @author jageorge
 *
 */
public class Batch {
	private int id;
	private int projectId;

	private String imagePath;
	private List<Record> records;
	
	public Batch(int projectId, String imagePath){
		this(-1, projectId, imagePath, null);
	}
	
	public Batch(int id, int projectId,String imagePath, List<Record> records) {
		super();
		this.id = id;
		this.projectId = projectId;
		this.imagePath = imagePath;
		this.records = records;
	}
	

	@Override
	public String toString() {
		return "Batch [id=" + id + ", projectId=" + projectId + ", imagePath="
				+ imagePath + ", records=" + records + "]";
	}
	
	//Getters and Setters
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getProjectId() {
		return projectId;
	}
	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}
	public String getImagePath() {
		return imagePath;
	}
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	public List<Record> getRecords() {
		return records;
	}
	public void setRecords(List<Record> records) {
		this.records = records;
	}
	
	
}
