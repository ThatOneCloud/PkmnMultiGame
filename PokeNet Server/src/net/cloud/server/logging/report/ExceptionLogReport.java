package net.cloud.server.logging.report;

import java.io.PrintWriter;

/**
 * A LogReport which will summarize an exception that occurred. 
 */
public class ExceptionLogReport extends LogReport {
	
	/** A possibly redundant message about what went on */
	private String message;
	
	/** The exception we're going to tattle on */
	private Exception exception;
	
	/**
	 * Create a LogReport which will detail an exception that occurred 
	 * alongside a message describing what happened. The message may be redundant 
	 * but that's okay.
	 * @param message A brief message about the problem
	 * @param exception The exception this report is all about
	 */
	public ExceptionLogReport(String message, Exception exception) {
		super();
		this.message = message;
		this.exception = exception;
	}

	/**
	 * Create a LogReport which will detail an exception that occurred 
	 * alongside a message describing what happened. The message may be redundant 
	 * but that's okay.
	 * @param message A brief message about the problem
	 * @param exception The exception this report is all about
	 * @param section Where to file this report
	 */
	public ExceptionLogReport(String msg, Exception ex, LogSection section) {
		super(section);
		this.message = msg;
		this.exception = ex;
	}

	/**
	 * The contents of this report will be displayed as a general 
	 * message about what happened and the stack trace of the exception.
	 */
	@Override
	public void logReport(PrintWriter out) {
		// The message is first
		out.println(message);

		// The exception is shown as its stack trace (I'm glad that method accepts a PrintWriter)
		exception.printStackTrace(out);
	}

}
