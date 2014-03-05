package server.dba;

import java.sql.*;

import shared.comm.LogIn_Params;
import shared.comm.ValidateUser_Results;
import shared.model.User;

/**
 * Used to access the Users table. 
 * @author jageorge
 *
 */
public class UsersDba {
	private Database db;
	
	public UsersDba(Database db){
		this.db = db;
	}
	
	public void addUser(User user) throws SQLException{
		String sql = "INSERT INTO Users" + 
   			 "(username,password,lastname,firstname,email, indexedRecords)" +
                "values(?,?,?,?,?,?)";
		try(PreparedStatement stmt = db.getConnection().prepareStatement(sql);) {
		    stmt.setString(1, user.getUserName());
		    stmt.setString(2, user.getPassword());
		    stmt.setString(3, user.getLastName());
		    stmt.setString(4, user.getFirstName());
		    stmt.setString(5, user.getEmail());
		    stmt.setInt(6, user.getIndexedRecords());
		    if (stmt.executeUpdate() != 1)
		    	;//TODO Throw error?
		}catch(SQLException e){
			System.err.println("SQL exception thrown");
			e.printStackTrace();
		}
	}
	
	//I don't think I need update calls in this table.
	//public void update(User user) throws SQLException{	}
	
	/**
	 * 
	 * @param usernameIn
	 * @return User object if exists in database, else null
	 * @throws SQLException
	 */
	
	public User getUser(String usernameIn) throws SQLException{
		String sql = "SELECT * " +
					"FROM users "+
					"WHERE username = ?;";
		ResultSet rs = null;
		User retUser= null;
		try(PreparedStatement stmt = db.getConnection().prepareStatement(sql);) {
			stmt.setString(1, usernameIn);
			rs = stmt.executeQuery();
			
			if(rs.next()){
				int id = rs.getInt(1);
				String username = rs.getString(2);
				String password = rs.getString(3);
				String firstname = rs.getString(4);
				String lastname = rs.getString(5);
				String email = rs.getString(6);
				int recordsIndexed = rs.getInt(7);
				retUser = new User(id,username,password,firstname,lastname,email,recordsIndexed);
			}
		}
		return retUser;
	}
	/**
	 * Return null if Exception occurred
	 * @param logIn
	 * @return
	 */
	public ValidateUser_Results validateUser(LogIn_Params logIn){
		ValidateUser_Results ret = null;
		User user = null;
		try {
			user = getUser(logIn.getUsername());
			
		} catch (SQLException e) {
			 
			e.printStackTrace();
			return ret;
		}
		ret = new ValidateUser_Results();
		if(user != null && user.getPassword().equals(logIn.getPassword())){
			ret.setFirstName(user.getFirstName());
			ret.setLastName(user.getLastName());
			ret.setNumRecords(user.getIndexedRecords());
			ret.setSuccess(true);
		}else{
			
		}
			
		return ret;
	}
	/**
	 * Used for only checking if the username and password are authentic
	 * @param login
	 * @return
	 */
	public boolean checkUser(String username, String _password){
		String password = _password;
		String sql = "SELECT password " +
				"FROM users "+
				"WHERE username = '" + username +"';";
		ResultSet rs = null;
		try(PreparedStatement stmt = db.getConnection().prepareStatement(sql);) {
			rs = stmt.executeQuery();
			if(rs.next()){
				String dbPassword = rs.getString(1);
				if(dbPassword.compareTo(password) == 0)
					return true;
			}
		}
		catch (SQLException e){
			e.printStackTrace();
		}
		return false;
		
	}

	public void updateIndexedRecords(String username, int indexedRecords) throws SQLException {
		User user = getUser(username);
		int sum = user.getIndexedRecords() + indexedRecords;
		String sql = "UPDATE users" +
					" SET IndexedRecords = " + sum +
					" WHERE UserName = '" +username +"';";
		try(PreparedStatement stmt = db.getConnection().prepareStatement(sql)){
			if(stmt.executeUpdate() != 1)
				throw new SQLException("Update IndexedRecords Error");
		}
	}
	
}
