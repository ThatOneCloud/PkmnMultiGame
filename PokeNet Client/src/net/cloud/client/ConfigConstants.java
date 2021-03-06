package net.cloud.client;

/**
 * I cba to load these things from a file, but having them all in one 
 * class is convenient enough.
 */
public class ConfigConstants {

	/** 
	 * Determines if log files will be created. No matter what this service will run, 
	 * but if this flag is true, logging will be sent to both SYS_OUT <b>and</b> a log file. 
	 * If it is false, the output will <b>only</b> go to SYS_OUT.
	 */
	public static final boolean LOGGING_ENABLED = false;
	
	/** Amount of time in milliseconds between each save of queued log reports */
	public static final int LOG_CYCLE_TIME = 5000;
	
	/** Number of threads the task engine will have available for running tasks */
	public static final int THREAD_POOL_SIZE = 2;
	
	/** Frames per second the UI will attempt to draw at */
	public static final int FRAME_RATE = 15;

	/** How frequently the current system status will be logged (ms) */
	public static final int TRACK_TASK_INTERVAL = 10000;

}
