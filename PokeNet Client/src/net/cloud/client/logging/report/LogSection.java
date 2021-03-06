package net.cloud.client.logging.report;

/**
 * An enumerable type for the various kinds of reports. 
 * Each different kind of report will go into a different 
 * file, the values here define what files there are and 
 * are used to categorize LogReports
 */
public enum LogSection {
	
	/** Report for the status of the system (StatReport) */
	STATS("stats"),
	
	/** Reports that don't necessary belong to any other section */
	MISC("misc");
	
	/** The name of the section, suitable as a file name */
	private String logName;
	
	/**
	 * @param logName The name of the section, suitable as a file name
	 */
	private LogSection(String logName)
	{
		this.logName = logName;
	}
	
	/**
	 * @return The name of the section, suitable as a file name
	 */
	public String logName()
	{
		return logName;
	}

}
