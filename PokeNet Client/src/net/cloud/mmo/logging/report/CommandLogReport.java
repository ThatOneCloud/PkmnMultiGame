package net.cloud.mmo.logging.report;

import java.io.PrintWriter;

/**
 * A LogReport specialized to keep track of a command that was used in the 
 * server and the results it gave back.
 */
public class CommandLogReport extends LogReport {
	
	/** The command that was issued in entirety */
	private String line;
	
	/** The results that the handling of the command returned */
	private String results;
	
	/**
	 * Create a new CommandReport with the given line and results. It will 
	 * be under the COMMAND LogSection
	 * @param line The command that was issued in entirety
	 * @param results The results that the handling of the command returned
	 */
	public CommandLogReport(String line, String results)
	{
		super(LogSection.COMMAND);
		
		this.line = line;
		this.results = results;
	}

	/**
	 * Logging a CommandReport will include a formatted piece of information 
	 * about the usage of a command.
	 */
	@Override
	public void logReport(PrintWriter out) {
		// It's implied that a command is being used. So jump right into the command
		out.println("[Command]: " + line);
		
		// and include the results it gave back
		out.println("[Results]: " + results);
		
		// and end with another line to keep the log looking clean
		out.print(System.lineSeparator());
	}

}
