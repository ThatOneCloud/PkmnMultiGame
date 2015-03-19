package net.cloud.gfx;

import javax.swing.JFrame;

import net.cloud.gfx.handlers.WindowEventHandler;
import net.cloud.gfx.interfaces.QuasiRoot;

/**
 * The application frame.  Within this frame is the panel containing the game graphics. 
 * It is a singleton class - as there is only one such frame. It serves as an access point 
 * to just about everything in the graphics system, through one way or another. 
 */
public class Mainframe extends JFrame {

	/** Eclipse generated serial ID */
	private static final long serialVersionUID = -4278003640955365389L;
	
	/** The width of the game graphics */
	public static final int WIDTH = 800;
	
	/** The height of the game graphics */
	public static final int HEIGHT = 600;
	
	/** Singleton instance of the class */
	private static Mainframe instance;
	
	/** The entry point to the game graphics */
	private CloudGfx cloudGfx;
	
	/** Default private constructor for singleton pattern */
	private Mainframe()
	{
		super("PokeNet Client");
		
		// Initialize the frame itself
		initFrame();
		
		// Initialize the game graphics
		initGraphics();
		
		// Get everything put together
		putItAllTogether();
		
		// Get the graphics going
		cloudGfx.startDrawing();
	}
	
	/**
	 * Obtain the singleton instance to the Mainframe object. 
	 * Can be used to access all sorts of graphics stuff. 
	 * @return The singleton instance to the Mainframe
	 */
	public static Mainframe instance()
	{
		if(instance == null)
		{
			synchronized(Mainframe.class)
			{
				if(instance == null)
				{
					instance = new Mainframe();
				}
			}
		}
		
		return instance;
	}
	
	/**
	 * Way shorter access to the QuasiRoot, because yeesh would that get old. <br>
	 * Equivalent to <code>instance().gfx().rootPanel().getQuasiRoot()</code>
	 * @return The graphic element root
	 */
	public static QuasiRoot root()
	{
		return instance().gfx().rootPanel().getQuasiRoot();
	}
	
	/**
	 * @return The CloudGfx object holding all of the game graphics stuff
	 */
	public CloudGfx gfx()
	{
		return cloudGfx;
	}
	
	/**
	 * Initialize the application JFrame, get it up and showing. 
	 * It will not yet contain the game graphics.
	 */
	private void initFrame()
	{
		// Set the size of the frame. At least, what it'll hopefully come out to be. Darn layouts.
//		super.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		
		// Make closing it attempt graceful shutdown
		super.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		super.addWindowListener(new WindowEventHandler());
	}
	
	/**
	 * Initialize the game graphics. They will not be added to the frame or 
	 * be getting redrawn yet. Just ready to place into the frame. 
	 */
	private void initGraphics()
	{
		// These are some simply methods I guess. Oh well, room for additions. 
		cloudGfx = new CloudGfx(WIDTH, HEIGHT);
	}
	
	/**
	 * Place the game graphics into this frame. Still not being redrawn. 
	 */
	private void putItAllTogether()
	{
		// For now, just have the panel
		super.add(cloudGfx.rootPanel());
		
		// Recall that pack will do a lot, like sizing stuff. 
		super.pack();
		
		// And of course make sure the frame is visible
		super.setVisible(true);
	}

}
