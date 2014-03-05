package server.dba;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import shared.comm.GetProjects_Results;
import shared.comm.LogIn_Params;
import shared.model.Project;

/**
 * Used to access the projects table. 
 * @author jageorge
 *
 */
public class ProjectsDba {

	private Database db;
	public ProjectsDba(Database database) {
		db = database;
	}

	public GetProjects_Results getProjects(LogIn_Params logIn){
		Map<Integer, String> retMap = new HashMap<Integer,String>();
		GetProjects_Results ret = new GetProjects_Results(); //default failed Result
		if(!db.getUsers().checkUser(logIn.getUsername(), logIn.getPassword()))
			return ret; //return failed if user isn't valid.
		try {
			List<Project> projectList = getProjectList(true,-1);
			for(Project p: projectList){
				retMap.put(p.getId(), p.getTitle());
			}
			ret = new GetProjects_Results(retMap);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return ret;
	}
	
	public Project getProject(int projectId) throws SQLException{
		List<Project> ret = getProjectList(false, projectId);
		if(ret.size() == 0)
			return null;
		else
			return ret.get(0);
	}
	/**
	 * 
	 * @param projectId
	 * @param getAll - Used if looking for all project results
	 * @return
	 * @throws SQLException
	 */
	public List<Project> getProjectList(boolean getAll,int projectId) throws SQLException{
		String sql = "SELECT * " +
				"FROM Projects ";
		if(!getAll)
				sql +="WHERE id = '" + projectId +"';";
		ResultSet rs = null;
		List<Project> retProject= new ArrayList<Project>();
		try(PreparedStatement stmt = db.getConnection().prepareStatement(sql);) {
			rs = stmt.executeQuery();
			while(rs.next()){
				int id = rs.getInt(1);
				String title = rs.getString(2);
				int recordsPerImage = rs.getInt(3);
				int firstYCoord = rs.getInt(4);
				int recordHeight = rs.getInt(5);
				retProject.add(new Project(
						id,title,recordsPerImage, firstYCoord, recordHeight));
			}
		}
		return retProject;
	}
	
	
	/**
	 * 
	 * @param project
	 * @return projectId for later use
	 * @throws SQLException 
	 */
	public int addProject(Project project) throws SQLException {
		String sql = "INSERT INTO Projects" + 
	   			 "(Title,RecordsPerImage,FirstYCoord,RecordHeight)" +
	                "values(?,?,?,?)";
		ResultSet generatedKeys = null;
		int ret = -1;
		try(PreparedStatement stmt = db.getConnection().prepareStatement(sql);) {
		    stmt.setString(1, project.getTitle());
		    stmt.setInt(2, project.getRecordsPerImage());
		    stmt.setInt(3, project.getFirstYCoord());
		    stmt.setInt(4, project.getRecordHeight());
		    if (stmt.executeUpdate() != 1)
		    	;//TODO Throw error?
		    generatedKeys = stmt.getGeneratedKeys();
	        if (generatedKeys.next()) {
	            ret = generatedKeys.getInt(1);
	        }
	        else{
	        	throw new SQLException("addProject Fail: No key was returned");
	        }
	        	
		}finally{
			if(generatedKeys != null)
				generatedKeys.close();
		}
		return ret;
	}
	
	
	
}
