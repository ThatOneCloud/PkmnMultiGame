package net.cloud.client.event.shutdown.hooks;

import java.io.PrintWriter;

import net.cloud.client.event.shutdown.ShutdownException;
import net.cloud.client.event.shutdown.ShutdownHook;
import net.cloud.gfx.DrawLogic;

/**
 * This hook will stop a CloudGfx system from drawing. 
 * After this hook is shutdown, the graphics will no longer be redrawn. 
 */
public class GraphicShutdownHook implements ShutdownHook {
	
	/** The object dealing with calling repaint */
	private DrawLogic drawLogic;
	
	/** The thread the draw logic is running on */
	private Thread drawThread;

	/**
	 * Create a new shutdown hook for a CloudGfx system. When this hook is 
	 * run, it will stop the graphics from being redrawn. 
	 * @param drawLogic The object dealing with calling repaint
	 * @param drawThread The thread the draw logic is running on
	 */
	public GraphicShutdownHook(DrawLogic drawLogic, Thread drawThread)
	{
		this.drawLogic = drawLogic;
		this.drawThread = drawThread;
	}
	
	/**
	 * Tell the graphics attached to this hook to stop redrawing itself. 
	 * This method will return immediately.
	 */
	@Override
	public void shutdown(PrintWriter out) throws ShutdownException {
		out.println("Shutting down graphics");
		out.flush();
		
		// Try to interrupt the thread
		try {
			drawLogic.setRunning(false);
			drawThread.interrupt();
		} catch (Exception e) {
			// Chain exceptions
			throw new ShutdownException("Could not interrupt graphics thread", e);
		}
		
		out.println("Graphics shut down");
		out.flush();
	}

}
