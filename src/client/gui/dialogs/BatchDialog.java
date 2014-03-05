package client.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import shared.comm.GetProjects_Results;
import shared.comm.GetSample_Results;
import shared.comm.LogIn_Params;
import shared.comm.Project_Params;
import client.clientComm.ClientComm;
import client.exceptions.ServerException;

@SuppressWarnings("serial")
public class BatchDialog extends JPanel {

	//member variables
	ClientComm m_comm;
	LogIn_Params m_creditials;
	Map<Integer, String> m_projectMap;
	
	//Component Variables
	private JComboBox<String> m_projects;
	private JButton m_sampleButton;
	private JButton m_downloadButton;
	private boolean m_okFlag;
	private JDialog m_dialog;
	
	public BatchDialog(){
		dialogLayoutInit();
	}
	
	public void initDialog(ClientComm comm, LogIn_Params creditials) throws ServerException{
		//if(m_projects.getItemCount() > 0){
			m_comm = comm;
			m_creditials = creditials;
			GetProjects_Results projRet = m_comm.getProjects(m_creditials);
			if(projRet == null || !projRet.isSuccess())
				throw new ServerException();
			else{
				//Initialize the comboBox
				m_projectMap = projRet.getProjects();
				for(Integer key: m_projectMap.keySet()){
					m_projects.addItem(projRet.getProjects().get(key));
				}
			}
		//}
			
	}


	private void dialogLayoutInit() {
		setLayout(new BorderLayout());
		//Parameter Panel
		JPanel panel = new JPanel();
		panel.add(new JLabel("Project:"));
		panel.add(m_projects = new JComboBox<String>());
		
		m_sampleButton = new JButton("View Sample");
		m_sampleButton.addActionListener(new SampleAction());
		panel.add(m_sampleButton);
		
		
		add(panel, BorderLayout.CENTER);
		
		//Bottom Panel
		JPanel buttonPanel = new JPanel();
		m_downloadButton = new JButton("Download");
		m_downloadButton.addActionListener(new ActionListener() 
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

		buttonPanel.add(cancelButton);
		buttonPanel.add(m_downloadButton);
		add(buttonPanel, BorderLayout.SOUTH);
	}
	
	
	public boolean showDialog(Component parent, String title, ClientComm comm, LogIn_Params creditials){
		
		//locate the owner frame
		Frame owner = null;
		if(parent instanceof Frame)
			owner = (Frame) parent;
		else
			owner = (Frame) SwingUtilities.getAncestorOfClass(Frame.class, parent);
		
		m_okFlag = false;
		try {
			initDialog(comm, creditials);
		} catch (ServerException e) {
			JOptionPane.showMessageDialog(owner,
				    "Could not connect to server. Please make sure server is running",
				    "connection error",
				    JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			return m_okFlag;
		}
		
		//if first time or owner has changed, create a dialog
		if(m_dialog == null || m_dialog.getOwner() != owner){
			m_dialog = new JDialog(owner, true);
			m_dialog.setResizable(false);
			m_dialog.setModal(true);
			m_dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			m_dialog.add(this);
			m_dialog.getRootPane().setDefaultButton(m_downloadButton);
			m_dialog.pack();
		}
		
		//set title and display
		m_dialog.setTitle(title);
		m_dialog.setVisible(true);
		
		return m_okFlag;
	}
	
	public int getSelectedProjectId() {
		String projectName = (String)m_projects.getSelectedItem();
		//find the projectId which maps to the given project name.
		int projectId = -1;
		for(Integer key:m_projectMap.keySet()){
			if(m_projectMap.get(key).equals(projectName)){
				projectId = key;
				break;
			}
		}
		return projectId;
	}
	
	public class SampleAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent event) {
			int projectId = getSelectedProjectId();
			
			GetSample_Results sampleRet = m_comm.getSample(new Project_Params(m_creditials.getUsername(),
							m_creditials.getPassword(), projectId));
			if(sampleRet != null)
				System.out.println("Sample URL" + sampleRet.getImageURL());
			
			if(sampleRet == null || !sampleRet.isSuccess()){
				JOptionPane.showMessageDialog(BatchDialog.this,
					    "Could not connect to server. Please make sure server is running",
					    "connection error",
					    JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			String url = sampleRet.getImageURL();
			if(url != null){
				String imagePath = url.substring(url.indexOf('/',7));
				File retFile = m_comm.downloadURL(imagePath);
				BufferedImage image;
				try {
					image = ImageIO.read(retFile);
				} catch (IOException exception) {
					exception.printStackTrace();
					return;
				}
				JLabel lbl = new JLabel(new ImageIcon(image));
			    JOptionPane.showMessageDialog(null, lbl, "ImageDialog", 
			                                 JOptionPane.PLAIN_MESSAGE, null);
			}
		}
	}
	
	
}
