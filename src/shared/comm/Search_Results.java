package shared.comm;

import java.util.ArrayList;
import java.util.List;

import shared.model.Field;
/**
 * Contains the results from the search function.
 * @author jageorge
 *
 */
public class Search_Results extends Result{
	private List<SearchResult> results;
	
	public Search_Results(){
		results = new ArrayList<SearchResult>();
		setSuccess(false);
	}
	
	public void addresult(int batchId, String imageURL,int recordNum,int fieldId){
		results.add(new SearchResult(batchId,imageURL,recordNum, fieldId));
	}
	
	public void AppendHostPort(String url){
		if(results == null)
			return;
		for(SearchResult s :results){
			s.setImageURL(url + s.getImageURL());
		}
	}
	
	
	public List<SearchResult> getResults() {
		return results;
	}

	@Override
	public String toString() {
		if(isSuccess()){
			StringBuilder ret = new StringBuilder();
			for(SearchResult s: results){
				ret.append(s.getBatchId()+"\n");
				ret.append(s.getImageURL()+"\n");
				ret.append(s.getRecordNum()+"\n");
				ret.append(s.getFieldId()+"\n");
			}
			
			return ret.toString();
		}else{
			return "FAILED\n";
		}
		
		
	}



	/**
	 * Contains the information needed for a single search result to be stored in a list.
	 * @author jageorge
	 *
	 */
	public class SearchResult {
		int batchId;
		String ImageURL;
		int recordNum;
		int fieldId;
		
		public SearchResult(int batchId, String imageURL, int recordNum,
				int fieldId) {
			this.batchId = batchId;
			ImageURL = imageURL;
			this.recordNum = recordNum;
			this.fieldId = fieldId;
		}
		
		public void setImageURL(String url){
			ImageURL = url;
		}
		
		public int getBatchId() {
			return batchId;
		}
		public String getImageURL() {
			return ImageURL;
		}
		public int getRecordNum() {
			return recordNum;
		}
		public int getFieldId() {
			return fieldId;
		}

		@Override
		public String toString() {
			return "SearchResult [batchId=" + batchId + ", ImageURL="
					+ ImageURL + ", recordNum=" + recordNum + ", fieldId="
					+ fieldId + "]";
		}
		
		
	}
	
	
}
