/* RdesktopFrame_Localised.java
 * Component: ProperJavaRDP
 * 
 * Revision: $Revision: 1.1.1.1 $
 * Author: $Author: suvarov $
 * Date: $Date: 2007/03/08 00:26:54 $
 *
 * Copyright (c) 2005 Propero Limited
 *
 * Purpose: Java 1.4 specific extension of RdesktopFrame class
 */
// Created on 07-Jul-2003

package com.lixia.rdp;
import java.awt.*;

public class RdesktopJFrame_Localised extends RdesktopJFrame {
	public RdesktopJFrame_Localised(){
		super();
	}
	protected void fullscreen(){
			setUndecorated (true);
			setExtendedState (Frame.MAXIMIZED_BOTH);
	}
	
	public void goFullScreen(){
		if(!Options.fullscreen) return;
		
		if(this.isDisplayable()) this.dispose();
		this.setVisible(false);
		this.setLocation(0, 0);
		this.setUndecorated(true);
		this.setVisible(true);
		
		this.pack();
	}
	
	public void leaveFullScreen() {
		if(!Options.fullscreen) return;
		
		if(this.isDisplayable()) this.dispose();
		
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice myDevice = env.getDefaultScreenDevice();
		if (myDevice.isFullScreenSupported()) myDevice.setFullScreenWindow(null);
		
		this.setLocation(10, 10);
		this.setUndecorated(false);
		this.setVisible(true);
		this.pack();
	}
}
