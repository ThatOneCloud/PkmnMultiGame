package net.cloud.server.logging.report;

import java.io.PrintWriter;

/**
 * A report about some going-on in the server. 
 */
public abstract class LogReport {
	
	/** Which report file this report will be categorized into */
	private LogSection section;
	
	/** Create a LogReport which will be categorized into the MISC section by default */
	public LogReport()
	{
		this.section = LogSection.MISC;
	}
	
	/** 
	 * Create a LogReport which will be categorized into the given section
	 * @param section Which file this report should go into
	 */
	public LogReport(LogSection section)
	{
		this.section = section;
	}
	
	/**
	 * The report will write its contents to the given PrintWriter. 
	 * It is up to each individual implementation to format the output accordingly, 
	 * however there is no need for <code>flush()</code> to be called, and <code>close()</code> 
	 * should <b>not</b> be called.
	 * @param out The PrintWriter the reports contents will be written to
	 */
	public abstract void logReport(PrintWriter out);
	
	/**
	 * @return The section this report belongs to
	 */
	public LogSection getSection()
	{
		return section;
	}

}
