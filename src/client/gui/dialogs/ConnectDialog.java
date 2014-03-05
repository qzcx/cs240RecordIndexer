package client.gui.dialogs;

import java.util.*;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import client.gui.util.*;

import javax.swing.*;

import modifiedSwingComponents.JNumberTextField;

public class ConnectDialog extends JPanel {
	//Member Variables
	private JTextField m_host;
	private JNumberTextField m_port;
	private JTextField m_username;
	private JPasswordField m_password;
	private JButton m_okButton;
	private boolean m_okFlag;
	private JDialog m_dialog;
	
	public ConnectDialog(){
		setLayout(new BorderLayout());
		//Parameter Panel
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		
		GridBagUtil.setGrid(c, 0, 0);
		panel.add(new JLabel("Host:"),c);
		
		GridBagUtil.setGrid(c, 0, 1, 3);
		panel.add(m_host = new JTextField(),c);
		
		GridBagUtil.setGrid(c, 1, 0);
		panel.add(new JLabel("Port:"),c);
		
		m_port = new JNumberTextField();
		m_port.setFormat(JNumberTextField.NUMERIC);
		m_port.setAllowNegative(false);
		GridBagUtil.setGrid(c, 1, 1, 3);
		panel.add(m_port,c);
		
		GridBagUtil.setGrid(c, 2, 0);
		panel.add(new JLabel("Username:"),c);
		GridBagUtil.setGrid(c, 2, 1, 3);
		panel.add(m_username = new JTextField(),c);
		GridBagUtil.setGrid(c, 3, 0);
		panel.add(new JLabel("Password:"),c);
		GridBagUtil.setGrid(c, 3, 1, 3);
		panel.add(m_password = new JPasswordField(),c);
		add(panel, BorderLayout.CENTER);
		//Button logic
		
		m_okButton = new JButton("Ok");
		m_okButton.addActionListener(new ActionListener() 
			{
				@Override
				public void actionPerformed(ActionEvent e) {
					m_okFlag = true;
					m_dialog.setVisible(false);
				}
			});
		
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() 
			{
				@Override
				public void actionPerformed(ActionEvent e) {
					m_dialog.setVisible(false);
				}
			});
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(m_okButton);
		buttonPanel.add(cancelButton);
		add(buttonPanel, BorderLayout.SOUTH);
		
	}
	

	public java.util.List<String> getResponse(){
		List<String> ret = new ArrayList<String>();
		ret.add(m_host.getText());
		ret.add(m_port.getText());
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
			m_dialog.add(this);
			m_dialog.getRootPane().setDefaultButton(m_okButton);
			m_dialog.pack();
		}
		
		//set title and display
		m_dialog.setTitle(title);
		m_dialog.setVisible(true);
		
		return m_okFlag;
	}
	
	public void setHost(String host){
		m_host.setText(host);
	}
	public void setPort(int port){
		m_port.setText(""+port);
	}	
	public void setUsername(String user){
		m_username.setText(user);
	}	
}
