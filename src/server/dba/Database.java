package server.dba;

import java.io.File;
import java.sql.*;

import shared.comm.*;



/**
 * Contains the database connection
 * Has pointers to all Database access classes
 * Acts as an interface to the database access classes
 * Contains all database functions which the client can call.
 * @author jageorge
 *
 */
public class Database {
	
	//Static Globals
	private  final static String USERS_DROP_TABLE = "DROP TABLE if exists users;";
	private final static String PROJECTS_DROP_TABLE = "DROP TABLE if exists projects;";
	private final static String FIELDS_DROP_TABLE = "DROP TABLE if exists fields;";
	private final static String BATCHES_DROP_TABLE = "DROP TABLE if exists batches;";
	private final static String RECORDS_DROP_TABLE = "DROP TABLE if exists records;";
	
	private final static String USERS_CREATE_TABLE = 
			"CREATE TABLE Users (Id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL,"+
					"UserName TEXT NOT NULL  UNIQUE , "+
					"Password TEXT NOT NULL , "+
					"FirstName TEXT, "+
					"LastName TEXT, "+
					"Email TEXT, "+
					"IndexedRecords INTEGER DEFAULT 0);";
	private final static String PROJECTS_CREATE_TABLE = 
			"CREATE TABLE Projects (Id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL,"+
					"Title TEXT NOT NULL  UNIQUE,"+
					"RecordsPerImage INTEGER NOT NULL,"+
					"FirstYCoord INTEGER NOT NULL,"+
					"RecordHeight INTEGER NOT NULL);";
	private final static String FIELDS_CREATE_TABLE = 
			"CREATE TABLE Fields (Id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL, "+
					"ProjectId INTEGER NOT NULL, "+
					"Name TEXT NOT NULL, "+
					"XCoord INTEGER NOT NULL, "+
					"Width INTEGER NOT NULL, "+
					"HelpHtml TEXT, "+
					"KnownData TEXT);";
	private final static String BATCHES_CREATE_TABLE = 
			"CREATE TABLE Batches (Id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL,"+
					"FilePath TEXT NOT NULL , "+
					"ProjectId INTEGER NOT NULL, "+
					"UserId INTEGER, "+
					"IsIndexed BOOL DEFAULT FALSE);";
	private final static String RECORDS_CREATE_TABLE = 
			"CREATE TABLE Records (Id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL, "+
					"Value TEXT NOT NULL , "+
					"FieldId INTEGER NOT NULL ,"+
					"RecordNum INTEGER NOT NULL,"+
					"BatchId INTEGER NOT NULL )";
	
	
	
	//Constants
	private static final String DB_NAME = "data" + File.separator + "IndexDb.sqlite";
	private static final String DB_CONNECTION_URL = "jdbc:sqlite:" + DB_NAME;
	//Members
	private Connection connection;
	private UsersDba m_users;
	private ProjectsDba m_projects;
	private FieldsDba m_fields;
	private BatchesDba m_batches;
	private RecordsDba m_records;
	
	public static void IntializeDatabase() throws DriverLoadingException{
		try {
			final String driver = "org.sqlite.JDBC";
			Class.forName(driver);
		}catch(ClassNotFoundException e){
				throw new DriverLoadingException();
		}
	}
	
	/**
	 * Initializes the connection to the database
	 */
	public Database(){
		m_users = new UsersDba(this);
		m_projects = new ProjectsDba(this);
		m_fields = new FieldsDba(this);
		m_batches = new BatchesDba(this);
		m_records = new RecordsDba(this);
	}
	
	protected Connection getConnection() {
		return connection;
	}

	//Should this even exist?
	protected void setConnection(Connection connection) {
		this.connection = connection;
	}
	
	@SuppressWarnings("serial")
	public static class DriverLoadingException extends Exception{}
	
	public void startTransaction(){ //throws ServerException?
		
		
		try {
		    // Open a database connection
		connection = DriverManager.getConnection(DB_CONNECTION_URL);
		    
		// Start a transaction
		connection.setAutoCommit(false);
		}
		catch (SQLException e) {
		   e.printStackTrace();
		}
	}
	
	public void endTransaction(boolean commit) {
		try{
			if(commit){
				connection.commit();
			}else{
				connection.rollback();
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
		    try { //TODO ask TA about this exception which can be thrown.
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public void emptyDatabase(){
		
		
		startTransaction();
		try(Statement sql = connection.createStatement();){
			sql.execute(USERS_DROP_TABLE);
			sql.execute(PROJECTS_DROP_TABLE);
			sql.execute(FIELDS_DROP_TABLE);
			sql.execute(BATCHES_DROP_TABLE);
			sql.execute(RECORDS_DROP_TABLE);		
			sql.execute(USERS_CREATE_TABLE);
			sql.execute(PROJECTS_CREATE_TABLE);
			sql.execute(FIELDS_CREATE_TABLE);
			sql.execute(BATCHES_CREATE_TABLE);
			sql.execute(RECORDS_CREATE_TABLE);
			endTransaction(true);
		}
		catch(SQLException e){
			e.printStackTrace();
			endTransaction(false);
		}
	
	}
	
	public UsersDba getUsers() {
		return m_users;
	}

	public ProjectsDba getProjects() {
		return m_projects;
	}

	public FieldsDba getFields() {
		return m_fields;
	}

	public BatchesDba getBatches() {
		return m_batches;
	}

	public RecordsDba getRecords() {
		return m_records;
	}
	
	public ValidateUser_Results validateUser(LogIn_Params logIn){	
		return m_users.validateUser(logIn);
	}
	
	public GetProjects_Results getProjects(LogIn_Params logIn){
		return m_projects.getProjects(logIn);
	}
	
	public GetSample_Results getSample(Project_Params project){
		return m_batches.getSample(project);
	}
	
	public Download_Results downloadBatch(Project_Params project){
		return m_batches.downloadBatch(project);
	}

	public Result submitBatch(Submit_Params submit){
		return m_records.submitBatch(submit);
	}
	
	public GetFields_Results getFields(Project_Params fields){
		return m_fields.getFields(fields);
	}
	
	public Search_Results search(Search_Params search){
		return m_records.search(search);
	}
	
	public static void main(String[] args) {
		Database db = new Database();
		try {
			db.startTransaction();
			DatabaseMetaData md = db.getConnection().getMetaData();
			ResultSet rs = md.getTables(null, null, "%", null);
			while (rs.next()) {
			  System.out.println(rs.getString(3));
			}
		}  catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			db.endTransaction(false);
		}
	}
	
}
