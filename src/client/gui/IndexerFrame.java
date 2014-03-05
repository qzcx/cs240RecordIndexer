package client.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;
import com.thoughtworks.xstream.io.xml.DomDriver;

import shared.comm.Download_Results;
import shared.comm.GetFields_Results;
import shared.comm.GetProjects_Results;
import shared.comm.LogIn_Params;
import shared.comm.Project_Params;
import shared.comm.Submit_Params;
import shared.model.Field;
import client.clientComm.ClientComm;
import client.gui.IndexerClient.LogoutAction;
import client.gui.components.*;
import client.gui.dialogs.BatchDialog;
import client.gui.dialogs.ConnectDialog;
import client.gui.panels.*;
import client.gui.search.SearchFrame;
import client.states.BatchState;
import client.states.ImageState;
import client.states.WindowState;

public class IndexerFrame extends JFrame {

	//Component Variables
	private CommandPanel m_commPanel;
	private ImageComp m_imageComp;
	private TableEntryPanel m_tableEntryPanel;
	private FormEntryPanel m_formEntryPanel;
	private FieldHelpPanel m_fieldHelpPanel;
	private ImageNavComp m_imageNavComp;
	private BatchDialog m_batchDialog;
	
	//Menu Item
	private JMenuItem m_downloadBatchItem;
	
	//State Variables
	private ImageState m_imageState;
	private BatchState m_batchState;
	private WindowState m_windowState;
	private boolean m_loadWindowState;
	private boolean m_loadBatchState;
	
	//SplitPane
	JSplitPane m_bottomSplitPane;
	JSplitPane m_centerSplitPane;
	//Other Variables
	private LogIn_Params m_creditials;
	private ClientComm m_comm;
	private XStream m_xStream;
	
	
	private LogoutAction m_logoutAction;
	
	
	public IndexerFrame(ClientComm comm, LogIn_Params creditials, LogoutAction logoutAction) {
		m_creditials = creditials;
		m_comm = comm;
		m_logoutAction = logoutAction;
		
		menuInit(m_logoutAction);
		compInit();
		layoutInit();
		loadState();
	}



