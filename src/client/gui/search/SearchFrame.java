package client.gui.search;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import shared.comm.*;
import client.clientComm.ClientComm;
import client.gui.dialogs.ConnectDialog;

@SuppressWarnings("serial")
public class SearchFrame extends JFrame {

	//Member Variable
	private ConnectDialog m_connect;
	private ClientComm m_comm;
	LogIn_Params m_creditials;
	
	private SearchPanel m_searchPanel;
	private ResultPanel m_resultPanel;
	
	
	public SearchFrame(){
		//construct a menu
		m_comm = new ClientComm("localhost", 2000);//create it with defaults
		m_creditials = new LogIn_Params("","");
		
		setupMenu();
		
		JPanel centerPanel = new JPanel();
		
		//test setup
		m_searchPanel = new SearchPanel(this, m_comm, m_creditials);
		m_resultPanel = new ResultPanel(m_comm);
		
		//Set default sizes
		m_searchPanel.setPreferredSize(new Dimension(500,800));
		m_resultPanel.setPreferredSize(new Dimension(500,800));
		
		
		centerPanel.add(m_searchPanel, BorderLayout.WEST);
		centerPanel.add(m_resultPanel, BorderLayout.EAST);
		
		add(centerPanel, BorderLayout.CENTER);
		pack();
		
	}

	/**
	 * SetupMenu: initializes the menu
	 * The menu will have the following Items:
	 * Connect: opens the Connect Dialog
	 * Exit: exits the program
	 */
	private void setupMenu() {
		
		JMenuBar menuBar= new JMenuBar();
		setJMenuBar(menuBar);
		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);
		
		JMenuItem connectItem = new JMenuItem("Connect");
		connectItem.addActionListener(new ConnectAction());
		fileMenu.add(connectItem);
		
		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.addActionListener(new ActionListener() 
			{
				@Override
				public void actionPerformed(ActionEvent e) {
					System.exit(0);
				}
			});
		fileMenu.add(exitItem);
	}
	
	
	public class ConnectAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			//if first time, create dialog
			if(m_connect == null)
				m_connect = new ConnectDialog();
			//set defaults/previous values
			m_connect.setHost(m_comm.getHost());
			m_connect.setPort(m_comm.getPort());

			m_connect.setUsername(m_creditials.getUsername());
			
			//pop up dialog
			if(m_connect.showDialog(SearchFrame.this, "Connect")){
				//if accepted run connect logic
				List<String> params = m_connect.getResponse();
				System.out.println(params);
				m_comm.setHost(params.get(0));
				m_comm.setPort(Integer.parseInt(params.get(1)));

				String username = params.get(2);
				String password = params.get(3);
				m_creditials.setUsername(username);
				m_creditials.setPassword(password);
				
				GetProjects_Results projRet = m_comm.getProjects(new LogIn_Params(username, password));
				GetFields_Results fieldRet = m_comm.getFields(new Project_Params(username, password));
				
				if(projRet ==null || fieldRet == null || !projRet.isSuccess() 
						|| !fieldRet.isSuccess()){ //check to see if project connected properly
					JOptionPane.showMessageDialog(SearchFrame.this,
						    "Could not connect to server. Please check host, port, username and password and try again",
						    "connection error",
						    JOptionPane.ERROR_MESSAGE);
					
				}else{
					//Set the Projects and fields 
					m_searchPanel.SetProjectsAndFields(projRet.getProjects(), fieldRet.getFields());
				}
			}
		}
	}
	
	public void setSearchResults(List<String> searchResults) {
		m_resultPanel.updateResults(searchResults);
	}
	
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				final SearchFrame ex = new SearchFrame();
				ex.setVisible(true);
			}
		});
	}


	
}
