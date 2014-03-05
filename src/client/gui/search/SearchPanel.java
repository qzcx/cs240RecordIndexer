package client.gui.search;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import client.clientComm.ClientComm;
import shared.comm.LogIn_Params;
import shared.comm.Search_Params;
import shared.comm.Search_Results;
import shared.comm.Search_Results.SearchResult;
import shared.model.*;

/**
 * This Panel contains a search field and a button at the top 
 * below is a panel with a list of projects as labels
 * with their fields as check boxes 
 * @author jageorge
 *
 */
public class SearchPanel extends JPanel {

	private JPanel m_fieldPanel;
	
	private JTextField m_searchField;
	private JButton m_searchButton;
	
	private List<JCheckBox> m_fieldChecks;
	
	//These are maps of Strings to field ids
	private Map<String, Integer> m_fieldMap;
	
	//Comm pointer from Frame class
	SearchFrame m_parent;
	ClientComm m_comm;
	LogIn_Params m_creditials;
	
	public SearchPanel(SearchFrame parent, ClientComm comm, LogIn_Params creditials){
		super();
		m_fieldPanel = new JPanel();
		JPanel topPanel = new JPanel(new FlowLayout());
		
		
		topPanel.add(m_searchField = new JTextField(30));
		m_searchButton = new JButton("Search");
		m_searchButton.addActionListener(new SearchAction());
		topPanel.add(m_searchButton);
		
		add(topPanel);
		add(m_fieldPanel);
		
		m_fieldChecks = new ArrayList<JCheckBox>();
		
		m_fieldMap = new HashMap<String,Integer>();
		m_comm = comm;
		m_creditials = creditials;
		m_parent = parent;
	}
	
	
	
	
	public void SetProjectsAndFields(Map<Integer, String> projects, List<Field> fields){
		//reset the panel;
		this.remove(m_fieldPanel);
		m_fieldPanel = new JPanel();
		m_fieldChecks = new ArrayList<JCheckBox>();
		
		
		//int height = projects.size() + fields.size();
		//m_fieldPanel.setLayout(new GridLayout(height,1));
		//m_fieldPanel.setLayout(new BorderLayout());
		m_fieldPanel.setLayout(new BoxLayout(m_fieldPanel, BoxLayout.Y_AXIS));
		for(Integer id: projects.keySet()){
			m_fieldPanel.add(new JLabel(projects.get(id)));
			for(Field f:fields){
				if(id == f.getProjectId()){
					JCheckBox newCheck = new JCheckBox(f.getTitle());
					m_fieldChecks.add(newCheck);
					m_fieldPanel.add(newCheck);
				}
			}
			
		}
		
		m_fieldMap.clear();
		for(int i=0; i< fields.size(); i++){
			Field f = fields.get(i);
			m_fieldMap.put(f.getTitle() + i, f.getId()); //append i to ensure that 
		}
		
		add(m_fieldPanel);
		revalidate();
		
	}
	
	
	
	public void testValues(){
		List<Project> projects = new ArrayList<Project>();
		projects.add(new Project(1, "test1", 0, 0, 0));
		projects.add(new Project(2, "test2", 0, 0, 0));
		projects.add(new Project(3, "test3", 0, 0, 0));
		projects.add(new Project(4, "test4", 0, 0, 0));
		
		List<Field> fields = new ArrayList<Field>();
		fields.add(new Field(1, 2, "field2", 0, 0, "", ""));
		fields.add(new Field(2, 4, "field4", 0, 0, "", ""));
		fields.add(new Field(3, 3, "field3", 0, 0, "", ""));
		fields.add(new Field(4, 1, "field1", 0, 0, "", ""));
		
		//SetProjectsAndFields(projects, fields);
	}
	
	public String generateFieldString(){
		String fieldString ="";
		for(int i=0; i<m_fieldChecks.size(); i++){
			if(m_fieldChecks.get(i).isSelected()){
				if(fieldString != "")
					fieldString +=",";
				fieldString += m_fieldMap.get(m_fieldChecks.get(i).getText() + i);
			}
		}
		
		return fieldString;
	}
	
	public class SearchAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String fieldString = generateFieldString();
			
			Search_Results searchRet = m_comm.search(new Search_Params(
					m_creditials.getUsername(), m_creditials.getPassword(),
					fieldString, m_searchField.getText()));
			System.out.println(searchRet);
			if(searchRet == null){
				JOptionPane.showMessageDialog(m_parent,
					    "Could not connect to server. Please check host, port, username and password and try again",
					    "connection error",
					    JOptionPane.ERROR_MESSAGE);
				return;
			}
			List<String> ret = new ArrayList<String>();
			for(SearchResult s:searchRet.getResults()){
				ret.add(s.getImageURL());
			}
			m_parent.setSearchResults(ret);
			
		}

	}
}
