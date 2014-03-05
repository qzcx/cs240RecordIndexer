package client.states;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import client.states.BatchState.BatchListener;

public class ImageState {

	private float m_zoomLevel;
	private Point m_imageLoc;
	private boolean m_highlight;
	private boolean m_invert;
	private boolean m_saved;
	
	private transient List<ImageListener> listeners;
	
	public ImageState(){
		listeners = new ArrayList<ImageListener>();
		m_zoomLevel = 1;
		m_imageLoc = new Point(0,0);
		m_highlight = true;
		m_invert = false;
		m_saved = false;
	}
	
	public boolean isSaved() {
		return m_saved;
	}

	public void save() {
		this.m_saved = true;
	}

	public void addListener(ImageListener l){
		listeners.add(l);
	}
	
	public void removeAllListener(){
		if(listeners != null){
			listeners.clear();
		}else{
			listeners = new ArrayList<ImageListener>();
		}
	}
	
	public float getZoomLevel() {
		return m_zoomLevel;
	}

	public Point getImageLoc() {
		return m_imageLoc;
	}

	public boolean isHighlight() {
		return m_highlight;
	}

	public boolean isInvert() {
		return m_invert;
	}

	public List<ImageListener> getListeners() {
		return listeners;
	}

	public void zoomIn(){
		setZoom(m_zoomLevel*2);
	}
	
	public void zoomOut(){
		setZoom(m_zoomLevel/2);
	}
	
	public void setZoom(float zoomLevel){
		m_zoomLevel = zoomLevel;
		for(ImageListener l: listeners){
			l.zoomChanged(zoomLevel);
		}
	}
	
	public void setImageLoc(Point coord){
		m_imageLoc = coord;
		for(ImageListener l: listeners){
			l.setImageLoc(coord);
		}
	}
	
	public void toggleHighlight(){
		m_highlight = !m_highlight;
		for(ImageListener l: listeners){
			l.toggleHighlight();
		}
	}
	
	public void setHighlight(boolean highlight){
		m_highlight = highlight;
	}
	
	public void invert(){
		m_invert = !m_invert;
		for(ImageListener l: listeners){
			l.invert();
		}
	}
	
	public void setInvert(boolean invert){
		m_invert = invert;
	}
	
	public interface ImageListener{
		
		public void zoomChanged(float zoomLevel);

		public void setImageLoc(Point coord);
		
		public void toggleHighlight();
		
		public void invert();
		
	}
}
