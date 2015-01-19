package net.cloud.server.logging;

import java.io.PrintWriter;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.cloud.server.ConfigConstants;
import net.cloud.server.file.FileRequestException;
import net.cloud.server.file.FileServer;
import net.cloud.server.file.address.FileAddressBuilder;
import net.cloud.server.file.request.PrintWriterRequest;
import net.cloud.server.logging.report.LogReport;
import net.cloud.server.util.IOUtil;

/**
 * The runnable service which holds a queue of LogReports to be 
 * processed and saved to file.
 */
public class LoggerService implements Runnable {
	
	/** Queue of all reports waiting to be written to file */
	private Queue<LogReport> reportQueue;
	
	/** A set of PrintWriters for the different LogSection files */
	private LogWriterSet writerSet;
	
	/** Since the log file may not have opened, keep it in an Optional */
	private Optional<PrintWriter> logFile;
	
	/** This PrintWriter will write to the console and log file */
	private PrintWriter logWriter;
	
	/** Flag as to whether the service is running or not. (will not even save reports) */
	private volatile boolean running;
	
	/** Flag as to whether the service is still accepting reports or not */
	private volatile boolean accepting;

	/** 
	 * Default constructor. Intended to be used by the Logger class - not directly 
	 */
	public LoggerService()
	{
		reportQueue = new ConcurrentLinkedQueue<LogReport>();
		accepting = true;
		
		// Initialize the log file to standard out
		initLogFile();
		
		// Initialize the writer that is tee'd to both sys_out and the log file
		initLogWriter();
		
		// Each LogSection will have its own file. This set tracks all that.
		writerSet = new LogWriterSet();
	}

	/**
	 * This method will loop for as long as the service is running. 
	 * Each cycle of it will save all pending file changes
	 */
	@Override
	public void run() {
		running = true;
		
		// Go for as long as we're set to be running
		while(running)
		{
			// Once we're no longer accepting, there's no need to loop again
			updateRunningFlag();
			
			// Check if there is anything in the queue to save to file
			processQueue();
			
			// Flush each of the section files
			writerSet.flushWriters();
			
			// Flush the standard out file to make sure it gets updated
			flushStdOutFile();
			
			// Now that the queue has been emptied, wait before doing it again
			try {
				Thread.sleep(ConfigConstants.LOG_CYCLE_TIME);
			} catch (InterruptedException e) {
				// This is a normal part of shutting down
				System.err.println("Logger Service interrupted");
			}
		}
		
		// Before finishing up, close the standard out file
		logFile.ifPresent((file) -> file.close());
		
		// As well as the files in the writer set
		writerSet.closeWriters();
	}

	/**
	 * Submit a report so that it will be saved to file on the next 
	 * cycle of the service. Thread-safe and non-blocking. Thanks Java Concurrent API!<br>
	 * This will not throw an exception or anything if reports are not being accepted. It'd be 
	 * out of the hands of the caller anyways.
	 * @param report The report which should be saved away
	 */
	public void submit(LogReport report)
	{
		// Only add to the queue if we're still accepting reports
		if(accepting)
		{
			// Add to the queue. Good thing it's a concurrent queue
			reportQueue.add(report);
		}
	}
	
	/**
	 * Tell this service that it should stop. 
	 * This will cause it to stop accepting more reports, 
	 * however it will continue until all currently queued reports have been saved.
	 */
	public void stop()
	{
		this.accepting = false;
	}
	
	/**
	 * Obtain a Writer which can be used to print to both standard output 
	 * as well as a log file for standard output. 
	 * @return A PrintWriter to both standard out and its log file
	 */
	public PrintWriter getLogWriter()
	{
		return logWriter;
	}
	
	/**
	 * Initialize the log file for writing a log of standard output
	 * If the file cannot be opened, an error message will be printed and the Optional will be empty
	 */
	private void initLogFile() {
		// Obtain a PrintWriter to a file to use for logging
		PrintWriterRequest fileRequest = new PrintWriterRequest(FileAddressBuilder.newBuilder().createLogFileAddress("std_out"));
		
		try {
			// Given the file is opened, use that
			logFile = Optional.of(FileServer.instance().submitAndWaitForDescriptor(fileRequest));
		} catch (FileRequestException e) {
			// Oops. File didn't open, so we essentially leave it null
			System.err.println("[WARNING] Could not create log file. Using only SYS_OUT");
			logFile = Optional.empty();
		}
	}
	
	/**
	 * Initialize the Writer which will write through to both standard output 
	 * and the log file.  (This writer will only go to standard out if the file isn't open)
	 */
	private void initLogWriter() {
		// Now the writer to be used for logging depends on if the file opened		
		if(logFile.isPresent())
		{
			// The writer will go to both SYS_OUT and the log file
			logWriter = new PrintWriter(new LoggingTeeWriter(IOUtil.SYS_OUT, logFile.get()));
		} else {
			// The writer will only go to SYS_OUT
			logWriter = IOUtil.SYS_OUT;
		}
	}
	
	/**
	 * For every LogReport currently queued, remove if from the queue 
	 * and have it save itself to the correct log file.
	 */
	private void processQueue() {
		while(!reportQueue.isEmpty())
		{
			// We know the queue will have something there. Grab it.
			LogReport report = reportQueue.poll();
			
			// Have report saved to file
			writerSet.getWriter(report.getSection()).ifPresent((file) -> report.logReport(file));
		}
	}

	/**
	 * Check to see if the loop should continue running. 
	 * If not, the running flag will be set to false
	 */
	private void updateRunningFlag() {
		// We know it's time to stop when we're no longer accepting reports
		if(!accepting)
		{
			running = false;
		}
	}
	
	/**
	 * If the log file was successfully opened, flush any pending 
	 * writes to the file. 
	 */
	private void flushStdOutFile()
	{
		logFile.ifPresent((file) -> file.flush());
	}
	
}
