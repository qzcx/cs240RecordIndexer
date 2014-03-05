package client.gui.util;

import java.awt.GridBagConstraints;

public class GridBagUtil {

	/**
	 * Sets GridBagConstaints
	 * Defaults width to 1
	 * @param c
	 * @param y
	 * @param x
	 * @return
	 */
	public static GridBagConstraints setGrid(GridBagConstraints c, 
			int y, int x){
		
		return setGrid(c,y,x,1);
	}
	
	/**
	 * Basic set GridBagConstaints function.
	 * @param c
	 * @param x
	 * @param y
	 * @param width
	 * @return
	 */
	public static GridBagConstraints setGrid(GridBagConstraints c, 
			int y, int x, int width){
		c.gridy = y;
		c.gridx = x;
		c.gridwidth = width;
		
		return c;
	}
	
}
