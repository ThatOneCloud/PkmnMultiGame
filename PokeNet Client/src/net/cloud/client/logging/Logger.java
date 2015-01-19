package net.cloud.client.logging;

import java.io.PrintWriter;

import net.cloud.client.ConfigConstants;
import net.cloud.client.event.shutdown.ShutdownHook;
import net.cloud.client.event.shutdown.ShutdownService;
import net.cloud.client.event.shutdown.hooks.LoggerShutdownHook;
import net.cloud.client.logging.report.*;
import net.cloud.client.tracking.StatContainer;
import net.cloud.client.tracking.StatReport;
import net.cloud.client.util.IOUtil;

/**
 * The front to the logging system. The only "point of contact" 
 * other systems should need to have.  This is the place for reports 
 * to be submitted. There are factory style methods that will take care 
 * of creation of the report and then submit them for saving.
 */
public class Logger implements ShutdownService {
	
	/** Singleton instance of the Logger class */
	private static Logger instance;
	
	/** The thread the service is running on */
	private Thread thread;
	
	/** The LoggerService has the queue of reports. We submit through to it */
	private LoggerService loggerService;
	
	/** The hook to stop the service */
	private ShutdownHook shutdownHook;
	
	/** Default singleton constructor - initializes and starts service */
	private Logger()
	{
		// Stop short if logging isn't a-go
		if(!ConfigConstants.LOGGING_ENABLED)
		{
			return;
		}
		
		// Get ready to start the service
		loggerService = new LoggerService();
		thread = new Thread(loggerService);
		
		// Actually start it
		thread.start();
		
		// Now we can create a shutdown hook accordingly
		shutdownHook = new LoggerShutdownHook(thread, loggerService);
	}
	
	/**
	 * Obtain the Logger instance which can then be used to submit reports 
	 * about going-ons in the server.
	 * @return The singleton Logger instance
	 */
	public static Logger instance()
	{
		if(instance == null)
		{
			instance = new Logger();
		}
		
		return instance;
	}
	
	/**
	 * Submit a report to be logged on the next cycle. 
	 * @param report The report to be saved to file
	 */
	public void submit(LogReport report)
	{
		// Don't bother submitting anything if we're not logging
		if(!ConfigConstants.LOGGING_ENABLED)
		{
			return;
		}
		
		// Delegate to the logger service
		loggerService.submit(report);
	}
	
	/**
	 * Submit a log detailing that an exception happened. The default 
	 * log file will be used. 
	 * @param msg A brief message detailing what happened
	 * @param ex The exception that occurred
	 */
	public void logException(String msg, Exception ex)
	{
		// For exceptions, throw out a notice in the console as well
		this.logWriter().println("[EXCEPTION]" + msg);
		this.logWriter().flush();
		submit(new ExceptionLogReport(msg, ex));
	}
	
	/**
	 * Submit a log detailing that an exception happened. The specified 
	 * log file will be used.
	 * @param msg A brief message detailing what happened
	 * @param ex The exception that occurred
	 * @param section Which log file this report should be placed in
	 */
	public void logException(String msg, Exception ex, LogSection section)
	{
		// For exceptions, throw out a notice in the console as well
		this.logWriter().println("[EXCEPTION]" + msg);
		this.logWriter().flush();
		submit(new ExceptionLogReport(msg, ex, section));
	}
	
	/**
	 * Submit a log detailing some message.  A straight forward 
	 * and simple report. The specified log file will be used. 
	 * @param msg The message to report
	 * @param section Which log file this report should be placed in
	 */
	public void logMessage(String msg, LogSection section)
	{
		submit(new MessageLogReport(msg, section));
	}
	
	/**
	 * Submit a log detailing some message.  A straight forward 
	 * and simple report. The default log file will be used. 
	 * @param msg The message to report
	 */
	public void logMessage(String msg)
	{
		submit(new MessageLogReport(msg));
	}
	
	/**
	 * Submit a report detailing the status of the system
	 * @param stats The object with the status information
	 */
	public void logStats(StatContainer stats)
	{
		submit(new StatReport(stats));
	}
	
	/**
	 * Obtain a PrintWriter which will write to both standard output and 
	 * a log file for it.  Note that you will still need to flush the writer 
	 * to make the text appear in the console, but this will not flush the file as well. 
	 * Closing the writer will do nothing, standard out cannot be closed and the file 
	 * should not be closed outside of the logging system.<br>
	 * If logging is not enabled, this writer will just be SYS_OUT
	 * @return The PrintWriter described above.
	 */
	public PrintWriter logWriter()
	{
		// If logging isn't enabled, just throw back SYS_OUT
		if(!ConfigConstants.LOGGING_ENABLED)
		{
			return IOUtil.SYS_OUT;
		}
		
		return loggerService.getLogWriter();
	}
	
	/**
	 * Convenience call for <code>Logger.instance.logWriter()</code>
	 * @return The PrintWriter described in {@link Logger#logWriter()}
	 */
	public static PrintWriter writer()
	{
		return instance().logWriter();
	}

	/**
	 * Obtain the hook to stop this Logger. On shutdown, it will no longer 
	 * accept any more reports but will continue to save reports until there 
	 * are none left queued. 
	 * This hook will be left null if logging is not enabled (via the LOGGING_ENABLED constant)
	 */
	@Override
	public ShutdownHook getShutdownHook() throws NullPointerException {
		return shutdownHook;
	}

}
