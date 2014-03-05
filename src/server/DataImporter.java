package server;

import shared.model.*;
import server.dba.*;
import server.dba.Database.DriverLoadingException;

import java.sql.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.SAXException;


public class DataImporter {

	private String m_projectName = "";
	private Database db;
	
	/**
	 * importDataFromZip does two things.
	 * 1. copies the folders of data to the data directory. 
	 * 2. Parses the xml file and places the information into the database
	 * @param zipPath
	 */
	public void importDataFromZip(String zipPath){
		unZipIt(zipPath);
		if(m_projectName != "")
			parseXML("."+ File.separatorChar +"data" +File.separatorChar +m_projectName+".xml");
		
	}
		
	/**
	 * Parses the Project XML file
	 */
	public void parseXML(String XMLPath) {
		try {
			Database.IntializeDatabase();
		} catch (DriverLoadingException e1) {
			e1.printStackTrace();
			return;
		}
		db = new Database();
		boolean success = false; //default roll back
		try{
			
			db.emptyDatabase();
			
			db.startTransaction();
			
			File dataXml = new File(XMLPath);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder;
			try {
				dBuilder = dbFactory.newDocumentBuilder();
				System.out.println(dataXml);
				System.out.println(XMLPath);
				Document doc = dBuilder.parse(dataXml);
				doc.getDocumentElement().normalize();
				parseUsers(doc);
				parseProjects(doc);
				
				success = true; //set commit
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} 
		
		}catch (SQLException e) {
			success = false;
			e.printStackTrace();
		}finally{
			db.endTransaction(success);
		}
		
		
		
	}

	/**
	 * parses the project tags and inserts their data into the database
	 * @param doc
	 */
	private void parseProjects(Document doc) throws SQLException{
		NodeList projectList = doc.getElementsByTagName("project");
		for(int i=0; i<projectList.getLength(); i++){
			Node projectNode = projectList.item(i);
			if (projectNode.getNodeType() == Node.ELEMENT_NODE) {
				Element projectElement = (Element) projectNode;
				Project newProject = new Project(
						extractString(projectElement, "title"),
						extractInt(projectElement, "recordsperimage"),
						extractInt(projectElement, "firstycoord"),
						extractInt(projectElement, "recordheight")
						);
				int projectId = db.getProjects().addProject(newProject);
				List<Integer> fieldIds = parseFields(projectElement, projectId);
				parseBatches(projectElement, projectId, fieldIds);
			}
		}
	}
	
	/**
	 * parses the batch tags and inserts their data into the database
	 * @param projectElement
	 * @param projectId
	 * @param fieldIds
	 */
	private void parseBatches(Element projectElement, int projectId, List<Integer> fieldIds) throws SQLException{
		NodeList batchList = projectElement.getElementsByTagName("image");
		for(int i=0; i<batchList.getLength(); i++){
			Node batchNode = batchList.item(i);
			if(batchNode.getNodeType() == Node.ELEMENT_NODE){
				Element batchElement = (Element) batchNode;
				Batch newBatch = new Batch(
						projectId,
						extractString(batchElement, "file")
						);
				int batchId = db.getBatches().addBatch(newBatch);
				parseRecords(batchElement, batchId, fieldIds);
			}
		}
	}
	
	/**
	 * parses the record tags and inserts their data into the database
	 * @param batchElement
	 * @param batchId
	 * @param fieldIds
	 */
	private void parseRecords(Element batchElement, int batchId,List<Integer> fieldIds) {
		NodeList recordList = batchElement.getElementsByTagName("record");
		for(int i=0; i<recordList.getLength(); i++){
			Node recordNode = recordList.item(i);
			if(recordNode.getNodeType() == Node.ELEMENT_NODE){
				Element recordElement = (Element) recordNode;
				NodeList valueList = recordElement.getElementsByTagName("value");
				for(int j=0; j<valueList.getLength(); j++){
					Node valueNode = valueList.item(j);
					if(valueNode.getNodeType()  == Node.ELEMENT_NODE){
						Element valueElement = (Element) valueNode;
					
						Record newRecord = new Record(
								batchId,
								fieldIds.get(j), 
								i, //recordNum
								valueElement.getTextContent().toUpperCase()
								);
						db.getRecords().addRecord(newRecord);
					
					}
				}
			}
		}
		
	}

	/**
	 * parses the field tags and inserts their data into the database
	 * @param projectElement
	 * @param projectId
	 * @return the field Ids in order that they were put in.
	 */
	private List<Integer> parseFields(Element projectElement, int projectId) throws SQLException {
		List<Integer> fieldIds = new ArrayList<Integer>();
		NodeList fieldList = projectElement.getElementsByTagName("field");
		for(int i=0; i<fieldList.getLength(); i++){
			Node fieldNode = fieldList.item(i);
			if(fieldNode.getNodeType() == Node.ELEMENT_NODE){
				Element fieldElement = (Element) fieldNode;
				Field newField = new Field(
						projectId,
						extractString(fieldElement, "title"),
						extractInt(fieldElement, "xcoord"),
						extractInt(fieldElement, "width"),
						extractString(fieldElement, "helphtml"),
						extractString(fieldElement, "knowndata")
						);
				
				int fieldId = db.getFields().addField(newField);
				fieldIds.add(fieldId);
			}
		}
		
		return fieldIds;
	}
	/**
	 * Parses the users and inserts their data into the database
	 * @param doc
	 * @throws SQLException
	 */
	private void parseUsers(Document doc) throws SQLException {
		NodeList UserList = doc.getElementsByTagName("user");
		for(int i=0; i<UserList.getLength(); i++){
			Node userNode = UserList.item(i);
			if (userNode.getNodeType() == Node.ELEMENT_NODE) {
				Element userElement = (Element) userNode;
				User newUser = new User(
						extractString(userElement, "username"),
						extractString(userElement, "password"),
						extractString(userElement, "firstname"),
						extractString(userElement, "lastname"),
						extractString(userElement, "email"),
						extractInt(userElement, "indexedrecords")
						);
				db.getUsers().addUser(newUser);
				
			}
		}
	}

