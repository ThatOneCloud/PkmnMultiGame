package net.cloud.client.event.shutdown.hooks;

import java.io.PrintWriter;

import net.cloud.client.event.shutdown.ShutdownException;
import net.cloud.client.event.shutdown.ShutdownHook;
import net.cloud.client.logging.LoggerService;

/**
 * A shutdown hook which will stop the Logger service. 
 * On shutdown, the logger will not accept any more reports but 
 * will finish processing all reports currently queued. 
 */
public class LoggerShutdownHook implements ShutdownHook {
	
	/** The thread the logger service is running on */
	private Thread loggingThread;
	
	/** The object responsible for queueing and processing reports */
	private LoggerService service;
	
	/**
	 * Create a new shutdown hook for a LoggerService. 
	 * @param loggingThread The thread the service is running on
	 * @param service The service itself
	 */
	public LoggerShutdownHook(Thread loggingThread, LoggerService service)
	{
		this.loggingThread = loggingThread;
		this.service = service;
	}

	/**
	 * Stop the LoggerService. It will no longer accept LogReports 
	 * but will continue processing and saving them until there are 
	 * no more queued.
	 */
	@Override
	public void shutdown(PrintWriter out) throws ShutdownException
	{
		// It is important that stop() is called before interrupt()
		service.stop();
		loggingThread.interrupt();
	}

}
