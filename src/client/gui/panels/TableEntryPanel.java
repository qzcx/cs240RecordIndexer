package client.gui.panels;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import shared.comm.Download_Results;
import shared.model.Field;
import client.gui.dialogs.SuggestionDialog;
import client.spell.SuggestionUtil;
import client.states.BatchState;

@SuppressWarnings("serial")
public class TableEntryPanel  extends JPanel{

	private BatchState m_batchState;
	private JTable m_table;
	private MyTableModel m_tableModel;
	private String[] m_columnNames;
	
	private SuggestionDialog m_sugDialog;
	private Point m_selCell;

	private Map<Integer, Set<String>> m_valueMap;
	
	
	
	public TableEntryPanel(BatchState batchState) {
		m_sugDialog = new SuggestionDialog();
		
		m_batchState = batchState;
		m_batchState.addListener(new TableBatchListener());
		m_table = new JTable();
		JScrollPane scrollPane = new JScrollPane(m_table);
		
		//table parameters
		m_table.setFillsViewportHeight(true);
		m_table.setColumnSelectionAllowed(false);
		m_table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		m_table.getTableHeader().setReorderingAllowed(false);
		m_table.setCellSelectionEnabled(true);
		m_table.setPreferredSize(new Dimension(200, 100));
		
		//Set row and column listeners
		m_table.getSelectionModel().addListSelectionListener(new RowListener());
        m_table.getColumnModel().getSelectionModel().
        				addListSelectionListener(new ColumnListener());
        m_table.addMouseListener(m_mouseListener);
        
        add(scrollPane);
		this.setVisible(false); 
	}

	public void hideTable(){
		this.setVisible(false); 
	}
	
	
	private class TableBatchListener implements BatchState.BatchListener{

		@Override
		public void updateCell(Point cell, String value) {
			m_tableModel.fireTableCellUpdated(cell.y, cell.x);
			
		}

		@Override
		public void changeSelection(Point cell) {
			m_table.changeSelection(cell.y, cell.x + 1, false, false);
			m_table.requestFocus();
		}

		@Override
		public void loadBatch(Download_Results batchInfo, Map<Integer, Set<String>> knownValueMap) {
			m_valueMap = knownValueMap;
			
			List<Field> fields = batchInfo.getFields();
			
			int width = fields.size();
			
			m_columnNames = new String[width + 1];
			m_columnNames[0] = "Record Number";
			for(int i=0; i<fields.size(); i++){
				m_columnNames[i + 1] = fields.get(i).getTitle();
			}
			
			m_tableModel = new MyTableModel();
			m_table.setModel(m_tableModel);
			
			TableEntryPanel.this.setVisible(true);
			
			TableColumnModel columnModel = m_table.getColumnModel();
			for (int i = 0; i < m_tableModel.getColumnCount(); ++i) {
				TableColumn column = columnModel.getColumn(i);
				if(i > 0){
					column.setCellRenderer(new ColorCellRenderer());
				}
				column.setPreferredWidth(150);
			}
		}
		
		
	}
	
	class MyTableModel extends AbstractTableModel{
	    public String getColumnName(int col) {
	        return m_columnNames[col].toString();
	    }
	    public int getRowCount() { return m_batchState.getGrid().length; }
	    public int getColumnCount() { return m_columnNames.length; }
	    public Object getValueAt(int row, int col) {
	        return m_batchState.getGrid()[row][col];
	    }
	    public boolean isCellEditable(int row, int col)
	        { return col > 0 ? true: false; }
	    public void setValueAt(Object value, int row, int col) {
	    	m_batchState.updateCell(new Point(col, row), value.toString());
	    	
	        
	    }
	}
	
	class ColorCellRenderer extends JLabel implements TableCellRenderer {

		private Border unselectedBorder = BorderFactory.createLineBorder(Color.BLACK, 1);
		private Border selectedBorder = BorderFactory.createLineBorder(Color.BLACK, 2);

		public ColorCellRenderer() {
			
			setOpaque(true);
			setFont(getFont().deriveFont(16.0f));
		}

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			
			//see if the value in the cell is in the value map
			//value map cannot be empty  and the value cannot be blank
			
			if(column > 0 && m_valueMap != null && value != null && m_valueMap.size() >0 && !value.equals("")){
				if(m_valueMap.get(column - 1) != null && m_valueMap.get(column - 1).size() > 0 && !m_valueMap.get(column - 1).contains(value.toString().toUpperCase()) ){
					this.setBackground(Color.RED);
					//System.out.println(m_valueMap.get(column -1));
		    	}else{
		    		this.setBackground(Color.WHITE);
		    	}
			}else{
				this.setBackground(Color.WHITE);
			}
			
			if (isSelected) {
				this.setBorder(selectedBorder);
			}
			else {
				this.setBorder(unselectedBorder);
			}
			
			this.setText((String)value);
			
			return this;
		}

	}
	
	
	 private class RowListener implements ListSelectionListener {
	        public void valueChanged(ListSelectionEvent event) {
	            if (event.getValueIsAdjusting()) {
	                return;
	            }
	            int column = m_table.getSelectedColumn() -1> 0 ? m_table.getSelectedColumn()-1 : 0;
	            int row = m_table.getSelectedRow() > 0 ? m_table.getSelectedRow()  : 0;
	            Point curSelection = new Point(column, row);
	            //System.out.println(curSelection);
	            if(!m_batchState.getSelected().equals(curSelection))
	            	m_batchState.changeSelection(curSelection);
	        }
	    }

	    private class ColumnListener implements ListSelectionListener {
	        public void valueChanged(ListSelectionEvent event) {
	            if (event.getValueIsAdjusting()) {
	                return;
	            }
	            int column = m_table.getSelectedColumn() - 1 > 0 ? m_table.getSelectedColumn() - 1 : 0;
	            int row = m_table.getSelectedRow() > 0 ? m_table.getSelectedRow()  : 0;
	            Point curSelection = new Point(column, row);
	            //System.out.println(curSelection);
	            if(!m_batchState.getSelected().equals(curSelection))
	            	m_batchState.changeSelection(curSelection);
	        }
	    }
	    
	    private MouseAdapter m_mouseListener = new MouseAdapter() {
	    	
	        public void mouseReleased(MouseEvent e){
	            if (SwingUtilities.isRightMouseButton(e)){
	            	int row = m_table.rowAtPoint(e.getPoint());
	                int col = m_table.columnAtPoint(e.getPoint());
	                m_selCell = new Point(col,row);
	                TableCellRenderer renderer = m_table.getCellRenderer(row, col);
	                Component cell = m_table.prepareRenderer(renderer, row, col);
	            	if(cell.getBackground().equals(Color.RED)){
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
						if(m_sugDialog.showDialog(TableEntryPanel.this, "Suggestion")){
							m_batchState.updateCell(m_selCell, m_sugDialog.getResponse());
						}
						m_batchState.changeSelection(m_selCell);
					}

					
				});
	            add(anItem);
	        }
	    }
	    
	    
}
