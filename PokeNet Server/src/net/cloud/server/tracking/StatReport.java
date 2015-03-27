package net.cloud.server.tracking;

import java.io.PrintWriter;

import net.cloud.server.ConfigConstants;
import net.cloud.server.logging.report.LogReport;
import net.cloud.server.logging.report.LogSection;

/**
 * A LogReport which will store and print out information about the status 
 * of the application. This information is created via the StatTracker system, 
 * and is updated at regular intervals. 
 */
public class StatReport extends LogReport {
	
	/** This object holds information on what we need to report */
	private StatContainer stats;
	
	/**
	 * Create a new report for the status of the system. The report will print the information 
	 * contained in the provided stat object.
	 * @param stats The object with all of the statistics stored in it
	 */
	public StatReport(StatContainer stats)
	{
		super(LogSection.STATS);
		
		this.stats = stats;
	}

	@Override
	public void logReport(PrintWriter out)
	{
		StringBuilder report = new StringBuilder();
		
		// A "nice" header with some general information. Lots of prints because primitives? I 'unno
		report.append("Status Report at ");
		report.append(stats.getCreationTime().getHour());
		report.append(":");
		report.append(stats.getCreationTime().getMinute());
		report.append(":");
		report.append(stats.getCreationTime().getSecond());
		report.append(System.lineSeparator());
		
		// Players online
		report.append("Players Online: ");
		report.append(stats.getPlayersOnlineStat());
		report.append(System.lineSeparator());
		
		// An extra blank line for some separation
		report.append(System.lineSeparator());
		
		// Write it to the report writer for sure
		out.write(report.toString());

		// Maybe write to the console as well
		if(ConfigConstants.STATS_TO_CONSOLE)
		{
			System.out.print(report);
			System.out.flush();
		}
	}

}
