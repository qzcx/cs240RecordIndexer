package server;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.sql.SQLException;

import server.dba.Database;
import server.dba.Database.DriverLoadingException;
import shared.comm.*;

import com.sun.net.httpserver.*;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class Server {
	
	private static final int DEFAULT_SERVER_PORT_NUMBER = 2001;
	private static final int MAX_WAITING_CONNECTIONS = 10;
	
	
	private HttpServer server;
	private XStream xStream;
	
	private void run(int portNum) {
		try{
			Database.IntializeDatabase();
		
		
			xStream = new XStream(new DomDriver());
			if (portNum == -1)
				portNum = DEFAULT_SERVER_PORT_NUMBER;
			
			try {
				server = HttpServer.create(new InetSocketAddress(
						portNum),MAX_WAITING_CONNECTIONS);
			} 
			catch (IOException e) {
				e.printStackTrace();
				return;
			}
			
			server.setExecutor(null); // use the default executor
			
			server.createContext("/ValidateUser", validateUserHandler);
			server.createContext("/GetProjects", getProjectsHandler);
			server.createContext("/GetSample", getSampleHandler);
			server.createContext("/DownloadBatch",downloadBatchHandler);
			server.createContext("/SubmitBatch", submitBatchHandler);
			server.createContext("/GetFields", getFieldsHandler);
			server.createContext("/Search", searchHandler);
			server.createContext("/DownloadURL", downloadURLHandler);
			server.createContext("/", downloadFileHandler);
			
			server.start();
			
		}catch (DriverLoadingException e){
			e.printStackTrace();
		}
		
	}
	
	private HttpHandler downloadFileHandler = new HttpHandler() {

		@Override
		public void handle(HttpExchange exchange) throws IOException {

			 String url = exchange.getRequestURI().getPath();
			 
			 String filepath = "./data" + url;
			 File file = new File(filepath);
			 
			 if(!file.exists()){
				 exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST , -1);
			 }else{
				 exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
				 try(BufferedInputStream is = new BufferedInputStream(new FileInputStream(file))){
					 int nextbyte = 0;
					 while( (nextbyte = is.read()) != -1){
						 exchange.getResponseBody().write(nextbyte);
					 }
				 }
				 exchange.getResponseBody().close();
				 
			 }
			 exchange.close();
		}
	};
	
	private HttpHandler validateUserHandler = new HttpHandler() {

		@Override
		public void handle(HttpExchange exchange) throws IOException {
			 Database db = new Database();
			 LogIn_Params logIn = (LogIn_Params)xStream.fromXML(exchange.getRequestBody());
			 //exchange.getRequestBody().close();
			 ValidateUser_Results result = null;
			 db.startTransaction();
			 result = db.validateUser(logIn);
			 db.endTransaction(false); //shouln't have changed anything
		 
			 if(result.equals(null)){
				 exchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, -1);
			 }else{
				 exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
				 xStream.toXML(result, exchange.getResponseBody());
				 exchange.getResponseBody().close();
				 
			 }
			 exchange.close();
		}
	};
	
	private HttpHandler getProjectsHandler = new HttpHandler() {

		@Override
		public void handle(HttpExchange exchange) throws IOException {
			 Database db = new Database();
			 LogIn_Params project_params = (LogIn_Params)xStream.fromXML(exchange.getRequestBody());
			 db.startTransaction();
			 GetProjects_Results result = db.getProjects(project_params);
			 db.endTransaction(false);//shouln't have changed anything
			 
			 if(result.equals(null)){
				 exchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, -1);
			 }else{
				 exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
				 xStream.toXML(result, exchange.getResponseBody());
				 exchange.getResponseBody().close();
			 }
			 exchange.close();
		}
	};
	
	private HttpHandler getSampleHandler = new HttpHandler() {

		@Override
		public void handle(HttpExchange exchange) throws IOException {
			 Database db = new Database();
			 
	 		 Project_Params params = (Project_Params)xStream.fromXML(exchange.getRequestBody());
	 		 System.out.println("Sample Params: " + params);
	 		 db.startTransaction();
			 GetSample_Results result = db.getSample(params);
			 System.out.println("Sample Result: " + result);
			 db.endTransaction(false);//nothing should have changed anything
			 
			 if(result.equals(null)){
				 exchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, -1);
			 }else{
				 exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
				 xStream.toXML(result, exchange.getResponseBody());
				 exchange.getResponseBody().close();
			 }
			 exchange.close();
		}
	};
	
	
	private HttpHandler downloadBatchHandler = new HttpHandler() {
	
			@Override
			public void handle(HttpExchange exchange) throws IOException {
				 Database db = new Database();
				 
				 Project_Params params = (Project_Params)xStream.fromXML(exchange.getRequestBody());
				 db.startTransaction();
				 Download_Results result = db.downloadBatch(params);
				 
				 if(result.equals(null)){
					 exchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, -1);
					 db.endTransaction(false);//don't keep changes
				 }else{
					 db.endTransaction(result.isSuccess());
					 exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
					 xStream.toXML(result, exchange.getResponseBody());
					 exchange.getResponseBody().close();
				 }
				 exchange.close();
			}
		};
		
	private HttpHandler submitBatchHandler = new HttpHandler() {
	
			@Override
			public void handle(HttpExchange exchange) throws IOException {
				 Database db = new Database();
				 
		 		 Submit_Params params = (Submit_Params)xStream.fromXML(exchange.getRequestBody());
		 		 db.startTransaction();
				 Result result = db.submitBatch(params);
				 if(result.equals(null)){
					 exchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, -1);
					 db.endTransaction(false);//don't keep changes
				 }else{
					 db.endTransaction(result.isSuccess());
					 exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
					 xStream.toXML(result, exchange.getResponseBody());
					 exchange.getResponseBody().close();
				 }
				 exchange.close();
			}
		};
		
	private HttpHandler getFieldsHandler = new HttpHandler() {
	
			@Override
			public void handle(HttpExchange exchange) throws IOException {
				 Database db = new Database();
				 
				 Project_Params params = (Project_Params)xStream.fromXML(exchange.getRequestBody());
				 db.startTransaction();
				 GetFields_Results result = db.getFields(params);
				 db.endTransaction(false);//don't keep changes
				 if(result.equals(null)){
					 exchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, -1);
				 }else{
					 exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
					 xStream.toXML(result, exchange.getResponseBody());
					 exchange.getResponseBody().close();
				 }
				 exchange.close();
			}
		};
		
	private HttpHandler searchHandler = new HttpHandler() {
	
			@Override
			public void handle(HttpExchange exchange) throws IOException {
				 Database db = new Database();
				 
				 Search_Params params = (Search_Params)xStream.fromXML(exchange.getRequestBody());
				 db.startTransaction();
				 Search_Results result = db.search(params);
				 db.endTransaction(false);//nothing should have changed
				 if(result.equals(null)){
					 exchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, -1);
				 }else{
					 exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
					 xStream.toXML(result, exchange.getResponseBody());
					 exchange.getResponseBody().close();
				 }
				 exchange.close();
			}
		};
		
	private HttpHandler downloadURLHandler = new HttpHandler() {

		@Override
		public void handle(HttpExchange exchange) throws IOException {
			
	 		 String url = (String)xStream.fromXML(exchange.getRequestBody());
			 File result = new File("./Data/" + url);
			 if(!result.exists()){
				 exchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, -1);
			 }else{
				 exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
				 xStream.toXML(result, exchange.getResponseBody());
				 exchange.getResponseBody().close();
			 }
			 exchange.close();
		}
	};

	public static void main(String[] args) {
		int portNum=-1;
		if(args.length >0)
			portNum = Integer.parseInt(args[0]);
		new Server().run(portNum);
	}
	
}