	private void layoutInit() {
		setLayout(new BorderLayout());
		setSize(900,700);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
		    public void windowClosing(WindowEvent we) {
		        saveState();
		        System.exit(0);
		    }
		});
		
		
		//Command Panel at the top of the frame
		add(m_commPanel, BorderLayout.NORTH);
		
		//Bottom Left Tabbed Panel
		JTabbedPane entryPane = new JTabbedPane();
		entryPane.addTab("Table Entry", null, new JScrollPane(m_tableEntryPanel));
		entryPane.addTab("Form Entry", null, m_formEntryPanel);
		entryPane.setPreferredSize(new Dimension(400, 300));
		//Bottom right tabbed Panel
		JTabbedPane helpPane = new JTabbedPane();
		helpPane.addTab("Field Help", null, new JScrollPane(m_fieldHelpPanel));
		helpPane.addTab("Image Navigation", null, m_imageNavComp);
		helpPane.setPreferredSize(new Dimension(400, 300));
		
		//Bottom Split Pane
		m_bottomSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				entryPane , helpPane);
		//bottomSplitPane.
		//bottomSplitPane.setDividerLocation(500);
		//Center Split Pane
		m_centerSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				m_imageComp , m_bottomSplitPane);
		m_centerSplitPane.setDividerLocation(400);
		
		add(m_centerSplitPane, BorderLayout.CENTER);
	}
	
	private void compInit() {
		
		loadStateObjects();
		
		m_commPanel = new CommandPanel();
		m_commPanel.setZoomActionListeners(m_zoomInListener, m_zoomOutListener);
		m_commPanel.setInvertActionListener(m_invertListener);
		m_commPanel.setHighlightActionListener(m_highlightListener);
		m_commPanel.setSaveActionListener(m_saveListener);
		m_commPanel.setSubmitActionListener(m_submitListener);
		m_commPanel.setEnabled(false);
		
		
		m_imageComp = new ImageComp(m_imageState, m_batchState, m_comm);
		m_tableEntryPanel = new TableEntryPanel(m_batchState);
		m_formEntryPanel = new FormEntryPanel(m_batchState);
		m_fieldHelpPanel = new FieldHelpPanel(m_batchState, m_comm);
		m_imageNavComp = new ImageNavComp(m_imageState);
		
	}

	private void menuInit(IndexerClient.LogoutAction logoutAction){
		JMenuBar menuBar= new JMenuBar();
		setJMenuBar(menuBar);
		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);
		
		m_downloadBatchItem = new JMenuItem("Download Batch");
		m_downloadBatchItem.addActionListener(m_downloadAction);
		fileMenu.add(m_downloadBatchItem);
		
		JMenuItem logoutItem = new JMenuItem("Logout");
		logoutItem.addActionListener(logoutAction);
		fileMenu.add(logoutItem);
		
		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.addActionListener(new ActionListener() 
			{
				@Override
				public void actionPerformed(ActionEvent e) {
					saveState();
					System.exit(0);
				}
			});
		fileMenu.add(exitItem);
	}
	
	private void loadStateObjects(){
		if(m_xStream == null)
			m_xStream = new XStream(new JettisonMappedXmlDriver());
		File windowStateFile = new File(".windowState" + m_creditials.getUsername());
		File imageStateFile = new File(".imageState" + m_creditials.getUsername());
		File batchStateFile = new File(".batchState" + m_creditials.getUsername());
		try{
			if(windowStateFile.exists()){
				BufferedInputStream bwWindow = new BufferedInputStream(new FileInputStream(windowStateFile));
				m_windowState = (WindowState) m_xStream.fromXML(bwWindow);
				if(m_windowState == null){
					m_windowState = new WindowState();
					m_loadWindowState = false;
				}else{
					//this flag is necessary, because all components need to be loaded before restoring the window state.
					m_loadWindowState = true;
				}
				
			}else{
				m_windowState = new WindowState();
				m_loadWindowState = false;
			}
			
			if(imageStateFile.exists()){
				BufferedInputStream bwImage = new BufferedInputStream(new FileInputStream(imageStateFile));
				m_imageState = (ImageState) m_xStream.fromXML(bwImage);
				m_imageState.removeAllListener();
				if(m_imageState == null){
					m_imageState = new ImageState();
				}
			}else{
				m_imageState = new ImageState();
			}
			
			if(batchStateFile.exists()){
				BufferedInputStream bwBatch  = new BufferedInputStream(new FileInputStream(batchStateFile));
				m_batchState = (BatchState) m_xStream.fromXML(bwBatch);
				if(m_batchState == null){ //check if it's null
					m_batchState = new BatchState();
					m_loadBatchState = false;
				}else{ //remove listeners and set flag
					m_batchState.removeAllListener();
					//this flag is necessary, because all components need to be loaded before restoring the batch state.
					m_loadBatchState = true;
				}
			}else{
				m_batchState = new BatchState();
				m_loadBatchState = false;
			}
			
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	private void loadState() {
		if(m_loadWindowState){
			loadWindowState();
		}
		if(m_loadBatchState && m_batchState.getBatchInfo() != null){
			m_downloadBatchItem.setEnabled(false);
			m_commPanel.setEnabled(true);
			loadBatchState();
		}
		//this can always be done, sets to defaults 
		loadImageState();
		
	}

	private void saveState(){
		m_imageState.save();
		
		if(m_xStream == null)
			m_xStream = new XStream(new DomDriver());
		setWindowState();
		File windowStateFile = new File(".windowState" + m_creditials.getUsername());
		File imageStateFile = new File(".imageState" + m_creditials.getUsername());
		File batchStateFile = new File(".batchState" + m_creditials.getUsername());
		try {
			windowStateFile.createNewFile();
			imageStateFile.createNewFile();
			batchStateFile.createNewFile();
			
			BufferedOutputStream bwWindow = new BufferedOutputStream(new FileOutputStream(windowStateFile));
			BufferedOutputStream bwImage = new BufferedOutputStream(new FileOutputStream(imageStateFile));
			BufferedOutputStream bwBatch = new BufferedOutputStream(new FileOutputStream(batchStateFile));
			
			m_xStream.toXML(m_windowState, bwWindow);
			m_xStream.toXML(m_imageState, bwImage);
			m_xStream.toXML(m_batchState, bwBatch);
			
			bwWindow.close();
			bwImage.close();
			bwBatch.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	private void loadImageState(){
		m_imageState.setZoom(m_imageState.getZoomLevel());

		m_imageState.setImageLoc(m_imageState.getImageLoc());
		
		if(m_imageState.isInvert()){ //invert is flag is true
			m_imageState.invert();
			//invert changes the value of invert, change it back
			m_imageState.setInvert(!m_imageState.isInvert());
		}
		
		if(!m_imageState.isHighlight()){ //default is on, so toggle if not on
			m_imageState.toggleHighlight();
			//toggle changes the value of highlight, change it back
			m_imageState.setHighlight(!m_imageState.isHighlight());
		}
		
	}
	
	/**
	 * Before calling this method, you must first check if BatchInfo is not null
	 */
	
	private void loadBatchState(){
		m_batchState.loadBatch(getKnownValues(m_batchState.getBatchInfo()));
	}
	
	/**
	 * Loads the frame's parameters from the window state
	 */
	private void loadWindowState() {
		m_bottomSplitPane.setDividerLocation(m_windowState.getM_bottomDivide());
		m_centerSplitPane.setDividerLocation(m_windowState.getM_centerDivide());
		this.setSize(m_windowState.getM_windowWidth(), m_windowState.getM_windowHeight());
		this.setLocation(m_windowState.getM_windowPos()); 
	}
	
	/**
	 * Sets the WindowState object's parameters to the current state of the frame
	 */
	private void setWindowState() {
		m_windowState.setM_bottomDivide(m_bottomSplitPane.getDividerLocation());
		m_windowState.setM_centerDivide(m_centerSplitPane.getDividerLocation());
		m_windowState.setM_windowHeight(this.getHeight());
		m_windowState.setM_windowWidth(this.getWidth());
		m_windowState.setM_windowPos(this.getLocation());
		
	}

	private String getFieldValues(String[][] grid) {
		String fieldValues = "";
		Download_Results batchInfo = m_batchState.getBatchInfo();
		for(int i=0; i< batchInfo.getNumRecords(); i++){
			if(i != 0)
				fieldValues += ";";
			for(int j=0; j < batchInfo.getFields().size(); j++){
				if( j != 0)//if not the first value, add a comma
					fieldValues += ",";
				fieldValues += m_batchState.getGrid()[i][j+1];
			}
		}
		
		return fieldValues;
	}
	
	private Map<Integer, Set<String>> getKnownValues(Download_Results batchDownload) {
		Map<Integer, Set<String>> valueMap = new HashMap<Integer, Set<String>>();
		
		
		for(int i=0; i< batchDownload.getFields().size(); i++){
			Field f = batchDownload.getFields().get(i);
			String valueUrl = f.getKnownDataUrl();
			if(valueUrl.equals(""))
				return valueMap;
			String valuePath = valueUrl.substring(valueUrl.indexOf('/',7));
			File valueFile = m_comm.downloadURL(valuePath);
			try(BufferedReader br = new BufferedReader(new FileReader(valueFile))){
				String read = "";
				while((read = br.readLine())!= null){
					String[] split = read.split(",");
					for(String val: split){
						if(valueMap.get(i) ==null){
							valueMap.put(i, new HashSet<String>());
						}
						valueMap.get(i).add(val.toUpperCase());
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return null;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		
		System.out.println("ValueMap" + valueMap);
		return valueMap;
	}
	
	
	private ActionListener m_downloadAction = new ActionListener(){
		@Override
		public void actionPerformed(ActionEvent e) {
			if(m_batchDialog == null)
				m_batchDialog = new BatchDialog();
			
			//pop up dialog
			if(m_batchDialog.showDialog(IndexerFrame.this, "Download Batch", m_comm, m_creditials)){
				int projectId = m_batchDialog.getSelectedProjectId();
				Download_Results batchDownload = m_comm.downloadBatch(new Project_Params(m_creditials.getUsername(), m_creditials.getPassword(), projectId));
				//set the Batch info
				if(batchDownload == null || !batchDownload.isSuccess()){
					System.err.println("Download Batch Failed, likely this user already has a batch downloaded");
				}
				
				Map<Integer,Set<String>> knownValueMap = getKnownValues(batchDownload);
				
				m_batchState.setBatchInfo(batchDownload, knownValueMap);
				//disable the menu option
				m_downloadBatchItem.setEnabled(false);
				m_commPanel.setEnabled(true);
				
			}
		}

		
	};
	
	private ActionListener m_submitListener = new ActionListener(){
		@Override
		public void actionPerformed(ActionEvent e) {
			
			String fieldValues = getFieldValues(m_batchState.getGrid());
			Submit_Params submit = new Submit_Params(m_creditials.getUsername(), m_creditials.getPassword(), 
					m_batchState.getBatchInfo().getBatchId(), fieldValues);
			m_comm.submitBatch(submit);
			
			//TODO reset the GUI to empty state.
			m_tableEntryPanel.hideTable();
			m_formEntryPanel.resetPanel();
			m_imageComp.removeImage();
			m_fieldHelpPanel.resetPanel();
			m_commPanel.setEnabled(false);
			m_downloadBatchItem.setEnabled(true);
			/*IndexerFrame.this.removeAll();
			menuInit(m_logoutAction);
			compInit();
			layoutInit();*/
		}
	};
	
	private ActionListener m_zoomInListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			m_imageState.zoomIn();
			
		}
	};
	
	private ActionListener m_zoomOutListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			m_imageState.zoomOut();
			
		}
	};

	private ActionListener m_invertListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			m_imageState.invert();
		}
	};
	
	private ActionListener m_highlightListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			m_imageState.toggleHighlight();
		}
	};
	
	private ActionListener m_saveListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			saveState();
		}
	};
}
