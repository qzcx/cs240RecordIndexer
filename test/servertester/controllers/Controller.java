package servertester.controllers;

import java.util.*;

import client.clientComm.ClientComm;
import servertester.views.*;
import shared.comm.*;

public class Controller implements IController {

	private IView _view;
	private ClientComm clientComm;
	
	public Controller() {
		clientComm = new ClientComm("localhost", 8080);
		return;
	}
	
	public IView getView() {
		return _view;
	}
	
	public void setView(IView value) {
		_view = value;
	}
	
	// IController methods
	//
	
	@Override
	public void initialize() {
		getView().setHost("localhost");
		getView().setPort("39640");
		operationSelected();
	}

	@Override
	public void operationSelected() {
		ArrayList<String> paramNames = new ArrayList<String>();
		paramNames.add("User");
		paramNames.add("Password");
		
		switch (getView().getOperation()) {
		case VALIDATE_USER:
			break;
		case GET_PROJECTS:
			break;
		case GET_SAMPLE_IMAGE:
			paramNames.add("Project");
			break;
		case DOWNLOAD_BATCH:
			paramNames.add("Project");
			break;
		case GET_FIELDS:
			paramNames.add("Project");
			break;
		case SUBMIT_BATCH:
			paramNames.add("Batch");
			paramNames.add("Record Values");
			break;
		case SEARCH:
			paramNames.add("Fields");
			paramNames.add("Search Values");
			break;
		default:
			assert false;
			break;
		}
		
		getView().setRequest("");
		getView().setResponse("");
		getView().setParameterNames(paramNames.toArray(new String[paramNames.size()]));
	}

	@Override
	public void executeOperation() {
		
		
		clientComm = new ClientComm(_view.getHost(), Integer.parseInt(_view.getPort()));
		
		String input = "";
		for(String s: _view.getParameterValues())
			input +=s + "\n";
		_view.setRequest(input);
		
		
		
		switch (getView().getOperation()) {
		case VALIDATE_USER:
			validateUser();
			break;
		case GET_PROJECTS:
			getProjects();
			break;
		case GET_SAMPLE_IMAGE:
			getSampleImage();
			break;
		case DOWNLOAD_BATCH:
			downloadBatch();
			break;
		case GET_FIELDS:
			getFields();
			break;
		case SUBMIT_BATCH:
			submitBatch();
			break;
		case SEARCH:
			search();
			break;
		default:
			assert false;
			break;
		}
	}
	
	private void validateUser() {
		String[] values = _view.getParameterValues();
		ValidateUser_Results ret = clientComm.validateUser(new LogIn_Params(values[0], values[1]));
		if(ret !=null)
			_view.setResponse(ret.toString());
		else
			_view.setResponse("FAILED\n");
		
	}
	
	private void getProjects() {
		String[] values = _view.getParameterValues();
		GetProjects_Results ret = clientComm.getProjects(new LogIn_Params(values[0], values[1]));
		if(ret !=null)
			_view.setResponse(ret.toString());
		else
			_view.setResponse("FAILED\n");
	}
	
	private void getSampleImage() {
		try{
			String[] values = _view.getParameterValues();
			if(values[2].equals(""))
				values[2] = "-1";
			GetSample_Results ret = clientComm.getSample(new Project_Params(values[0], values[1], Integer.parseInt(values[2])));
			if(ret !=null)
				_view.setResponse(ret.toString());
			else
				_view.setResponse("FAILED\n");
		}catch(NumberFormatException e){
			e.printStackTrace();
		}
	}
	
	private void downloadBatch() {
		try{
			String[] values = _view.getParameterValues();
			if(values[2].equals(""))
				values[2] = "-1";
			Download_Results ret = clientComm.downloadBatch(new Project_Params(values[0], values[1], Integer.parseInt(values[2])));
			if(ret !=null)
				_view.setResponse(ret.toString());
			else
				_view.setResponse("FAILED\n");
		}catch(NumberFormatException e){
			e.printStackTrace();
		}
	}
	
	private void getFields() {
		try{
			String[] values = _view.getParameterValues();
			if(values[2].equals(""))
				values[2] = "-1";
			
			GetFields_Results ret = clientComm.getFields(new Project_Params(values[0], values[1], Integer.parseInt(values[2])));
			if(ret !=null)
				_view.setResponse(ret.toString());
			else
				_view.setResponse("FAILED\n");
		}catch(NumberFormatException e){
			e.printStackTrace();
		}
	}
	
	private void submitBatch() {
		try{
		String[] values = _view.getParameterValues();
		if(values[2].equals(""))
			values[2] = "-1";
		Result ret = clientComm.submitBatch(new Submit_Params(values[0], values[1], Integer.parseInt(values[2]), values[3]));
		if(ret !=null)
			_view.setResponse(ret.toString());
		else
			_view.setResponse("FAILED\n");
		}catch(NumberFormatException e){
			e.printStackTrace();
		}
	}
	
	private void search() {
		String[] values = _view.getParameterValues();
		Result ret = clientComm.search(new Search_Params(values[0], values[1], values[2], values[3]));
		if(!ret.equals(null))
			_view.setResponse(ret.toString());
		else
			_view.setResponse("FAILED\n");
		
		
	}

}

