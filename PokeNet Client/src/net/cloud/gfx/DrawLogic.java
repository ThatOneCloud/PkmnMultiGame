package net.cloud.gfx;

import javax.swing.JPanel;

import net.cloud.client.ConfigConstants;
import net.cloud.client.logging.Logger;
import net.cloud.client.tracking.StatTracker;

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
	
	/** Flag as to whether or not drawing is still going on. */
	private volatile boolean running;
	
	/**
	 * Create a new DrawThread which will repaint the given JPanel. 
	 * This will not start itself. It must be wrapped in a thread and started. 
	 * @param drawPanel the JPanel that will draw the graphic hierarchy
	 */
	public DrawLogic(JPanel drawPanel)
	{
		// Figure out the sleep time from the desired frame rate
		MAX_SLEEP_TIME = 1000 / ConfigConstants.FRAME_RATE;
		
		this.drawPanel = drawPanel;
		
		this.running = false;
	}

	/**
	 * Repaint the graphics in a timed loop. Seeks to maintain the frame rate defined 
	 * in {@link ConfigConstants}. If for some reason drawing falls below this frame rate, 
	 * the loop will run constantly without delay. 
	 */
	@Override
	public void run() {
		this.running = true;
		
		// Just repaint the panel in a loop!
		while(running && !Thread.currentThread().isInterrupted())
		{
			// Keep track of when the loop has started, to better time the wait
			long loopStart = System.currentTimeMillis();
			
			// Start the repaint. This may take a bit.
			drawPanel.repaint();
			
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
	 * Call this with a parameter of false to stop the loop. Once stopped, will not restart.
	 * @param flag False to stop drawing
	 */
	public void setRunning(boolean flag)
	{
		this.running = flag;
	}

}
