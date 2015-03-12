package net.cloud.client.tracking;

import net.cloud.client.ConfigConstants;
import net.cloud.client.event.task.TaskEngine;
import net.cloud.client.event.task.voidtasks.CancellableVoidTask;

/**
 * A class which can be updated with various game statistics to keep 
 * track of how the application is doing.  There are effectively two modes of 
 * operation: Always on and temporarily on.  Always on will track operation 
 * always when the client is running.  Temporarily on will only keep track 
 * when the statistics overlay interface is open.  <br>
 * Updates will periodically be written to a log file, via a scheduled task.
 */
public class StatTracker {
	
	/** The various operating modes that are possible */
	private enum TrackingMode 
	{
		/** Tracking will always happen for the lifetime of the client */
		ALWAYS_ON, 
		/** Tracking will only happen when the statistics overlay is showing */
		TEMP_ON, 
		/** Not an effective mode, but either way this means tracking isn't occurring */
		OFF
	};
	
	/** Singleton instance of this object. All threads will come through here. */
	private static StatTracker instance;
	
	/** The current operating mode the tracker is in */
	// Note: Access is synchronized on the instance object. Multiple threads may toggle it. 
	// Note: For Temporarily On mode, set this to OFF instead (since that'll actually be the starting state)
	private TrackingMode mode = TrackingMode.ALWAYS_ON;
	
	/** The task which will occasionally write to the log file */
	// Note: Also synchronized with mode. They go hand in hand when they need to.
	private CancellableVoidTask trackingTask;
	
	/** This object will keep track of the current statistics */
	private StatContainer stats;
	
	/** Default private constructor for singleton pattern */
	private StatTracker()
	{
		// Construct a task from the get-go only if we're always on. Else it'll get cancelled and created repeatedly. 
		if(mode == TrackingMode.ALWAYS_ON)
		{
			trackingTask = createTrackingTask();
		}
		
		// Always initialize the stats object
		stats = new StatContainer();
	}
	
	/**
	 * Obtain a StatTracker which can be informed of going-ons in the application
	 * @return The singleton instance of the StatTracker
	 */
	public static StatTracker instance()
	{
		if(instance == null)
		{
			synchronized(StatTracker.class)
			{
				if(instance == null)
				{
					instance = new StatTracker();
				}
			}
		}
		
		return instance;
	}
	
	/**
	 * Inform the StatTracker that the statistics overlay has been opened and is now showing. 
	 * If the mode is set to TEMP_ON, then this will start tracking. 
	 */
	public synchronized void overlayOpened()
	{
		// Take advantage of ALWAYS_ON mode never being off
		if(mode == TrackingMode.OFF)
		{
			// Switch tracking on 
			mode = TrackingMode.TEMP_ON;
			
			// Create a new task (and have it submitted as well)
			trackingTask = createTrackingTask();
		}
	}
	
	/**
	 * Inform the StatTracker that the statistics overlay has been closed is is no longer showing. 
	 * If the mode is set to TEMP_ON, then this will stop tracking. 
	 */
	public synchronized void overlayClosed()
	{
		// Same as above. ALWAYS_ON won't become off, so this is safe to do. 
		if(mode == TrackingMode.TEMP_ON)
		{
			// Switch tracking off
			mode = TrackingMode.OFF;
			
			// Stop the task which [should] be currently running
			trackingTask.cancel();
			trackingTask = null;
		}
	}
	
	/**
	 * Obtain the object holding information on the current system stats. This can then be cloned to 
	 * create a StatContainer as a record of a specific moment.
	 * @return The current system statistics container
	 */
	public StatContainer getStats()
	{
		return stats;
	}
	
	/**
	 * Update statistics and status information pertaining to drawing. 
	 * @param time The amount of time the draw loop took in milliseconds
	 */
	public void updateDrawStats(int time)
	{
		if(!tracking())
		{
			return;
		}
		
		// Figure out the fps. Protect against division by zero.
		int fps = time == 0 ? 1000 : (1000 / time);
		stats.updateFpsStat(fps);
	}
	
	/**
	 * Check to see if we are currently tracking statistics
	 * @return True if tracking should be done
	 */
	private boolean tracking()
	{
		return mode != TrackingMode.OFF;
	}
	
	/**
	 * Create a Task which will write the current statistic information 
	 * to the log file. This method is synchronized with the mode methods 
	 * since a lot of its usage revolves around them. Just in case. Re-entrance woo!
	 * @return A Task to be scheduled at a fixed rate. It will write to the log file. 
	 */
	private synchronized CancellableVoidTask createTrackingTask()
	{
		// Create a specialized task for logging the stats
		CancellableVoidTask task = new TrackingTask();
		
		// Also have this submit the task to the engine. 'cause why not.
		TaskEngine.instance().scheduleImmediate(ConfigConstants.TRACK_TASK_INTERVAL, task);
		
		return task;
	}

}
