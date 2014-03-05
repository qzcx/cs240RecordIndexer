package client.clientComm;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.*;

import server.DataImporter;
import shared.comm.Download_Results;
import shared.comm.LogIn_Params;
import shared.comm.Project_Params;
import shared.comm.Result;
import shared.comm.Search_Params;
import shared.comm.Search_Results;
import shared.comm.Submit_Params;
import shared.comm.ValidateUser_Results;
import shared.comm.Search_Results.SearchResult;

public class TestClientComm {
	ClientComm comm;
	@Before
	public void BeforeClass(){
		//load the XML data
		DataImporter dataImp = new DataImporter();
		dataImp.parseXML("./data/Records.xml");
		comm = new ClientComm("localhost", 2000);
	}
	
	@AfterClass
	public static void AfterClass(){
		//load the XML data
		DataImporter dataImp = new DataImporter();
		dataImp.parseXML("./data/Records.xml");
	}
	
	@Test
	public void ValidateUsersTest() {
		
		ValidateUser_Results check = comm.validateUser(
				new LogIn_Params("sheila","parker"));
		assertTrue(check.isSuccess());
		assertEquals("Sheila",check.getFirstName());
		assertEquals(0, check.getNumRecords());
		assertEquals("Parker", check.getLastName());
		
		check = comm.validateUser(
				new LogIn_Params("no","parker"));
		assertFalse(check.isSuccess());
	}

	
	@Test
	public void GetProjectsTest() {
		//try to submit without checking out
		Submit_Params subParams = new Submit_Params("test1","test1", 1, "fname,lname,M,12;fname2,lname2,F,13");
		Result ret = comm.submitBatch(subParams);
		
		assertFalse(ret.isSuccess());
		
		//check out a project and make sure it works
		Project_Params projParams = new Project_Params("test1", "test1", 1);
		Download_Results dlRet = comm.downloadBatch(projParams);
		assertTrue(dlRet.isSuccess());
		assertEquals(1, dlRet.getProjectId());
		assertEquals(1, dlRet.getBatchId());
		
		
		ret = comm.submitBatch(subParams);
		assertTrue(ret.isSuccess());
		
		//Search for the values entered into the database
		Search_Params params = new Search_Params("test1", "test1", "4", "12,13");
		Search_Results searchRet = comm.search(params);
		assertTrue(searchRet.isSuccess());
		assertEquals(2, searchRet.getResults().size());
		
		//pull the next batch from the database
		projParams = new Project_Params("test1", "test1", 1);
		dlRet = comm.downloadBatch(projParams);
		assertTrue(dlRet.isSuccess());
		assertEquals(1, dlRet.getProjectId());
		assertEquals(2, dlRet.getBatchId());
	}
	
	@Test
	public void SearchTest(){
		String fields = "";
		String search = "";
		
		//test without search strings
		fields+="10";
		Search_Params params = new Search_Params("test1", "test1", fields, search);
		Search_Results searchRet = comm.search(params);
		assertFalse(searchRet.isSuccess());
		fields = "";
		//test without fields
		search ="FOX";
		params = new Search_Params("test1", "test1", fields, search);
		searchRet = comm.search(params);
		assertFalse(searchRet.isSuccess());
		
		//test with a bad field ID
		fields += "-50";
		params = new Search_Params("test1", "test1", fields, search);
		searchRet = comm.search(params);
		assertFalse(searchRet.isSuccess());
		fields = "";
		search = "";
		
		//Search for a single record
		//Check each of its return values.
		fields = "10";
		search = "FOX";
		
		params = new Search_Params("test1", "test1", fields, search);
		searchRet = comm.search(params);
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
		searchRet = comm.search(params);
		assertTrue(searchRet.isSuccess());
		retList = searchRet.getResults();
		
		assertEquals(1,retList.size());
		
		//Test logic of the search, add a new search without the proper field.
		search += ", RUSSELL";
		params = new Search_Params("test1", "test1", fields, search);
		searchRet = comm.search(params);
		assertTrue(searchRet.isSuccess());
		retList = searchRet.getResults();
		
		assertEquals(1,retList.size());
		
		//add the proper field and check if two fields are returned
		fields += ",11";
		params = new Search_Params("test1", "test1", fields, search);
		searchRet = comm.search(params);
		assertTrue(searchRet.isSuccess());
		retList = searchRet.getResults();
		
		assertEquals(2,retList.size());
	}
	
}
