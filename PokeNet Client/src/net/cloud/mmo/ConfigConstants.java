package net.cloud.mmo;

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
	public static final boolean LOGGING_ENABLED = true;
	
	/** Amount of time in milliseconds between each save of queued log reports */
	public static final int LOG_CYCLE_TIME = 5000;
	
	/** Number of threads the task engine will have available for running tasks */
	public static final int THREAD_POOL_SIZE = 1;
	
	/** Frames per second the UI will attempt to draw at */
	public static final int FRAME_RATE = 15;

}
