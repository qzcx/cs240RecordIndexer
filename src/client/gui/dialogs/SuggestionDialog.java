package client.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

@SuppressWarnings("serial")
public class SuggestionDialog extends JPanel {
	private JList<String> m_suggestionList;
	private DefaultListModel<String> m_listModel;
	private JButton m_okButton;
	private JDialog m_dialog;
	private boolean m_okFlag;
	
	public SuggestionDialog(){
		//this.setSize(200, 400);
		this.setLayout(new BorderLayout());
		
		JPanel panel = new JPanel();
		m_listModel = new DefaultListModel<String>();
		m_suggestionList = new JList<String>(m_listModel);
		m_suggestionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		m_suggestionList.setSelectedIndex(0);
		m_suggestionList.setVisibleRowCount(5);
		m_suggestionList.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(m_suggestionList.getSelectedIndex() >=0){
					m_okButton.setEnabled(true);
				}
					
			}
		});
		
		panel.add(m_suggestionList);
		JScrollPane scroll = new JScrollPane(panel);
		scroll.setPreferredSize(new Dimension(200,200));
		add(scroll, BorderLayout.CENTER);
		//Button Panel
		JPanel buttonPanel = new JPanel();
		
		m_okButton = new JButton("Use Suggestion");
		m_okButton.addActionListener(new ActionListener() 
			{
				@Override
				public void actionPerformed(ActionEvent e) {
					m_okFlag = true;
					m_dialog.setVisible(false);
				}
			});
		
		JButton cancelButton = new JButton("Close");
		cancelButton.addActionListener(new ActionListener() 
			{
				@Override
				public void actionPerformed(ActionEvent e) {
					m_dialog.setVisible(false);
				}
			});
		
		buttonPanel.add(m_okButton);
		buttonPanel.add(cancelButton);
		add(buttonPanel, BorderLayout.SOUTH);
		
	}
	
	public void updateSuggestions(Set<String> suggestions){
		m_listModel.clear();
		SortedSet<String> sorted = new TreeSet<String>();
		sorted.addAll(suggestions);
		if(sorted.size() <=0){
			//m_listModel.addElement("Test");
		}else{
			for(String sug: sorted){
				m_listModel.addElement(sug);
			}
		}
		
	}
	
	public String getResponse(){
		return m_suggestionList.getSelectedValue();
	}
	
	
	public boolean showDialog(Component parent, String title){
		m_okFlag = false;
		
		//locate the owner frame
		Frame owner = null;
		if(parent instanceof Frame)
			owner = (Frame) parent;
		else
			owner = (Frame) SwingUtilities.getAncestorOfClass(Frame.class, parent);
		
		//if first time or owner has changed, create a dialog
		if(m_dialog == null || m_dialog.getOwner() != owner){
			m_dialog = new JDialog(owner, true);
			m_dialog.setPreferredSize(new Dimension(400,200));
			//m_dialog.setSize(200, 400);
			m_dialog.setResizable(false);
			m_dialog.setModal(true);
			m_dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			m_dialog.add(this);
			m_dialog.getRootPane().setDefaultButton(m_okButton);
			m_dialog.pack();
			
		}
		//disable the OK button before showing
		m_okButton.setEnabled(false);
		//set title and display
		m_dialog.setTitle(title);
		m_dialog.setVisible(true);
		
		return m_okFlag;
	}
	
}
