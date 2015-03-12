
package net.cloud.client.logging.report;

import java.io.PrintWriter;

/**
 * A LogReport which simply contains a text message. 
 * Probably going to be by far the most prevalent type of report.
 */
public class MessageLogReport extends LogReport {
	
	/** The message this report will print out, as is */
	private final String message;
	
	/**
	 * A MessageLogReport which will just print the message to the 
	 * default log file. 
	 * @param message The message to report as-is
	 */
	public MessageLogReport(String message)
	{
		super();
		this.message = message;
	}

	/**
	 * A MessageLogReport which will just print the message to the 
	 * specified log file. 
	 * @param msg The message to report as-is
	 * @param section Which file to write the report to
	 */
	public MessageLogReport(String msg, LogSection section)
	{
		super(section);
		this.message = msg;
	}

	/** Write this reports message to the PrintWriter */
	@Override
	public void logReport(PrintWriter out)
	{
		out.println(message);
	}

}
