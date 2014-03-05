package client.gui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import shared.comm.Download_Results;
import shared.model.Field;
import client.gui.dialogs.SuggestionDialog;
import client.gui.util.GridBagUtil;
import client.spell.SuggestionUtil;
import client.states.BatchState;

@SuppressWarnings("serial")
public class FormEntryPanel extends JPanel {

	private BatchState m_batchState;
	private JList<String> m_recordList;
	private DefaultListModel<String> m_listModel;
	
	private SuggestionDialog m_sugDialog;
	
	private List<JTextField> m_fieldList;
	private Map<Integer, Set<String>> m_valueMap;
	
	private Point m_selCell; 
	
	public FormEntryPanel(BatchState batchState) {
		m_batchState = batchState;
		m_batchState.addListener(new FormBatchListener());
		
		m_listModel = new DefaultListModel<String>();

		m_recordList = new JList<String>(m_listModel);
		
		m_recordList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		m_recordList.setSelectedIndex(0);
		m_recordList.setVisibleRowCount(5);
		
		m_sugDialog = new SuggestionDialog();
		
	}
	
	public void resetPanel(){
		this.removeAll();
	}

	private class FormBatchListener implements BatchState.BatchListener{

		@Override
		public void updateCell(Point cell, String value) {
			setTextFieldValues(cell.y);
//			JTextField field = m_fieldList.get(cell.x -1);
//			if(cell.x - 1 >= 0 && m_valueMap != null && field.getText() != null && m_valueMap.size() >0 && !value.equals("")){
//				if(m_valueMap.get(cell.x -1) != null && m_valueMap.get(cell.x -1).size() > 0 && !m_valueMap.get(cell.x -1).contains(value.toString().toUpperCase()) ){
//					field.setBackground(Color.RED);
//		    	}else{
//		    		field.setBackground(Color.WHITE);
//		    	}
//			}else{
//				field.setBackground(Color.WHITE);
//			}
			
		}

		@Override
		public void changeSelection(Point cell) {
			m_recordList.setSelectedIndex(cell.y);
			m_fieldList.get(cell.x).requestFocus();
		}

		@Override
		public void loadBatch(Download_Results batchInfo, Map<Integer, Set<String>> knownValueMap) {
			m_valueMap = knownValueMap;
			List<Field> fields = batchInfo.getFields();
			//Adds the number of records to the list
			for(int i=0; i< batchInfo.getNumRecords(); i++){
				m_listModel.addElement("" + i + "     "); //add some spaces to make the list wider
			}
			
			//set List Action Listener
			m_recordList.addListSelectionListener(listListener);
			
			
			setLayout(new BorderLayout());
			add(new JScrollPane(m_recordList), BorderLayout.WEST);
			

			JPanel eastPanel = new JPanel();
			eastPanel.setLayout(new GridBagLayout());
			
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.anchor = GridBagConstraints.WEST;
			c.insets = new Insets(5,5,5,5);
			
			
			m_fieldList = new ArrayList<JTextField>();
			//Adds a label and textbox for each field
			for(int i=0; i<fields.size(); i++){
				Field f = fields.get(i);
				GridBagUtil.setGrid(c, i, 0);
				JLabel fieldText = new JLabel(f.getTitle());
				eastPanel.add(fieldText,c);
				GridBagUtil.setGrid(c, i, 1);
				JTextField textField = new JTextField(15);
				//add the action listener which takes care of changing values and selections.
				textField.addFocusListener(textFieldFocusListener);
				textField.addMouseListener(m_mouseListener);
				
				m_fieldList.add(textField);
				eastPanel.add(textField, c);
			}
			add(new JScrollPane(eastPanel), BorderLayout.CENTER);
		}
		
	}
	
