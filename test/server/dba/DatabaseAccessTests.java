package server.dba;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.*;

import server.DataImporter;
import server.dba.Database.DriverLoadingException;
import shared.comm.Download_Results;
import shared.comm.GetFields_Results;
import shared.comm.GetProjects_Results;
import shared.comm.GetSample_Results;
import shared.comm.LogIn_Params;
import shared.comm.Project_Params;
import shared.comm.Result;
import shared.comm.Search_Params;
import shared.comm.Search_Results;
import shared.comm.Search_Results.SearchResult;
import shared.comm.Submit_Params;
import shared.comm.ValidateUser_Results;
import shared.model.Field;
import shared.model.Project;
import shared.model.User;

public class DatabaseAccessTests {
	Database db;
	
	@Before
	public void Before() throws DriverLoadingException{
		Database.IntializeDatabase();
		db = new Database();
		db.emptyDatabase();
	}
	
	@Test
	public void testUsersAccess() {
		db = new Database();

		db.startTransaction();
		User test = new User(1,"username", "password", "firstname",
					"lastname", "email@gmail.com", 0);
		try {
			//add a user
			db.getUsers().addUser(test);
			//Test the getUser function
			User retUser = db.getUsers().getUser("username");
			assertEquals(test,retUser);
			
			//test the validateUser function
			ValidateUser_Results check = db.validateUser(
					new LogIn_Params("username","password"));
			assertTrue(check.isSuccess());
			assertEquals(test.getFirstName(),check.getFirstName());
			assertEquals(test.getIndexedRecords(), check.getNumRecords());
			assertEquals(test.getLastName(), check.getLastName());
			
			//Test Bad user
			check = db.validateUser(new LogIn_Params("false","password"));
			assertFalse(check.isSuccess());
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			db.endTransaction(false); //we don't want to keep this.
		}
	
		
	}

	@Test
	public void testProjectsAccess(){
		db = new Database();
		db.startTransaction();
		User testUser = new User(1,"username", "password", "firstname",
						"lastname", "email@gmail.com", 0);
		Project testProject1 = new Project(1,"One", 10, 0, 15);
		Project testProject2 = new Project(2,"Two", 20, 2, 25);
		try {
			//add a user
			db.getUsers().addUser(testUser);
			//add a couple project
			db.getProjects().addProject(testProject1);
			//Test the getProject function
			Project retProj = db.getProjects().getProject(1);
			
			assertEquals(testProject1, retProj);
			
			//add a second project
			db.getProjects().addProject(testProject2);
			
			//test the getProjects function
			GetProjects_Results ret = db.getProjects().getProjects(
					new LogIn_Params("username","password"));
			
			Map<Integer, String> expectedMap = new HashMap<Integer,String>();
			expectedMap.put(1, "One");
			expectedMap.put(2, "Two");
			//GetProjects_Results expected = new GetProjects_Results(expectedMap);
			assertTrue(ret.isSuccess());
			assertEquals(expectedMap, ret.getProjects());
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			db.endTransaction(false);
		}
	
	}
		
	@Test
	public void testFieldsAccess(){
		db = new Database();
		db.startTransaction();
		User testUser = new User(1,"username", "password", "firstname",
							"lastname", "email@gmail.com", 0);
		Project testProject = new Project(1,"One", 10, 0, 15);
		Project testProject2 = new Project(2,"Two", 20, 2, 25);
		
		Field testField1 = new Field(1,1,"Title",10,200,
				"<help_html>", "<KnownDataURL>");
		Field testField2 = new Field(2,2,"Title2",20,400,
				"<help_html2>", "<KnownDataURL2>");
		
		try {
			//add a user
			db.getUsers().addUser(testUser);
			//add a project
			db.getProjects().addProject(testProject);
			db.getProjects().addProject(testProject2);
			//add a couple fields
			db.getFields().addField(testField1);
			db.getFields().addField(testField2);
			
			//Test getting all fields
			List<Field> testList = new ArrayList<Field>();
			testList.add(testField1);
			testList.add(testField2);
			
			GetFields_Results ret = db.getFields().getFields(new Project_Params("username", "password"));
			assertTrue(ret.isSuccess());
			assertEquals(testList,ret.getFields());
			
			//Test the Field set from the first project
			ret = db.getFields().getFields(new Project_Params("username", "password", 1));
			testList.remove(testField2);
			assertTrue(ret.isSuccess());
			assertEquals(testList, ret.getFields());
			
			//Test the Field set from the second project
			ret = db.getFields().getFields(new Project_Params("username", "password", 2));
			testList.remove(testField1);
			testList.add(testField2);
			assertTrue(ret.isSuccess());
			assertEquals(testList, ret.getFields());
			
			//Test a bad user
			ret = db.getFields().getFields(new Project_Params("bad", "password", 2));
			assertFalse(ret.isSuccess());
			
			//Test a bad password
			ret = db.getFields().getFields(new Project_Params("username", "bad", 2));
			assertFalse(ret.isSuccess());
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			db.endTransaction(false);
		}
	
	}
		
