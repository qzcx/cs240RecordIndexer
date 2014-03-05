package client.clientComm;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;

import org.apache.commons.io.FileUtils;

import shared.comm.*;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Client communicator class.
 * Contains all functions called by the user and sends the 
 * requests to the server.
 * Each function uses a Wrapper object 
 * @author jageorge
 *
 */
public class ClientComm {
	private XStream xStream;
	private String host;
	private int port;
	
	
	public ClientComm(String host, int port){
		xStream = new XStream(new DomDriver());
		this.host = host;
		this.port = port;
	}
	
	public ValidateUser_Results validateUser(LogIn_Params logIn){
		Object ret = doPost("/ValidateUser", logIn);
		if(ret instanceof ValidateUser_Results)
			return (ValidateUser_Results)ret;
		return null;
	}
	
	public GetProjects_Results getProjects(LogIn_Params logIn){
		Object ret = doPost("/GetProjects", logIn);
		if(ret instanceof GetProjects_Results)
			return (GetProjects_Results)ret;
		return null;
	}
	
	public GetSample_Results getSample(Project_Params project){
		Object ret = doPost("/GetSample", project);
		if(ret == null)
			return null;
		if(ret instanceof GetSample_Results){
			GetSample_Results retObj =(GetSample_Results) ret;
			retObj.setImageURL("http://" +host + ":" + port +"/" + retObj.getImageURL());
			return retObj;
		}
		return null;
	}
	
	public Download_Results downloadBatch(Project_Params project){
		Object ret = doPost("/DownloadBatch", project);
		if(ret == null)
			return null;
		if(ret instanceof Download_Results){
			Download_Results retObj =(Download_Results) ret;
			if(retObj.isSuccess())//if successful then append the appropriate information.
				retObj.AppendHostPort("http://" + host + ":" + port +"/");
			
			return retObj;
		}
		return null;
	}

	public Result submitBatch(Submit_Params submit){
		Object ret = doPost("/SubmitBatch", submit);
		if(ret == null)
			return null;
		if(ret instanceof Result)
			return (Result)ret;
		return null;
	}
	
	public GetFields_Results getFields(Project_Params fields){
		Object ret = doPost("/GetFields", fields);
		if(ret == null)
			return null;
		if(ret instanceof GetFields_Results){
			GetFields_Results retObj =(GetFields_Results) ret;
			if(retObj.isSuccess())//if successful then append the appropriate information.
				retObj.AppendHostPort("http://" + host + ":" + port +"/");
			return (GetFields_Results)ret;
		}
		return null;
	}
	
	public Search_Results search(Search_Params search){
		Object ret = doPost("/Search", search);
		if(ret == null)
			return null;
		if(ret instanceof Search_Results){
			Search_Results retObj =(Search_Results) ret;
			if(retObj.isSuccess())//if successful then append the appropriate information.
				retObj.AppendHostPort("http://" + host + ":" + port +"/");
			return (Search_Results)retObj;
		}
		return null;
	}
	
	public File downloadURL(String url){
		//first check if the file exists on disk already. 
		File check = new File("./clientData" + url);
		if(check.exists()){
			return check;
		}else{ //If not then ask the server for it.
			Object ret = doGetFile(url);
			if(ret instanceof File)
				return (File)ret;
			return null;
		}
	}
	
	private File doGetFile(String urlPath){
		HttpURLConnection connection = null;
		try {
			URL url = new URL(getPrefix() + urlPath);
			connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			// Set HTTP request headers, if necessary
			//connection.addRequestProperty("Accept", urlPath);
			
			connection.connect();
			
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				InputStream responseBody = connection.getInputStream();
				File retFile = new File("./clientData" + urlPath);
				FileUtils.forceMkdir(retFile.getParentFile()); //make the directory if it doesn't exist.
				retFile.createNewFile();
				BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(retFile));
				int next;
				while( (next = responseBody.read()) != -1)
					os.write(next);
				responseBody.close();
				os.close();
				return retFile;
			}
			else {
				return null;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch(ConnectException e){
			System.out.println("Connection to Server Failed");
		}catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			connection.disconnect();
		}
		
		
		
		
		return null;
	}
	
	private Object doPost(String urlPath, Object postData) {
		HttpURLConnection connection = null;
		try {
			URL url = new URL(getPrefix() + urlPath);
			connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			// Set HTTP request headers, if necessary
			//connection.addRequestProperty("Accept", urlPath);
			
			connection.connect();
			OutputStream requestBody = connection.getOutputStream();
			xStream.toXML(postData, requestBody);
			requestBody.close();
			
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				InputStream responseBody = connection.getInputStream();
				Object retObj =  xStream.fromXML(responseBody);
				responseBody.close();
				return retObj;
			}
			else {
				return null;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch(ConnectException e){
			System.out.println("Connection to Server Failed");
		}catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			connection.disconnect();
		}
		return null;
	}

	private String getPrefix() {
		return "http://" + host + ":" + port;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	
}
