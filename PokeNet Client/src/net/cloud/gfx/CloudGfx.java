package net.cloud.gfx;

import net.cloud.client.event.shutdown.ShutdownHook;
import net.cloud.client.event.shutdown.ShutdownService;
import net.cloud.client.event.shutdown.hooks.GraphicShutdownHook;
import net.cloud.gfx.sprites.SpriteManager;

/**
 * A facade of sorts to the.. erm.. CloudGfx system. 
 * Contains the underlying objects necessary for the system, including the 
 * thread that drawing is done on, the panel containing the UI, and the shutdown hook 
 * to stop the thing. 
 */
public class CloudGfx implements ShutdownService {
	
	/** The JPanel that will draw the graphics hierarchy */
	private RootPanel rootPanel;
	
	/** The thread that the draw logic will be running on */
	private Thread drawThread;
	
	/** The object that will make drawing happen [regularly] */
	private DrawLogic drawLogic;
	
	/** The hook that will stop drawing activity */
	private ShutdownHook shutdownHook;
	
	/**
	 * Create a new graphics hierarchy, which can be embedded in an application. 
	 * This will act as a facade and central access point. The graphics will initially be 
	 * the desired dimensions. <br>
	 * Drawing will not start after creation, a call to <code>startDrawing()</code> is needed.
	 * @param width The width of the panel to create
	 * @param height The height of the panel to create
	 */
	public CloudGfx(int width, int height)
	{
		// Grab the SpriteManager, which will start it loading
		SpriteManager.instance();
		
		// Initialize the panel
		rootPanel = new RootPanel(width, height);
		
		// Get a thread up and ready to draw
		drawLogic = new DrawLogic(rootPanel);
		drawThread = new Thread(drawLogic);
		
		// Now we've got the necessary objects to create the hook
		shutdownHook = new GraphicShutdownHook(drawLogic, drawThread);
	}

	/**
	 * Obtain a shutdown hook which can be used to stop the graphics from being redrawn. 
	 * Available after construction. 
	 * @return The hook to stop the graphics loop
	 */
	@Override
	public ShutdownHook getShutdownHook() throws NullPointerException {
		return shutdownHook;
	}
	
	/**
	 * @return The RootPanel storing the game graphics
	 */
	public RootPanel rootPanel()
	{
		return rootPanel;
	}
	
	/**
	 * Start the graphics being drawn. This will start the drawing thread and 
	 * it will loop until stopped via the shutdown hook.
	 */
	public void startDrawing()
	{
		drawThread.start();
	}

}