	@Test
	public void testBatchesAccess() throws SQLException{
		try {
			db.startTransaction();
		
			
			//load the XML data
			DataImporter dataImp = new DataImporter();
			dataImp.parseXML("./data/Records.xml");
			
			Project_Params projParams = new Project_Params("test1", "test1", 1);
			//Test GetSample
			GetSample_Results sampleRet = db.getBatches().getSample(projParams);
			
			assertTrue(sampleRet.isSuccess());
			assertEquals("images/1890_image0.png", sampleRet.getImageURL());
			
			//test DownloadBatch
			Download_Results dlRet = db.getBatches().downloadBatch(projParams);
			
			assertTrue(dlRet.isSuccess());
			assertEquals(1, dlRet.getProjectId());
			assertEquals(1 ,dlRet.getBatchId());
			assertEquals(8, dlRet.getNumRecords());
			assertEquals(199, dlRet.getFirstYCoord());
			assertEquals(60, dlRet.getRecordHeight());
			
			
			List<Field> fields = db.getFields().getFieldList(1);
			assertEquals(fields, dlRet.getFields());
			
			//check User's batchID
			int batchId = db.getBatches().findUsersBatch("test1");
			assertEquals(1,batchId);
			
			//test downloading another result
			dlRet = db.getBatches().downloadBatch(projParams);
			assertFalse(dlRet.isSuccess());
			
			//test a new user
			projParams = new Project_Params("test2", "test2", 1);
			dlRet = db.getBatches().downloadBatch(projParams);
			assertTrue(dlRet.isSuccess());
			assertEquals(1, dlRet.getProjectId());
			assertEquals(2, dlRet.getBatchId());
			
		}  finally{
			db.endTransaction(false);//roll it back
		}
	}
	
	@Test
	public void testSubmitFunction() throws SQLException{
		db.startTransaction();
		
		//load the XML data
		DataImporter dataImp = new DataImporter();
		dataImp.parseXML("./data/Records.xml");
		
		//try to submit without checking out
		Submit_Params subParams = new Submit_Params("test1","test1", 1, "fname,lname,M,12;fname2,lname2,F,13");
		Result ret = db.submitBatch(subParams);
		
		assertFalse(ret.isSuccess());
		
		//check out a project and make sure it works
		Project_Params projParams = new Project_Params("test1", "test1", 1);
		Download_Results dlRet = db.getBatches().downloadBatch(projParams);
		assertTrue(dlRet.isSuccess());
		assertEquals(1, dlRet.getProjectId());
		assertEquals(1, dlRet.getBatchId());
		
		
		ret = db.submitBatch(subParams);
		assertTrue(ret.isSuccess());
		
		
		//check User's got reset.
		int batchId = db.getBatches().findUsersBatch("test1");
		assertEquals(-1,batchId);
		
		//Search for the values entered into the database
		Search_Params params = new Search_Params("test1", "test1", "4", "12,13");
		Search_Results searchRet = db.getRecords().search(params);
		assertTrue(searchRet.isSuccess());
		assertEquals(2, searchRet.getResults().size());
		
		//pull the next batch from the database
		projParams = new Project_Params("test1", "test1", 1);
		dlRet = db.getBatches().downloadBatch(projParams);
		assertTrue(dlRet.isSuccess());
		assertEquals(1, dlRet.getProjectId());
		assertEquals(2, dlRet.getBatchId());
		
		db.endTransaction(false);
	}
	
	@Test
	public void testSearchFunction() throws SQLException{
		try {
			db.startTransaction();
			
			//load the XML data
			DataImporter dataImp = new DataImporter();
			dataImp.parseXML("./data/Records.xml");
			
			
			String fields = "";
			String search = "";
			
			//test without search strings
			fields+="10";
			Search_Params params = new Search_Params("test1", "test1", fields, search);
			Search_Results searchRet = db.getRecords().search(params);
			assertFalse(searchRet.isSuccess());
			fields = "";
			//test without fields
			search ="FOX";
			params = new Search_Params("test1", "test1", fields, search);
			searchRet = db.getRecords().search(params);
			assertFalse(searchRet.isSuccess());
			
			//test with a bad field ID
			fields += "-50";
			params = new Search_Params("test1", "test1", fields, search);
			searchRet = db.getRecords().search(params);
			assertFalse(searchRet.isSuccess());
			fields = "";
			search = "";
			
			//Search for a single record
			//Check each of its return values.
			fields = "10";
			search = "FOX";
			
			params = new Search_Params("test1", "test1", fields, search);
			searchRet = db.getRecords().search(params);
			assertTrue(searchRet.isSuccess());
			List<SearchResult> retList = searchRet.getResults();
			
			assertEquals(1,retList.size());
			SearchResult ret = retList.get(0);
			assertEquals(41,ret.getBatchId());
			assertEquals(10,ret.getFieldId());
			assertEquals(0,ret.getRecordNum());
			
			//check case insensitivity
			search = "fOx";
			params = new Search_Params("test1", "test1", fields, search);
			searchRet = db.getRecords().search(params);
			assertTrue(searchRet.isSuccess());
			retList = searchRet.getResults();
			
			assertEquals(1,retList.size());
			
			//Test logic of the search, add a new search without the proper field.
			search += ", RUSSELL";
			params = new Search_Params("test1", "test1", fields, search);
			searchRet = db.getRecords().search(params);
			assertTrue(searchRet.isSuccess());
			retList = searchRet.getResults();
			
			assertEquals(1,retList.size());
			
			//add the proper field and check if two fields are returned
			fields += ",11";
			params = new Search_Params("test1", "test1", fields, search);
			searchRet = db.getRecords().search(params);
			assertTrue(searchRet.isSuccess());
			retList = searchRet.getResults();
			
			assertEquals(2,retList.size());
			
			
			
		}finally{
			db.endTransaction(false);//roll it back
		}
		
	}
	
}
