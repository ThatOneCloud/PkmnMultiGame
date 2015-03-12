package net.cloud.gfx;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.util.function.Supplier;

import javax.swing.JPanel;

import net.cloud.client.ConfigConstants;
import net.cloud.client.logging.Logger;
import net.cloud.client.tracking.StatTracker;
import net.cloud.client.util.IteratorException;
import net.cloud.gfx.elements.Element;

/**
 * This runnable object will run in a loop and have the game graphics redrawn. 
 * By nature of the RootPanel, the graphics should be double buffered and should 
 * retry drawing if the graphics were altered why drawing. <br>
 * The idea here is that drawing should be continuous and not cause any blocking 
 * behavior with other tasks on the graphic hierarchy. 
 */
public class DrawLogic implements Runnable {
	
	/** Ideal amount of time between each drawing */
	private final int MAX_SLEEP_TIME;
	
	/** The panel that will draw the graphic hierarchy */
	private JPanel drawPanel;
	
	/** Obtain the root of the element hierarchy so drawing may start at it */
	private Supplier<Element> elementRoot;
	
	/** Flag as to whether or not drawing is still going on. */
	private volatile boolean running;
	
	/** A ready-to-go completely-drawn image to draw to the game screen */
	private BufferedImage onScreen;
	
	/** The image to draw to, so that what is being drawn to screen is as fast as possible */
	private BufferedImage offScreen;
	
	/** A graphics object associated with the onScreen image */
	private Graphics2D onGfx;
	
	/** A graphics object associated with the offScreen image */
	private Graphics2D offGfx;
	
	/** An object to lock on so we don't swap the images while one is being drawn to screen */
	private Object swapLock = new Object();
	
	/**
	 * Create a new DrawThread which will repaint the given JPanel. 
	 * This will not start itself. It must be wrapped in a thread and started. 
	 * @param drawPanel the JPanel that will draw the graphic hierarchy
	 */
	public DrawLogic(JPanel drawPanel, Supplier<Element> elementRoot)
	{
		// Figure out the sleep time from the desired frame rate
		MAX_SLEEP_TIME = 1000 / ConfigConstants.FRAME_RATE;
		
		this.drawPanel = drawPanel;
		
		this.elementRoot = elementRoot;
		
		this.running = false;
		
		// Create some images that we can draw to
		onScreen = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration()
				.createCompatibleImage(Mainframe.WIDTH, Mainframe.HEIGHT, Transparency.TRANSLUCENT);
		offScreen = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration()
				.createCompatibleImage(Mainframe.WIDTH, Mainframe.HEIGHT, Transparency.TRANSLUCENT);
		onGfx = onScreen.createGraphics();
		offGfx = offScreen.createGraphics();
	}

	/**
	 * Repaint the graphics in a timed loop. Seeks to maintain the frame rate defined 
	 * in {@link ConfigConstants}. If for some reason drawing falls below this frame rate, 
	 * the loop will run constantly without delay. 
	 */
	@Override
	public void run()
	{
		this.running = true;
		
		// Just repaint the panel in a loop!
		while(running && !Thread.currentThread().isInterrupted())
		{
			// Keep track of when the loop has started, to better time the wait
			long loopStart = System.currentTimeMillis();
			
			// We attempt to draw as a comodification will require that we try again
			try {
				// Should be a quick and effective way to clear to transparency
				offGfx.setComposite(AlphaComposite.Clear);
				offGfx.fillRect(0, 0, 800, 600);
				offGfx.setComposite(AlphaComposite.SrcOver);
				
				// Let the element hierarchy kick off its own drawing
				elementRoot.get().drawElement(offGfx, 0, 0);
			} catch (IteratorException e1) {
				// Darn, an unsafe change happened to the hierarchy during drawing. Try again. 
	            // Note that this may result in a lot of redrawings. May need to be fine-tuned.
	            Logger.writer().println("[NOTICE] Iteration exception while drawing");
	            Logger.writer().flush();
	           continue;
			}
			
			// Now we need to swap the offscreen and on screen variables
			synchronized(swapLock)
			{
				BufferedImage tmpImg = onScreen;
				onScreen = offScreen;
				offScreen = tmpImg;
				
				Graphics2D tmpGfx = onGfx;
				onGfx = offGfx;
				offGfx = tmpGfx;
			}
			
			// Tell the panel it'd be a good idea to redraw (asynchronous call)
			drawPanel.repaint();
			
			// Give the processor a break
			try {
				// Figure out how long to sleep this cycle
				long sleepDuration = MAX_SLEEP_TIME - (System.currentTimeMillis() - loopStart);
				sleepDuration = sleepDuration < 0 ? 0 : sleepDuration;
				
				Thread.sleep(sleepDuration);
				
				// Now is the time to report how long the draw loop ended up taking (the end result)
				StatTracker.instance().updateDrawStats((int) (System.currentTimeMillis() - loopStart));
			} catch (InterruptedException e) {
				Logger.instance().logException("Draw thread interrupted", e);
			}
		}
	}
	
	/**
	 * Uses the current on screen image and draws it to the provided graphics object
	 * @param g The graphics object to draw to
	 */
	public void drawOnScreen(Graphics g)
	{
		synchronized(swapLock)
		{
			// Fun fact: Making sure the image matches the graphic device makes this loads faster
			g.drawImage(onScreen, 0, 0, null);
		}
	}
	
	/**
	 * Call this with a parameter of false to stop the loop. Once stopped, will not restart.
	 * @param flag False to stop drawing
	 */
	public void setRunning(boolean flag)
	{
		this.running = flag;
	}

}
