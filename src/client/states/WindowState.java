package client.states;

import java.awt.Point;

public class WindowState {

	private Point m_windowPos;
	private int m_windowHeight;
	private int m_windowWidth;
	private int m_centerDivide;
	private int m_bottomDivide;
	
	public WindowState(){
		m_windowPos = new Point(-1,-1);
	}
	
	public Point getM_windowPos() {
		return m_windowPos;
	}
	public void setM_windowPos(Point m_windowPos) {
		this.m_windowPos = m_windowPos;
	}
	public int getM_windowHeight() {
		return m_windowHeight;
	}
	public void setM_windowHeight(int m_windowHeight) {
		this.m_windowHeight = m_windowHeight;
	}
	public int getM_windowWidth() {
		return m_windowWidth;
	}
	public void setM_windowWidth(int m_windowWidth) {
		this.m_windowWidth = m_windowWidth;
	}
	public int getM_centerDivide() {
		return m_centerDivide;
	}
	public void setM_centerDivide(int m_centerDivide) {
		this.m_centerDivide = m_centerDivide;
	}
	public int getM_bottomDivide() {
		return m_bottomDivide;
	}
	public void setM_bottomDivide(int m_bottomDivide) {
		this.m_bottomDivide = m_bottomDivide;
	}
	
	
	
	
}
