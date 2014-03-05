package client.gui.components;

import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;

import javax.imageio.*;
import javax.swing.*;

import java.io.*;

import shared.comm.Download_Results;
import shared.model.Field;
import client.clientComm.ClientComm;
import client.states.BatchState;
import client.states.ImageState;

@SuppressWarnings("serial")
public class ImageComp extends JComponent {

	private ImageState m_imageState;
	private BatchState m_batchState;
	private ClientComm m_comm;
	
	
	private DrawingImage m_batchImage;
	private DrawingRect m_selectedRect;
	
	private static BufferedImage NULL_IMAGE = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
	
	private int m_imageWidth;
	private int m_imageHeight;
	
	private boolean dragging;
	private int w_dragStartX;
	private int w_dragStartY;
	private int w_dragStartOriginX;
	private int w_dragStartOriginY;
	
	private static final short[] invertTable;
	static {
		invertTable = new short[256];
			for (int i = 0; i < 256; i++) {		
				invertTable[i] = (short) (255 - i);		
			}		
		}
	
	public ImageComp(ImageState imageState, BatchState batchState, ClientComm comm){
		m_comm = comm;
		m_imageState = imageState;
		m_imageState.addListener(new CompImageListener());
		m_batchState = batchState;
		m_batchState.addListener(new ImageBatchListener());
		
		m_batchImage = null;
		
		this.addMouseListener(mouseAdapter);
		this.addMouseMotionListener(mouseAdapter);
		this.addMouseWheelListener(mouseAdapter);
		
		setBackground(Color.DARK_GRAY);
		
		initDrag();
	}
	
	public void removeImage(){
		m_batchImage = null;
		m_selectedRect = null;
		repaint();
	}
	
	
	private void initDrag() {
		dragging = false;
		w_dragStartX = 0;
		w_dragStartY = 0;
		w_dragStartOriginX = 0;
		w_dragStartOriginY = 0;
	}
	
	@Override
	protected void paintComponent(Graphics g) {

		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D)g;
		drawBackground(g2);

