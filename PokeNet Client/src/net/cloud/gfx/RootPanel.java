package net.cloud.gfx;

import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Optional;
import java.util.function.Consumer;

import javax.swing.JPanel;

import net.cloud.gfx.focus.FocusController;
import net.cloud.gfx.handlers.KeyEventHandler;
import net.cloud.gfx.handlers.MouseEventHandler;
import net.cloud.gfx.interfaces.QuasiRoot;

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
	private QuasiRoot elementRoot;
	
	/** Handler that will deal with clicks on elements */
	private MouseEventHandler mouseEventHandler;
	
	/** Handler that will deal with typing keys and sending the event to elements */
	private KeyEventHandler keyEventHandler;
	
	/** A decoupled function to draw the game graphics to the screen */
	private Optional<Consumer<Graphics>> drawFunction;
	
	/**
	 * Create a new RootPanel so that a game's graphics can be contained in an application. 
	 * @param width The height the panel will be (as well as the root interface)
	 * @param height The width the panel will be (as well as the root interface)
	 */
	public RootPanel(int width, int height)
	{
		super();
		
		this.drawFunction = Optional.empty();
		
		Dimension size = new Dimension(width, height);
		super.setPreferredSize(size);
		super.setMinimumSize(size);
		super.setSize(size);
		
		// Create the root interface
		elementRoot = new QuasiRoot(width, height);
		
		// Set up the key and mouse listeners
		attachListeners();
		
		// Focus should go here, but we don't wanna consume tab events. Start with the focus.
		super.setFocusable(true);
		super.setFocusTraversalKeysEnabled(false);
		super.requestFocusInWindow();
		
		// And similarly, for the element hierarchy, the quasi-root should get focus first
		FocusController.instance().register(elementRoot);
	}
	
	/**
	 * Draws the element hierarchy. 
	 */
	@Override
	public void paintComponent(Graphics g)
	{
		// Honor a call to the super method
		super.paintComponent(g);
		
		// Have the game graphics drawn
		drawFunction.ifPresent((func) -> func.accept(g));
	}
	
	/**
	 * Tell the root panel that the given consumer function will be responsible for drawing the game graphics
	 * @param drawFunction The function that will draw game graphics
	 */
	public void setDrawFunction(Consumer<Graphics> drawFunction)
	{
		this.drawFunction = Optional.of(drawFunction);
	}
	
	/**
	 * @return The root of the CloudGfx element hierarchy
	 */
	public QuasiRoot getQuasiRoot()
	{
		return elementRoot;
	}
	
	/**
	 * @return The handler taking care of mouse events
	 */
	public MouseEventHandler getMouseEventHandler()
	{
		return mouseEventHandler;
	}

	/**
	 * @return The handler taking care of key events
	 */
	public KeyEventHandler getKeyEventHandler()
	{
		return keyEventHandler;
	}
	
	/**
	 * Create and add listeners to deal with key and mouse events 
	 * on this panel, so that the Element hierarchy will get them. 
	 */
	private void attachListeners()
	{
		mouseEventHandler = new MouseEventHandler(this, elementRoot);
		this.addMouseListener(mouseEventHandler);
		this.addMouseWheelListener(mouseEventHandler);
		
		keyEventHandler = new KeyEventHandler();
		this.addKeyListener(keyEventHandler);
	}

}
