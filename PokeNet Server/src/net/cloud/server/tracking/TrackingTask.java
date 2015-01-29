package net.cloud.server.tracking;

import net.cloud.server.event.task.voidtasks.CancellableVoidTask;
import net.cloud.server.logging.Logger;

/**
 * A task which should periodically be run. It will tell the Logging system that another 
 * report should be made for the current system status. 
 */
public class TrackingTask extends CancellableVoidTask {
	
	/**
	 * Upon execution, this task will pass the Logger a copy of the current system status. 
	 */
	@Override
	public void execute() {
		try {
			// Contact logger with a static copy of current stats
			Logger.instance().logStats(StatTracker.instance().getStats().clone());
		} catch (CloneNotSupportedException e) {
			// For some reason something didn't clone. Pop out a notice but that's it. 
			Logger.instance().logException("Could not clone StatContainer", e);
		}

	}

}
