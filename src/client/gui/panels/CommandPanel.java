package client.gui.panels;

import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class CommandPanel extends JPanel {

	JButton m_zoomInButton;
	JButton m_zoomOutButton;
	JButton m_invertButton;
	JButton m_highlightsButton;
	JButton m_saveButton;
	JButton m_submitButton;
	
	
	public CommandPanel(){
		
		//Create and add the buttons
		m_zoomInButton = new JButton("Zoom In");
		m_zoomOutButton = new JButton("Zoom Out");
		m_invertButton = new JButton("Invert Image");
		m_highlightsButton = new JButton("Toggle Highlights");
		m_saveButton = new JButton("Save");
		m_submitButton = new JButton("Submit");
		
		add(m_zoomInButton);
		add(m_zoomOutButton);
		add(m_invertButton);
		add(m_highlightsButton);
		add(m_saveButton);
		add(m_submitButton);
	}
		
	public void setEnabled(boolean flag){
		m_zoomInButton.setEnabled(flag);
		m_zoomOutButton.setEnabled(flag);
		m_invertButton.setEnabled(flag);
		m_highlightsButton.setEnabled(flag);
		m_saveButton.setEnabled(flag);
		m_submitButton.setEnabled(flag);
	}
	
	
	public void setZoomActionListeners(ActionListener zoomIn, ActionListener zoomOut){
		m_zoomInButton.addActionListener(zoomIn);
		m_zoomOutButton.addActionListener(zoomOut);
	}
	
	public void setInvertActionListener(ActionListener invertListener){
		m_invertButton.addActionListener(invertListener);
	}
	
	public void setHighlightActionListener(ActionListener highlightListener){
		m_highlightsButton.addActionListener(highlightListener);
	}
	
	public void setSaveActionListener(ActionListener saveListener){
		m_saveButton.addActionListener(saveListener);
	}
	
	public void setSubmitActionListener(ActionListener submitListener){
		m_submitButton.addActionListener(submitListener);
	}
	
}
