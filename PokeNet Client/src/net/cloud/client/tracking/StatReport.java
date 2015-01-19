package net.cloud.client.tracking;

import java.io.PrintWriter;

import net.cloud.client.logging.report.LogReport;
import net.cloud.client.logging.report.LogSection;

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
	public void logReport(PrintWriter out) {
		// A "nice" header with some general information. Lots of prints because primitives? I 'unno
		out.print("Status Report at ");
		out.print(stats.getCreationTime().getHour());
		out.print(":");
		out.print(stats.getCreationTime().getMinute());
		out.print(":");
		out.println(stats.getCreationTime().getSecond());
		
		// Frames-per-second
		out.print("FPS: ");
		out.println(stats.getFpsStat());
		
		// An extra blank line for some separation
		out.println("");

	}

}
