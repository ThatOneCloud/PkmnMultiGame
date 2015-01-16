package net.cloud.gfx;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import net.cloud.gfx.elements.Interface;
import net.cloud.mmo.logging.Logger;
import net.cloud.mmo.util.IteratorException;

/**
 * The root of the graphics package. The game's graphics will be housed 
 * in this JPanel. This panel is also the origin of the mouse and key listeners 
 * that will send events further down the element hierarchy. This panel will have 
 * an Interface element serving as a "quasi-root" - the root of the element hierarchy 
 * itself.  This JPanel can be added to ideally any Swing UI to use game graphics.
 */
public class RootPanel extends JPanel {

	/** Eclipse generated serial ID */
	private static final long serialVersionUID = 2890046422728587799L;
	
	/** The quasi-root - i.e. the start of the Element hierarchy */
	private Interface elementRoot;
	
	/**
	 * Create a new RootPanel so that a game's graphics can be contained in an application. 
	 * @param width The height the panel will be (as well as the root interface)
	 * @param height The width the panel will be (as well as the root interface)
	 */
	public RootPanel(int width, int height)
	{
		super();
		
		Dimension size = new Dimension(width, height);
		super.setPreferredSize(size);
		super.setMinimumSize(size);
		super.setSize(size);
		
		// Create the root interface
		elementRoot = new QuasiRoot(width, height);
		
		// Set up the key and mouse listeners
		attachListeners();
		
		// Focus should now go here first so key events get passed on
		super.setFocusable(true);
		super.requestFocusInWindow();
	}
	
	public void paintComponent(Graphics g)
	{
		// Honor a call to the super method
		super.paintComponent(g);
		
		// Try to paint the entire element hierarchy
		try {
			elementRoot.drawElement(g, 0, 0);
		} catch (IteratorException e) {
			// Darn, an unsafe change happened to the hierarchy during drawing. Try again. 
			// Note that this may result in a lot of redrawings. May need to be fine-tuned.
			Logger.writer().println("[NOTICE] Iteration exception while drawing");
			Logger.writer().flush();
			paintComponent(g);
		}
	}
	
	/**
	 * Create and add listeners to deal with key and mouse events 
	 * on this panel, so that the Element hierarchy will get them. 
	 */
	private void attachListeners()
	{
		MouseEventHandler mouse = new MouseEventHandler(this, elementRoot);
		this.addMouseListener(mouse);
		
		KeyEventHandler key = new KeyEventHandler(elementRoot);
		this.addKeyListener(key);
	}

}
