package shared.model;

import java.util.List;
/**
 * Model for a Field record
 * @author jageorge
 *
 */
public class Field {
	private int id;
	private int projectId;
	private String title;
	private int xCoord;
	private int width;
	private String helpHtml;
	private String knownDataUrl;
	private List<String> knownData;
	
	
	
	public Field(int projectId, String title, int xCoord, int pixel_Width,
			String helpHtml, String knownDataUrl) {
		this(-1,projectId,title,xCoord,pixel_Width,helpHtml,knownDataUrl);
	}


	public Field(int id,int projectId, String title, 
			int xCoord, int pixel_Width,
			String helpHtml, String knownDataUrl) {
		super();
		this.id = id;
		this.projectId = projectId;
		this.title = title;
		this.xCoord = xCoord;
		this.width = pixel_Width;
		this.helpHtml = helpHtml;
		this.knownDataUrl = knownDataUrl;
	}
	
	@Override
	public String toString() {
		return "Field [id=" + id + ", projectId=" + projectId + ", title="
				+ title + ", xCoord=" + xCoord + ", pixel_Width=" + width
				+ ", helpHtml=" + helpHtml + ", knownDataUrl=" + knownDataUrl
				+ ", knownData=" + knownData + "]";
	}

	//Getters and setters
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getxCoord() {
		return xCoord;
	}
	public void setxCoord(int xCoord) {
		this.xCoord = xCoord;
	}
	public int getProjectId() {
		return projectId;
	}
	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public String getHelpHtml() {
		return helpHtml;
	}
	public void setHelpHtml(String helpHtml) {
		this.helpHtml = helpHtml;
	}
	public String getKnownDataUrl() {
		return knownDataUrl;
	}
	public void setKnownDataUrl(String knownDataUrl) {
		this.knownDataUrl = knownDataUrl;
	}
	
	public List<String> getKnownData() {
		return knownData;
	}
	
	public void setKnownData(List<String> knownData) {
		this.knownData = knownData;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((helpHtml == null) ? 0 : helpHtml.hashCode());
		result = prime * result + id;
		result = prime * result
				+ ((knownData == null) ? 0 : knownData.hashCode());
		result = prime * result
				+ ((knownDataUrl == null) ? 0 : knownDataUrl.hashCode());
		result = prime * result + projectId;
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + width;
		result = prime * result + xCoord;
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Field other = (Field) obj;
		if (helpHtml == null) {
			if (other.helpHtml != null)
				return false;
		} else if (!helpHtml.equals(other.helpHtml))
			return false;
		if (id != other.id)
			return false;
		if (knownData == null) {
			if (other.knownData != null)
				return false;
		} else if (!knownData.equals(other.knownData))
			return false;
		if (knownDataUrl == null) {
			if (other.knownDataUrl != null)
				return false;
		} else if (!knownDataUrl.equals(other.knownDataUrl))
			return false;
		if (projectId != other.projectId)
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (width != other.width)
			return false;
		if (xCoord != other.xCoord)
			return false;
		return true;
	}
	
	
	
}
