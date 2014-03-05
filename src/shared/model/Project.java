package shared.model;


/**
 * Model for a Project Record
 * @author jageorge
 *
 */
public class Project {
	private int id;
	private String title;
	private int recordsPerImage;
	private int firstYCoord;
	private int recordHeight;
	
	public Project(String title, int recordsPerImage, 
			int firstYCoord, int recordHeight){
		this(-1,title, recordsPerImage,firstYCoord, recordHeight);
	}
	
	public Project(int id, String title, int recordsPerImage, 
			int firstYCoord, int recordHeight) {
		super();
		this.id = id;
		this.title = title;
		this.recordsPerImage = recordsPerImage;
		this.firstYCoord = firstYCoord;
		this.recordHeight = recordHeight;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + firstYCoord;
		result = prime * result + id;
		result = prime * result + recordHeight;
		result = prime * result + recordsPerImage;
		result = prime * result + ((title == null) ? 0 : title.hashCode());
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
		Project other = (Project) obj;
		if (firstYCoord != other.firstYCoord)
			return false;
		if (id != other.id)
			return false;
		if (recordHeight != other.recordHeight)
			return false;
		if (recordsPerImage != other.recordsPerImage)
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}



	@Override
	public String toString() {
		return "Project [id=" + id + ", title=" + title + ", recordsPerImage="
				+ recordsPerImage + ", firstYCoord=" + firstYCoord
				+ ", recordHeight=" + recordHeight + "]";
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getRecordsPerImage() {
		return recordsPerImage;
	}

	public void setRecordsPerImage(int recordsPerImage) {
		this.recordsPerImage = recordsPerImage;
	}

	public int getFirstYCoord() {
		return firstYCoord;
	}

	public void setFirstYCoord(int firstYCoord) {
		this.firstYCoord = firstYCoord;
	}

	public int getRecordHeight() {
		return recordHeight;
	}

	public void setRecordHeight(int recordHeight) {
		this.recordHeight = recordHeight;
	}

}
