package server.dba;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import shared.comm.Download_Results;
import shared.comm.GetSample_Results;
import shared.comm.Project_Params;
import shared.model.Batch;
import shared.model.Field;
import shared.model.Project;
import shared.model.User;

/**
 * Used to access the Batches table. 
 * @author jageorge
 *
 */
public class BatchesDba {
	private Database db;
	
	public BatchesDba(Database database) {
		db = database;
	}
	
	public GetSample_Results getSample(Project_Params params){
		GetSample_Results ret = new GetSample_Results();
		
		if(!db.getUsers().checkUser(params.getUsername(), params.getPassword()))
			return ret; //if user isn't valid then return FAILED

		String sql = "SELECT FilePath " +
				"FROM Batches "+
				"WHERE ProjectId = '" + params.getProjectId() +"';"; //add on where id = 1
		ResultSet rs = null;
		String retURL= "";
		try(PreparedStatement stmt = db.getConnection().prepareStatement(sql);) {
			rs = stmt.executeQuery();
			
			if(rs.next()){
				retURL = rs.getString(1);
				ret = new GetSample_Results(retURL);
			}
			rs.close();
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}
		
		return ret;
	}
	
	/**
	 * returns the information of the next unassigned, unindexed project.
	 * @param project
	 * @return
	 */
	public Download_Results downloadBatch(Project_Params params){
		Download_Results ret = new Download_Results();
		
		ResultSet rs = null;
		String sql = "SELECT FilePath, Id " +
				"FROM Batches " +
				"WHERE ProjectId = " + params.getProjectId() +
				" AND UserId IS NULL AND IsIndexed = \"FALSE\";"; //add on where id = 1
		String retURL= "";
		int batchId = -1;
		if(!db.getUsers().checkUser(params.getUsername(), params.getPassword()))
			return ret; //return failed if user isn't valid.
		
		try {
			Project proj = db.getProjects().getProject(params.getProjectId());
			List<Field> fields = db.getFields().getFieldList(params.getProjectId());
			
			try(PreparedStatement stmt = db.getConnection().prepareStatement(sql);) {
				rs = stmt.executeQuery();
				
				if(rs.next()){
					retURL = rs.getString(1);
					batchId = rs.getInt(2);
					if(findUsersBatch(params.getUsername()) != -1)
						return ret; //if the user already has a project,then FAIL
					SetBatchUser(params.getUsername(), batchId);
					
				}else{
					throw new NoMoreProjectsException();
				}
				
			}finally{
				if(!rs.equals(null))
					rs.close();
			}
			
			
			ret = new Download_Results(params.getProjectId(), batchId, retURL, proj.getFirstYCoord(),
					proj.getRecordHeight(), proj.getRecordsPerImage(), fields);
		} catch (SQLException e) {
			e.printStackTrace();
		}catch (NoMoreProjectsException e) {
			e.printStackTrace();
		}
		
		return ret;
	}
	/**
	 * Finds the user's current batch. returns -1 if user doesn't have one.
	 * @param username
	 * @return
	 */
	public int findUsersBatch(String username) throws SQLException{
		int userId = db.getUsers().getUser(username).getId();
		
		String sql = "SELECT Id " +
				"FROM Batches "+
				"WHERE UserId = '" + userId +"';"; //add on where id = 1
		ResultSet rs = null;
		int ret= -1;
		try(PreparedStatement stmt = db.getConnection().prepareStatement(sql);) {
			rs = stmt.executeQuery();
			if(rs.next()){
				ret = rs.getInt(1);
			}
		}
		return ret;
	}
	
	
	/**
	 * Updates the UserId of a Batch in the batch table with the given Id
	 * @param username
	 * @param batchId
	 */
	protected void SetBatchUser(String username, int batchId) throws SQLException {
		String userId = "null";
		if(!username.equals("null")){
			User user = db.getUsers().getUser(username);
			userId = ""+ user.getId();
		}
		String sql = "UPDATE Batches " +
				"SET userId = "+ userId +
				" WHERE Id = " + batchId +";"; //add on where id = 1
		try(PreparedStatement stmt = db.getConnection().prepareStatement(sql);) {
			if (stmt.executeUpdate() != 1)
				throw new SQLException("Update Batch User Failed");
		}
	}
	
	/**
	 * Returns the URL of the batch with the batchID given
	 * @param batchId
	 * @return 
	 * @throws SQLException
	 */
	public String getBatchURL(int batchId) throws SQLException{
		String sql = "SELECT FilePath " +
				"FROM Batches "+
				"WHERE id = '" + batchId +"';"; //add on where id = 1
		ResultSet rs = null;
		String retURL= "";
		try(PreparedStatement stmt = db.getConnection().prepareStatement(sql);) {
			rs = stmt.executeQuery();
			if(rs.next()){
				retURL = rs.getString(1);
			}
		}
		return retURL;
	}
	
	/**
	 * Adds the Given batch to the Database
	 * @param batch
	 * @return batchId of the new batch record.
	 */
	public int addBatch(Batch batch) throws SQLException {
		String sql = "INSERT INTO batches" + 
	   			 "(FilePath,ProjectId)" +
	                "values(?,?)";
		int ret = -1;
		ResultSet generatedKeys = null;
		try(PreparedStatement stmt = db.getConnection().prepareStatement(sql);) {
		    stmt.setString(1, batch.getImagePath());
		    stmt.setInt(2, batch.getProjectId());
		    if (stmt.executeUpdate() != 1)
		    	;//TODO Throw error?
		    generatedKeys = stmt.getGeneratedKeys();
	        if (generatedKeys.next()) {
	            ret = generatedKeys.getInt(1);
	        }
	        else{
	        	throw new SQLException("addBatch Fail: No key was returned");
	        }
		}finally{
			if(generatedKeys != null)
				generatedKeys.close();
		}

		return ret;
	}

	
	class NoMoreProjectsException extends Exception{}


	public void setIndexed(int batchId) throws SQLException {
		String sql = "UPDATE Batches " +
				" SET IsIndexed = 1" +
				" WHERE Id = " + batchId +";"; //add on where id = 1
		try(PreparedStatement stmt = db.getConnection().prepareStatement(sql);) {
			if (stmt.executeUpdate() != 1)
				throw new SQLException("Set Batch to Indexed Failed");
		}
		
	}

	
}