	public void CopyFilesFromDirectory(File src,File dest) throws IOException{
		
		if(src.isDirectory()){
			 
    		//if directory not exists, create it
    		if(!dest.exists()){
    		   dest.mkdir();
    		   System.out.println("Directory copied from " 
                              + src + "  to " + dest);
    		}
 
    		//list all the directory contents
    		String files[] = src.list();
 
    		for (String file : files) {
    		   //construct the src and dest file structure
    		   File srcFile = new File(src, file);
    		   File destFile = new File(dest, file);
    		   //recursive copy
    		   CopyFilesFromDirectory(srcFile,destFile);
    		}
 
    	}else{
    		//if file, then copy it
    		//Use bytes stream to support all file types
    		InputStream in = new FileInputStream(src);
    	        OutputStream out = new FileOutputStream(dest); 
 
    	        byte[] buffer = new byte[1024];
 
    	        int length;
    	        //copy the file content in bytes 
    	        while ((length = in.read(buffer)) > 0){
    	    	   out.write(buffer, 0, length);
    	        }
 
    	        in.close();
    	        out.close();
    	        System.out.println("File copied from " + src + " to " + dest);
    	}
	}
	
	
	/**
	 * Code borrowed and modified from http://www.mkyong.com/java/how-to-decompress-files-from-a-zip-file/
	 * I had to adjust this code so it would work correctly with the zip file.
	 * @param zipFile
	 */
	public void unZipIt(String zipFile){
		 
	     byte[] buffer = new byte[1024];
	     String outputPath = "./data/";
	     try{
	    	 
	    	//create output directory is not exists
	    	File folder = new File(outputPath);
	    	if(!folder.exists()){
	    		folder.mkdir();
	    	}
	 
	    	//get the zip file content
	    	ZipInputStream zis = 
	    		new ZipInputStream(new FileInputStream(zipFile));
	    	//get the zipped file list entry
	    	ZipEntry ze = zis.getNextEntry();
	 
	    	while(ze!=null){
	 
	    		String fileName = ze.getName();
	    		
	    		if(m_projectName == "")
	    			m_projectName = fileName.substring(0,fileName.indexOf('/'));
	    		
	           	File newFile = new File(outputPath + File.separator + fileName.substring(fileName.indexOf('/')));
	 
           		System.out.println("file unzip : "+ newFile.getAbsoluteFile());
	 
	            //create all non exists folders
	            //else you will hit FileNotFoundException for compressed folder
	           	if(ze.isDirectory()){
	           		newFile.mkdirs();
	           	}else{
	           		new File(newFile.getParent()).mkdirs();
	 
		            FileOutputStream fos = new FileOutputStream(newFile);             
		 
		            int len;
		            while ((len = zis.read(buffer)) > 0) {
		            	fos.write(buffer, 0, len);
		            }
	 
		            fos.close();   
	           	}
	            ze = zis.getNextEntry();
	            
	    	}
	 
	        zis.closeEntry();
	    	zis.close();
	 
	    	System.out.println("Done");
	 
	    }catch(IOException ex){
	       ex.printStackTrace(); 
	    }
	   }  
	
	
	/**
	 * returns a string of the content of the first element with the tagName given
	 * @param e
	 * @param tagName-xml tag of the element you want the data from
	 * @return if the tag is not found then an empty String is returned
	 */
	private String extractString(Element e, String tagName){
		NodeList nList = e.getElementsByTagName(tagName);
		if(nList.getLength()> 0)
			return nList.item(0).getTextContent();
		else
			return "";
	}
	
	/**
	 * returns an int of the content of the first element with the tagName given
	 * @param e./data/Records.xml

	 * @param tagName-xml tag of the element you want the data from
	 * @return if the tag is not found then return -1
	 */
	private int extractInt(Element e, String tagName){
		NodeList nList = e.getElementsByTagName(tagName);
		if(nList.getLength()> 0)
			return Integer.parseInt(nList.item(0).getTextContent());
		else
			return -1;
	}
	
	public static void main(String[] args) {
		DataImporter myDataImporter = new DataImporter();
		if(args.length <1){
			//System.out.println("Missing the zip filepath");
			myDataImporter.parseXML(/*"."+File.separatorChar+*/"data"+File.separatorChar+"Records.xml");
		}else{
			String xmlPath = args[0];
			myDataImporter.parseXML(xmlPath);
			File xmlFile = new File(xmlPath);
			File parent = xmlFile.getParentFile();
			try {
				myDataImporter.CopyFilesFromDirectory(parent, new File("./data"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
	}
	

}
