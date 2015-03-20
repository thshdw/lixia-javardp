/* RdesktopJFrame.java
 * Component: LixiaJavaRDP
 * 
 * Revision: $Revision: 1.0 $
 * Author: $Author: pengzhou $
 * Date: $Date: 2009/03/16  $
 *
 * Copyright (c) 2010 pengzhou
 *
 * Purpose: Container of drawing panel.
 */
package com.lixia.rdp;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.KeyboardFocusManager;
import java.awt.Label;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.apache.log4j.Logger;

import com.lixia.rdp.Options;


public class RdesktopJFrame extends javax.swing.JFrame {

	static Logger logger = Logger.getLogger(RdesktopJFrame.class);

	public RdesktopJPanel canvas = null;
	public RdpJPanel rdp = null;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RdesktopJFrame() {
		super();
		initGUI();
	}
	
	private void initGUI() {
		try {
			Common.frame = this;
			
			canvas = new RdesktopJPanel_Localised(Options.width, Options.height);
			this.setContentPane(canvas);
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			pack();
//			setSize(Options.width+8, Options.height+12);
//			setSize(Options.width, Options.height);
			
			if (Constants.OS != Constants.WINDOWS)
				setResizable(false);
			
			addWindowListener(new RdesktopWindowAdapter());
	        canvas.addFocusListener(new RdesktopFocusListener());
	        // key board focus manger
	        KeyboardFocusManager.setCurrentKeyboardFocusManager(null);
	        
	        if (Constants.OS == Constants.WINDOWS) {
				// redraws screen on window move
				addComponentListener(new RdesktopComponentAdapter());
			}

	        try{
		        URL url = RdesktopSwing.class.getResource("fron-ico.PNG");
		        if(url != null)
		        	this.setIconImage(Toolkit.getDefaultToolkit().getImage(url));
	        }
	        catch(Exception e){
	        	//nothing to do
	        }

            this.setTitle(Options.windowTitle);
            setLocationRelativeTo(null);
			canvas.requestFocusInWindow();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class OKDialog extends JDialog implements ActionListener {
		public OKDialog(JFrame parent, String title, String[] message) {

			super(parent, title, true);

			JPanel msg = new JPanel();
			msg.setLayout(new GridLayout(message.length, 1));
			for (int i = 0; i < message.length; i++)
				msg.add(new JLabel(message[i], JLabel.CENTER));
			this.add("Center", msg);

			JPanel p = new JPanel();
			p.setLayout(new FlowLayout());
			JButton ok = new JButton("OK");
			ok.addActionListener(this);
			p.add(ok);
			this.add("South", p);
			this.pack();

			if (getSize().width < 240)
				setSize(new Dimension(240, getSize().height));

//			centreWindow(this);
			
		}

		public void actionPerformed(ActionEvent e) {
			this.setVisible(false);
			this.dispose();
		}
	}

	public void showErrorDialog(String[] msg) {
		JDialog d = new OKDialog(this, "lixia-javardp: Error", msg);
		d.setVisible(true);
	}
	
	public void triggerReadyToSend() {
		// do not show out windows if seamless active. do it in ui_seamless_begin()
		if(!Options.no_loginProgress)
			this.setVisible(true);
		RdesktopJPanel pane = (RdesktopJPanel)this.getContentPane();
		pane.triggerReadyToSend();
	}
	
	public RdesktopJPanel getCanvas() {
		return this.canvas;
	}

	public void registerCommLayer(RdpJPanel rdp) {
		this.rdp = rdp;
		RdesktopJPanel pane = (RdesktopJPanel)this.getContentPane();
		pane.registerCommLayer(rdp);
	}

	class YesNoDialog extends Dialog implements ActionListener {

		JButton yes, no;

		boolean retry = false;

		public YesNoDialog(Frame parent, String title, String[] message) {
			super(parent, title, true);
			// Box msg = Box.createVerticalBox();
			// for(int i=0; i<message.length; i++) msg.add(new
			// Label(message[i],Label.CENTER));
			// this.add("Center",msg);
			JPanel msg = new JPanel();
			msg.setLayout(new GridLayout(message.length, 1));
			for (int i = 0; i < message.length; i++)
				msg.add(new Label(message[i], Label.CENTER));
			this.add("Center", msg);

			JPanel p = new JPanel();
			p.setLayout(new FlowLayout());
			yes = new JButton("Yes");
			yes.addActionListener(this);
			p.add(yes);
			no = new JButton("No");
			no.addActionListener(this);
			p.add(no);
			this.add("South", p);
			this.pack();
			if (getSize().width < 240)
				setSize(new Dimension(240, getSize().height));

			//centreWindow(this);
		}

		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == yes)
				retry = true;
			else
				retry = false;
			this.setVisible(false);
			this.dispose();
		}
	}

	public boolean showYesNoErrorDialog(String[] msg) {

		YesNoDialog d = new YesNoDialog(this, "lixia-javardp: Error", msg);
		d.setVisible(true);
		return d.retry;
	}
	
	private boolean menuVisible = false;
	
    /**
     * Hide the menu bar
     */
	public void hideMenu(){
		if(menuVisible && Options.enable_menu) this.setMenuBar(null);
		//canvas.setSize(this.WIDTH, this.HEIGHT);
		canvas.repaint();
		menuVisible = false;
	}

    class RdesktopFocusListener implements FocusListener {

        public void focusGained(FocusEvent arg0) {
            if (Constants.OS == Constants.WINDOWS) {
                // canvas.repaint();
                canvas.repaint(0, 0, Options.width, Options.height);
            }
            // gained focus..need to check state of locking keys
            canvas.gainedFocus();
        }

        public void focusLost(FocusEvent arg0) {
            //  lost focus - need clear keys that are down
            canvas.lostFocus();            
        }
    }

	class RdesktopWindowAdapter extends WindowAdapter {

		public void windowClosing(WindowEvent e) {
			setVisible(false);
			RdesktopSwing.exit(0, rdp, (RdesktopJFrame) e.getWindow(), true);
		}

		public void windowLostFocus(WindowEvent e) {
            logger.info("windowLostFocus");
			// lost focus - need clear keys that are down
			canvas.lostFocus();
		}

		public void windowDeiconified(WindowEvent e) {
			if (Constants.OS == Constants.WINDOWS) {
				// canvas.repaint();
				canvas.repaint(0, 0, Options.width, Options.height);
			}
			canvas.gainedFocus();
		}

		public void windowActivated(WindowEvent e) {
			if (Constants.OS == Constants.WINDOWS) {
				// canvas.repaint();
				canvas.repaint(0, 0, Options.width, Options.height);
			}
			// gained focus..need to check state of locking keys
			canvas.gainedFocus();
		}

		public void windowGainedFocus(WindowEvent e) {
			if (Constants.OS == Constants.WINDOWS) {
				// canvas.repaint();
				canvas.repaint(0, 0, Options.width, Options.height);
			}
			// gained focus..need to check state of locking keys
			canvas.gainedFocus();
		}
	}

	class RdesktopComponentAdapter extends ComponentAdapter {
		public void componentMoved(ComponentEvent e) {
			canvas.repaint(0, 0, Options.width, Options.height);
		}
	}

}
