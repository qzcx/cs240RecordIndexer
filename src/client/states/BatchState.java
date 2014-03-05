package client.states;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import shared.comm.Download_Results;
import shared.model.Field;

public class BatchState {

	private String[][] m_grid;
	private Point m_selected;
	private Download_Results m_batchInfo;
	
	private transient List<BatchListener> listeners;
	
	public BatchState(){
		listeners = new ArrayList<BatchListener>();
		m_selected = new Point(0,0);
	}
	
	public void setBatchInfo(Download_Results batchInfo, Map<Integer, Set<String>> knownValueMap){
		m_batchInfo = batchInfo;
		intializeGrid();
		loadBatch(knownValueMap);
	}
	
	public void loadBatch(Map<Integer, Set<String>>knownValueMap){
		if(m_batchInfo != null){
			for(BatchListener l : listeners){
				l.loadBatch(m_batchInfo, knownValueMap);
			}
		}
	}
	
	private void intializeGrid() {
		int width = m_batchInfo.getFields().size();
		int height = m_batchInfo.getNumRecords();
		m_grid = new String[height][width + 1];
		for(int h=0; h< height; h++ ){
			m_grid[h][0] = "" + h;
		}
	}

	public void addListener(BatchListener listener){
		listeners.add(listener);
	}
	
	public void removeAllListener(){
		if(listeners != null){
			listeners.clear();
		}else{
			listeners = new ArrayList<BatchListener>();
		}
		
	}
	
	public void updateCell(Point cell, String value){
		if(m_grid == null){
			System.err.println("Trying to write to grid, but grid is not yet intialized");
			return;
		}
		
		m_grid[cell.y][cell.x] = value;
		for(BatchListener l : listeners){
			l.updateCell(cell, value);
		}
	}
	
	public void changeSelection(Point cell){
		m_selected = cell;
		for(BatchListener l : listeners){
			l.changeSelection(cell);
		}
	}
	
	public String[][] getGrid() {
		return m_grid;
	}

	public Point getSelected() {
		return m_selected;
	}

	public Download_Results getBatchInfo() {
		return m_batchInfo;
	}



	@Override
	public String toString() {
		return "BatchState [m_grid=" + Arrays.toString(m_grid)
				+ ", m_selected=" + m_selected + ", m_batchInfo=" + m_batchInfo
				+ ", listeners=" + listeners + "]";
	}



	public interface BatchListener{
		
		public void updateCell(Point cell, String value);
		
		public void changeSelection(Point cell);
		
		
		public void loadBatch(Download_Results batchInfo, Map<Integer, Set<String>> knownValueMap);
		
	}
	
}
