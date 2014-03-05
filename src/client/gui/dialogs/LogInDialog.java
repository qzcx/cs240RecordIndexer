package client.gui.dialogs;

import java.util.*;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import client.gui.util.*;

import javax.swing.*;

import modifiedSwingComponents.JNumberTextField;

@SuppressWarnings("serial")
public class LogInDialog extends JPanel {
	//Member Variables
	private JTextField m_username;
	private JPasswordField m_password;
	private JButton m_okButton;
	private boolean m_okFlag;
	private JDialog m_dialog;
	
	public LogInDialog(){
		setLayout(new BorderLayout());
		//Parameter Panel
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(5,5,5,5);
		
		
		GridBagUtil.setGrid(c, 0, 0);
		panel.add(new JLabel("Username:"),c);
		GridBagUtil.setGrid(c, 0, 1, 3);
		panel.add(m_username = new JTextField(25),c);
		GridBagUtil.setGrid(c, 1, 0);
		panel.add(new JLabel("Password:"),c);
		GridBagUtil.setGrid(c, 1, 1, 3);
		panel.add(m_password = new JPasswordField(25),c);
		add(panel, BorderLayout.CENTER);
		
		
		//Button Panel
		JPanel buttonPanel = new JPanel();
		
		m_okButton = new JButton("Login");
		m_okButton.addActionListener(new ActionListener() 
			{
				@Override
				public void actionPerformed(ActionEvent e) {
					m_okFlag = true;
					m_dialog.setVisible(false);
				}
			});
		
		JButton cancelButton = new JButton("Exit");
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
	
	/**
	 * 
	 * @return A list of two values, the username, and the password
	 */
	public java.util.List<String> getResponse(){
		List<String> ret = new ArrayList<String>();
		ret.add(m_username.getText());
		ret.add(String.copyValueOf(m_password.getPassword()));//possible security issue in a real world program.
		return ret;
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
			m_dialog.setResizable(false);
			m_dialog.setModal(true);
			m_dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			m_dialog.add(this);
			m_dialog.getRootPane().setDefaultButton(m_okButton);
			m_dialog.pack();
		}
		
		//set title and display
		m_dialog.setTitle(title);
		m_dialog.setVisible(true);
		
		return m_okFlag;
	}
	
	public void setUsername(String user){
		m_username.setText(user);
	}	
}