		//Don't do anything if there is no image.
		if(m_batchImage != null){
			//these statements work in reverse.
			g2.translate(getWidth()/2 ,getHeight()/2);
			g2.scale(m_imageState.getZoomLevel(), m_imageState.getZoomLevel()); //scale
			g2.translate(-m_imageState.getImageLoc().x, -m_imageState.getImageLoc().y); 
			drawShapes(g2);
		}
	}
	
	private void drawBackground(Graphics2D g2) {
		g2.setColor(getBackground());
		g2.fillRect(0,  0, getWidth(), getHeight());
	}

	private void drawShapes(Graphics2D g2) {
		if(m_batchImage != null)
			m_batchImage.draw(g2);
		if(m_selectedRect != null && m_imageState.isHighlight())
			m_selectedRect.draw(g2);
	}
	
	private BufferedImage loadImage(File imageFile) {
		try {
			return ImageIO.read(imageFile);
		}
		catch (IOException e) {
			return NULL_IMAGE;
		}
	}
	
	
	private class CompImageListener implements ImageState.ImageListener{

		@Override
		public void zoomChanged(float zoomLevel) {
			ImageComp.this.repaint();
		}

		@Override
		public void setImageLoc(Point coord) {
			ImageComp.this.repaint();
			
		}

		@Override
		public void toggleHighlight() {
			repaint();
		}

		@Override
		public void invert() {
			m_batchImage.invertImage();
			repaint();
		}
		
	}
	
	private class ImageBatchListener implements BatchState.BatchListener{

		@Override
		public void updateCell(Point cell, String value) {
			//This should do nothing to the imagePanel
			return;			
		}

		@Override
		public void changeSelection(Point cell) {
			Download_Results batchInfo = m_batchState.getBatchInfo();
			//selectionRect
			int y1 = batchInfo.getFirstYCoord() + batchInfo.getRecordHeight() * cell.y;
			int height = batchInfo.getRecordHeight(); 
			int x1 = batchInfo.getFields().get(cell.x).getxCoord();
			int width = batchInfo.getFields().get(cell.x).getWidth();
			//divide by two since we scaled the original image by 2.
			m_selectedRect = new DrawingRect(new Rectangle(x1/2, y1/2, width/2, height/2), new Color(0, 0, 200, 128));
			
			ImageComp.this.repaint(); 
			
		}

		@Override
		public void loadBatch(Download_Results batchInfo, Map<Integer, Set<String>> knownValueMap) {
			String imageUrl = batchInfo.getImageURL();
			String imagePath = imageUrl.substring(imageUrl.indexOf('/',7));
			File image = m_comm.downloadURL(imagePath);
			BufferedImage newImage = loadImage(image);
			m_batchImage = new DrawingImage(newImage, new Rectangle2D.Double(0, 0, newImage.getWidth(null)/2, newImage.getHeight(null)/2));
			
			m_imageWidth = (int) m_batchImage.rect.getWidth();
			m_imageHeight = (int) m_batchImage.rect.getHeight();
			if(!m_imageState.isSaved())
				m_imageState.setImageLoc(new Point(m_imageWidth/2 ,m_imageHeight/2));
			
			//selectionRect
			int y1 = batchInfo.getFirstYCoord();
			int height = batchInfo.getRecordHeight(); 
			int x1 = batchInfo.getFields().get(0).getxCoord();
			int width = batchInfo.getFields().get(0).getWidth();
			//divide by two since we scaled the original image by 2.
			m_selectedRect = new DrawingRect(new Rectangle(x1/2, y1/2, width/2, height/2), new Color(0, 0, 200, 128));
			ImageComp.this.repaint(); 
		}
		
	}
	
	private MouseAdapter mouseAdapter = new MouseAdapter() {

		@Override
		public void mousePressed(MouseEvent e) {
			int d_X = e.getX();
			int d_Y = e.getY();
			
			AffineTransform transform = new AffineTransform();

			transform.translate(getWidth()/2 ,getHeight()/2);
			transform.scale(m_imageState.getZoomLevel(), m_imageState.getZoomLevel()); //scale
			transform.translate(-m_imageState.getImageLoc().x, -m_imageState.getImageLoc().y); 
			
			Point2D d_Pt = new Point2D.Double(d_X, d_Y);
			Point2D w_Pt = new Point2D.Double();
			try
			{
				transform.inverseTransform(d_Pt, w_Pt);
			}
			catch (NoninvertibleTransformException ex) {
				return;
			}
			int w_X = (int)w_Pt.getX();
			int w_Y = (int)w_Pt.getY();
			
			boolean hitShape = false;
			
			Graphics2D g2 = (Graphics2D)getGraphics();
			if (m_batchImage != null && m_batchImage.contains(g2, w_X, w_Y)) {
				hitShape = true;
				cellHitTest(w_X, w_Y);
			}
			
			if (hitShape) {
				dragging = true;		
				w_dragStartX = w_X;
				w_dragStartY = w_Y;	
				w_dragStartOriginX = m_imageState.getImageLoc().x;
				w_dragStartOriginY = m_imageState.getImageLoc().y;
			}
		}


		private void cellHitTest(int x, int y) {
			int record = -1;
			int field = -1;
			
			Download_Results batchInfo = m_batchState.getBatchInfo();
			//find the field x position the point hits.
			List<Field> fields = batchInfo.getFields();
			for(int i=0; i<fields.size(); i++){
				Field f = fields.get(i);
				if(f.getxCoord() < x*2 && f.getxCoord() + f.getWidth()> x*2){
					field = i;
					break;
				}
			}
			//if none of the fields x positions were hit, return
			if(field == -1)
				return;
			//find the record hit
			int topOfRow = batchInfo.getFirstYCoord();
			for(int i=0; i < batchInfo.getNumRecords(); i++){
				if(topOfRow + i*batchInfo.getRecordHeight()< y*2 &&
					topOfRow + (i+1)*batchInfo.getRecordHeight() > y*2){
					record = i;
					break;
				}
			}
			//if no record y coord was hit, return
			if(record == -1)
				return;
			
			m_batchState.changeSelection(new Point(field,record));
		}

		
		@Override
		public void mouseDragged(MouseEvent e) {		
			if (dragging) {
				int d_X = e.getX();
				int d_Y = e.getY();
				
				AffineTransform transform = new AffineTransform();
				transform.translate(getWidth()/2 ,getHeight()/2);
				transform.scale(m_imageState.getZoomLevel(), m_imageState.getZoomLevel()); //scale
				transform.translate(-w_dragStartOriginX, -w_dragStartOriginY); 
				
				Point2D d_Pt = new Point2D.Double(d_X, d_Y);
				Point2D w_Pt = new Point2D.Double();
				try
				{
					transform.inverseTransform(d_Pt, w_Pt);
				}
				catch (NoninvertibleTransformException ex) {
					return;
				}
				int w_X = (int)w_Pt.getX();
				int w_Y = (int)w_Pt.getY();
				
				int w_deltaX = w_X - w_dragStartX;
				int w_deltaY = w_Y - w_dragStartY;
				
				m_imageState.setImageLoc(new Point(w_dragStartOriginX - w_deltaX,w_dragStartOriginY - w_deltaY));
				
				//notifyOriginChanged(m_imageState.getImageLoc().x, m_imageState.getImageLoc().y);
				
				repaint();
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			initDrag();
		}

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			int scrollValue = e.getUnitsToScroll();
			if(scrollValue < 0)
				m_imageState.zoomIn();
			else{
				m_imageState.zoomOut();
			}
			
			return;
		}	
	};
	
	
	
	interface DrawingShape {
		boolean contains(Graphics2D g2, double x, double y);
		void draw(Graphics2D g2);
		Rectangle2D getBounds(Graphics2D g2);
	}


	class DrawingRect implements DrawingShape {

		private Rectangle2D rect;
		private Color color;
		
		public DrawingRect(Rectangle2D rect, Color color) {
			this.rect = rect;
			this.color = color;
		}

		@Override
		public boolean contains(Graphics2D g2, double x, double y) {
			return rect.contains(x, y);
		}

		@Override
		public void draw(Graphics2D g2) {
			g2.setColor(color);
			g2.fill(rect);
		}
		
		@Override
		public Rectangle2D getBounds(Graphics2D g2) {
			return rect.getBounds2D();
		}
	}
	
	class DrawingImage implements DrawingShape {

		private BufferedImage image;
		private Rectangle2D rect;
		
		public DrawingImage(BufferedImage image, Rectangle2D rect) {
			this.image = image;
			this.rect = rect;
		}

		public void invertImage(){
			final BufferedImageOp invertOp = new LookupOp(new ShortLookupTable(0, invertTable), null);
			invertOp.filter(image,image);
		}
		
		
		@Override
		public boolean contains(Graphics2D g2, double x, double y) {
			return rect.contains(x, y);
		}
		
		@Override
		public void draw(Graphics2D g2) {
			//BufferedImage b = new BufferedImage(rect.getX(),rect.getY(), BufferedImage.TYPE_INT_RGB);
			g2.drawImage(image, (int)rect.getMinX(), (int)rect.getMinY(), (int)rect.getMaxX(), (int)rect.getMaxY(),
							0, 0, image.getWidth(null), image.getHeight(null), null);
		}	
		
		@Override
		public Rectangle2D getBounds(Graphics2D g2) {
			return rect.getBounds2D();
		}
	}
	
	
}
