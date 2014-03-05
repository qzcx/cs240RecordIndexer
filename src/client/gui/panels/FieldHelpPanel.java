package client.gui.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import shared.comm.Download_Results;
import client.clientComm.ClientComm;
import client.states.BatchState;

public class FieldHelpPanel extends JPanel {

	private JEditorPane m_htmlViewer;
	private BatchState m_batchState;
	//private JLabel m_htmlLabel;
	
	ClientComm m_comm;
	public FieldHelpPanel(BatchState batchState, ClientComm comm) {
		m_htmlViewer = new JEditorPane();
		m_htmlViewer.setOpaque(true);
		m_htmlViewer.setBackground(Color.white);
		m_htmlViewer.setPreferredSize(new Dimension(500, 600));
		m_htmlViewer.setEditable(false);
		m_htmlViewer.setContentType("text/html");
		
		//m_htmlLabel = new JLabel();
		add(new JScrollPane(m_htmlViewer));
		
		
		
		m_comm = comm;
		
		m_batchState = batchState;
		m_batchState.addListener(new FieldStateBatchListener());
	}

	public void resetPanel(){
		m_htmlViewer.setText("");
	}
	
	public void loadFieldHelp(int column){
		//get the help html, from this long string of accessors
		URL helpURL = null;
		try {
			helpURL = new URL(m_batchState.getBatchInfo().getFields().get(column).getHelpHtml());
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}

		try {
			m_htmlViewer.setPage(helpURL);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private class FieldStateBatchListener implements BatchState.BatchListener{

		@Override
		public void updateCell(Point cell, String value) {
			//does nothing
			return;
		}

		@Override
		public void changeSelection(Point cell) {
			loadFieldHelp(cell.x);
		}

		@Override
		public void loadBatch(Download_Results batchInfo, Map<Integer, Set<String>> knownValueMap) {
			loadFieldHelp((int)m_batchState.getSelected().getX());
		}
		
	}
	
}