	private void setTextFieldValues(int record){
		for(int i=0; i<m_fieldList.size(); i++){
			JTextField field =  m_fieldList.get(i);
			String gridValue = m_batchState.getGrid()[record][i + 1];
			field.setText(gridValue);
			
			if(i >= 0 && m_valueMap != null && gridValue != null && m_valueMap.size() >0 && !gridValue.equals("")){
				if(m_valueMap.get(i) != null && m_valueMap.get(i).size() > 0 && !m_valueMap.get(i).contains(gridValue.toString().toUpperCase()) ){
					field.setBackground(Color.RED);
					//System.out.println(m_valueMap.get(column -1));
		    	}else{
		    		field.setBackground(Color.WHITE);
		    	}
			}else{
				field.setBackground(Color.WHITE);
			}
			
		}
	}
	
	
	private ListSelectionListener listListener = new ListSelectionListener() {
		@Override
		public void valueChanged(ListSelectionEvent e) {
			//System.out.println(m_batchState);
			m_batchState.changeSelection(new Point(m_batchState.getSelected().x,m_recordList.getSelectedIndex()));
			setTextFieldValues(m_recordList.getSelectedIndex());
		}
	};
	
	private FocusListener textFieldFocusListener = new FocusListener() { 
        @Override
        public void focusGained(FocusEvent e) {
        	JTextField focus = (JTextField)e.getSource();
        	int index = -1;
        	for(int i=0; i<m_fieldList.size(); i++){
        		if(focus.equals(m_fieldList.get(i))){
        				index = i;
        				break;
        		}
        	}
        	m_batchState.changeSelection(new Point(index, m_batchState.getSelected().y));
        }

        @Override
        public void focusLost(FocusEvent e) {
        	JTextField focus = (JTextField)e.getSource();
        	Point selection = m_batchState.getSelected();
        	selection.x = selection.x + 1; //increment the selected index by 1 before calling updateCell
        	m_batchState.updateCell(selection, focus.getText());
        	
        }
    };


    private MouseAdapter m_mouseListener = new MouseAdapter() {
    	
        public void mouseReleased(MouseEvent e){
            if (SwingUtilities.isRightMouseButton(e)){

            	int row = m_recordList.getSelectedIndex();
            	int col = -1;
            	//JTextField field = null;
            	JTextField field = null;
            	JTextField selField = (JTextField)e.getComponent();
            	for(int i=0; i< m_fieldList.size(); i++){
            		field = m_fieldList.get(i);
            		System.out.println(field.getY());
            		System.out.println(field.getHeight());
            		System.out.println(e.getPoint());
            		if(field == e.getComponent()){
            			col = i;
            			selField = field;
            		}
            	}
            	if(col == -1)
            		return;
            	
            	m_selCell = new Point(col + 1,row);
            	if(selField.getBackground().equals(Color.RED)){
            		doPop(e);
            	}
            }
        }

        private void doPop(MouseEvent e){
            RightClickMenu menu = new RightClickMenu();
            menu.show(e.getComponent(), e.getX(), e.getY());
        }
    };
    
    private Set<String> getSuggestions(int col, int row) { 
		return SuggestionUtil.generateEditDistanceTwo(m_batchState.getGrid()[row][col], 
											m_valueMap.get(col - 1));
	}
    
    class RightClickMenu extends JPopupMenu {
        JMenuItem anItem;
        public RightClickMenu(){
            anItem = new JMenuItem("See Suggestions");
            anItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					assert(m_selCell != null);
					Set<String> suggestions = getSuggestions(m_selCell.x, m_selCell.y);
					m_sugDialog.updateSuggestions(suggestions);
					if(m_sugDialog.showDialog(FormEntryPanel.this, "Suggestion")){
						JTextField field =  m_fieldList.get(m_selCell.x - 1 );
						field.setText(m_sugDialog.getResponse());
						m_batchState.updateCell(m_selCell, m_sugDialog.getResponse());
					}
					m_batchState.changeSelection(m_selCell);
				}

				
			});
            add(anItem);
        }
    }
    
    
    
    
}
