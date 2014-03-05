package shared.comm;
/**
 * Contains the results from the getSample function.
 * @author jageorge
 *
 */
public class GetSample_Results extends Result{
	private String ImageURL;
	
	public GetSample_Results(String imageURL){
		ImageURL = imageURL;
		setSuccess(true);
	}

	
	
	public void setImageURL(String imageURL) {
		ImageURL = imageURL;
	}

	public GetSample_Results(){
		setSuccess(false);
	}
	
	public String getImageURL(){
		return ImageURL;
	}

	public String toString(){
		if(isSuccess()){
			return ImageURL + "\n";
		}else{
			return "FAILED\n";
		}
	}
}
