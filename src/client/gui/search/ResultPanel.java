package client.gui.search;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.*;

import client.clientComm.ClientComm;

public class ResultPanel extends JPanel {

	
	ClientComm m_comm;
	
	JList<String> m_resultList;
	DefaultListModel<String> m_listModel;
	
	public ResultPanel(ClientComm comm) {
		super(new BorderLayout());

		m_listModel = new DefaultListModel<String>();

        //Create the list and put it in a scroll pane.
        m_resultList = new JList<String>(m_listModel);
        m_resultList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        m_resultList.setSelectedIndex(0);
        m_resultList.setVisibleRowCount(5);
        JScrollPane listScrollPane = new JScrollPane(m_resultList);

        JButton viewButton = new JButton("View");

		viewButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				viewImage();
			}
		});
        
        //Create a panel that uses BoxLayout.
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane,
                                           BoxLayout.LINE_AXIS));
        buttonPane.add(viewButton);
        

        add(listScrollPane, BorderLayout.CENTER);
        add(buttonPane, BorderLayout.NORTH);
	
		m_comm = comm;
		
		
		//testResults();
	}

	public void updateResults(List<String> results){
		m_listModel.clear();
		for(String ret: results){
			m_listModel.addElement(ret);
		}
		
	}
	
	public void testResults(){
		List<String> testList = new ArrayList<String>();
		testList.add("http://localhost:2000/images/1900_image7.png");
		updateResults(testList);
	}
	
	
	private void viewImage() {
		String url = m_resultList.getSelectedValue();
		if(url != null){
			String imagePath = url.substring(url.indexOf('/',7));
			File retFile = m_comm.downloadURL(imagePath);
			BufferedImage image;
			try {
				image = ImageIO.read(retFile);
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			JLabel lbl = new JLabel(new ImageIcon(image));
		    JOptionPane.showMessageDialog(null, lbl, "ImageDialog", 
		                                 JOptionPane.PLAIN_MESSAGE, null);
		}
		
	}
	
}
