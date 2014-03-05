package client.gui.components;

import java.awt.Point;

import javax.swing.JComponent;

import client.states.ImageState;

@SuppressWarnings("serial")
public class ImageNavComp extends JComponent{

	private ImageState m_imageState;
	
	public ImageNavComp(ImageState imageState) {
		m_imageState = imageState;
		m_imageState.addListener(new NavImageListener());
	}

	private class NavImageListener implements ImageState.ImageListener{

		@Override
		public void zoomChanged(float zoomLevel) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setImageLoc(Point coord) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void toggleHighlight() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void invert() {
			
		}
		
	}
	
}
