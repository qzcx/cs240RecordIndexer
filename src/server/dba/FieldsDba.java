package server.dba;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import shared.comm.GetFields_Results;
import shared.comm.Project_Params;
import shared.model.Field;
import shared.model.Project;

/**
 * Used to access the Fields table. 
 * @author jageorge
 *
 */
public class FieldsDba {
	private Database db;
	
	public FieldsDba(Database database) {
		db = database;
	}

	public GetFields_Results getFields(Project_Params params){
		GetFields_Results ret = new GetFields_Results();
		if(!db.getUsers().checkUser(params.getUsername(), params.getPassword()))
			return ret; //return failed if user isn't valid.
		try{
			List<Field> fieldList = getFieldList(params.getProjectId());
			ret = new GetFields_Results(fieldList);
		}catch(SQLException e){
			e.printStackTrace();
		}
		return ret;//return failed if SQLexception occurs.
	}
	
	protected List<Field> getFieldList(int projectId) throws SQLException{
		String sql = "SELECT * " +
				"FROM Fields ";
		if(projectId > 0)
				sql +="WHERE projectid = '" + projectId +"';";
		ResultSet rs = null;
		List<Field> retFieldList= new ArrayList<Field>();
		try(PreparedStatement stmt = db.getConnection().prepareStatement(sql);) {
			rs = stmt.executeQuery();
			while(rs.next()){
				int id = rs.getInt(1);
				int projId = rs.getInt(2);
				String title = rs.getString(3);
				int xCoord = rs.getInt(4);
				int width = rs.getInt(5);
				String helpHtml = rs.getString(6);
				String knownDataUrl = rs.getString(7);
				retFieldList.add(new Field(id, projId, title, 
						xCoord, width, helpHtml, knownDataUrl));
			}
		}
		return retFieldList;
	}
	
	
	
	/**
	 * Adds a field to the database
	 * @param field
	 * @return the id of the new Field entry
	 */
	public int addField(Field field) throws SQLException {
		String sql = "INSERT INTO Fields" + 
	   			 "(projectId,name,xcoord,width,helphtml,knowndata)" +
	                "values(?,?,?,?,?,?)";
		int ret = -1;
		ResultSet generatedKeys = null;
		try(PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {
			stmt.setInt(1, field.getProjectId());
		    stmt.setString(2, field.getTitle());
		    stmt.setInt(3, field.getxCoord());
		    stmt.setInt(4, field.getWidth());
		    stmt.setString(5, field.getHelpHtml());
		    stmt.setString(6, field.getKnownDataUrl());
		    if (stmt.executeUpdate() != 1)
		    	;//TODO Throw error?
		    generatedKeys = stmt.getGeneratedKeys();
		    if (generatedKeys.next()) {
	            ret = generatedKeys.getInt(1);
	        }
	        else{
	        	throw new SQLException("addField Fail: No key was returned");
	        }
		}finally{
			if(generatedKeys != null)
				generatedKeys.close();
		}
		return ret;
	}
	/**
	 * Gets all the fields in the given batch
	 * @param batchId
	 * @return
	 * @throws SQLException 
	 */
	public List<Integer> getFieldIds(int batchId) throws SQLException {
		//First find the ProjectId from the BatchId.
		String sql = "SELECT projectId "+
					" FROM Batches" + 
					" WHERE id =" + batchId + ";";
		ResultSet rs = null;
		
		int projId = -1;
		try(PreparedStatement stmt = db.getConnection().prepareStatement(sql);) {
			rs = stmt.executeQuery();
			if(rs.next()){
				projId = rs.getInt(1);
			}
			rs.close();
		}
		//Now use the ProjectId to find all of the fieldsIds
		sql = "SELECT id " +
				"FROM Fields " +
				"WHERE projectId = "+projId+";";
		rs = null;
		List<Integer> retIdList= new ArrayList<Integer>();
		try(PreparedStatement stmt = db.getConnection().prepareStatement(sql);) {
			rs = stmt.executeQuery();
			while(rs.next()){
				int id = rs.getInt(1);
				retIdList.add(id);
			}
			rs.close();
		}
		return retIdList;
	}
	
	/**
	 * Gets all the fieldsIds in the project.
	 * @return
	 * @throws SQLException
	 */
	protected List<Integer> getFieldIds() throws SQLException{
		String sql = "SELECT id " +
				"FROM Fields ";
		ResultSet rs = null;
		List<Integer> retIdList= new ArrayList<Integer>();
		try(PreparedStatement stmt = db.getConnection().prepareStatement(sql);) {
			rs = stmt.executeQuery();
			while(rs.next()){
				int id = rs.getInt(1);
				retIdList.add(id);
			}
		}
		return retIdList;
	}
	
	/**
	 * Check if the given list of FieldIds are all valid. 
	 * @param fields
	 * @return
	 */
	public boolean checkFieldIds(String[] fields) {
		try {
			List<Integer> dbFieldIds = getFieldIds();
			List<Integer> fieldIds = new ArrayList<Integer>();
			for(String field:fields){
				fieldIds.add(Integer.parseInt(field));
			}
			boolean ret = dbFieldIds.containsAll(fieldIds);
			return ret;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}


	
}
