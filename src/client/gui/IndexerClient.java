package client.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import shared.comm.LogIn_Params;
import shared.comm.ValidateUser_Results;
import client.clientComm.ClientComm;
import client.gui.dialogs.LogInDialog;
import client.gui.search.SearchFrame;

@SuppressWarnings("serial")
public class IndexerClient  {

	private LogInDialog m_login;
	private LogIn_Params m_creditials;
	private ClientComm m_comm;
	
	private IndexerFrame m_indexerFrame;
	
	public IndexerClient(String host, int port){
		m_login = new LogInDialog();
		m_creditials = new LogIn_Params("","");
		m_comm = new ClientComm(host, port);
	}
	
	private void GUIinit() {
		m_indexerFrame = new IndexerFrame(m_comm, m_creditials, new LogoutAction());
	}

	public void logIn(){
		m_login = new LogInDialog();
		if(logInPrompt()){
			GUIinit();
			//TODO load config or clear.
			m_indexerFrame.setVisible(true);
		}else{
			System.exit(0);
		}
	}
	
	public boolean logInPrompt(){
		while(true){ //repeat until returning valid user info, or exit.
			if(m_login.showDialog(null, "Login to Indexer")){
				List<String> params = m_login.getResponse();
				m_creditials.setUsername(params.get(0));
				m_creditials.setPassword(params.get(1));
				ValidateUser_Results userRet = m_comm.validateUser(m_creditials);
				if(userRet == null || !userRet.isSuccess()){ //check to see if project connected properly
					JOptionPane.showMessageDialog(m_login,
						    "Could not connect to server. Please check host, port, username and password and try again",
						    "connection error",
						    JOptionPane.ERROR_MESSAGE);
					
				}else{
					String message = "Welcome, " + userRet.getFirstName() + " " + 
							userRet.getLastName() + ".\n" +
							"You have indexed " + userRet.getNumRecords()+ " records.";
					JOptionPane.showMessageDialog(m_login,
							message,
						    "Welcome to Indexer",
						    JOptionPane.PLAIN_MESSAGE);
					m_login.setVisible(false);
					return true;
				}
			}
			else{
				System.exit(0);
			}
		}
	}
	
	public class LogoutAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			//TODO Save
			m_indexerFrame.setVisible(false); //hide the old frame
			logIn();
		}
	}
	

	public static void main(String[] args) {
		String host = "localhost";
		int port = 2001;
		if(args.length >= 2){
			host = args[0];
			try{
			port = Integer.parseInt(args[1]);
			}catch(NumberFormatException e){
				//bad port argument
				System.err.println("Bad port number, using default of 2000");
			}
		}
		
		IndexerClient myClient = new IndexerClient(host, port);
		myClient.logIn();
	}

}


















