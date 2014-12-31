package net.cloud.mmo.logging;

import java.io.PrintWriter;
import java.util.EnumMap;
import java.util.Optional;

import net.cloud.mmo.file.FileRequestException;
import net.cloud.mmo.file.FileServer;
import net.cloud.mmo.file.address.FileAddressBuilder;
import net.cloud.mmo.file.request.PrintWriterRequest;
import net.cloud.mmo.logging.report.LogSection;

/**
 * A collection of PrintWriters to be used for writing to log files, 
 * where each PrintWriter is associated with a different LogSection.
 */
public class LogWriterSet {
	
	/**
	 * The PrintWriters are stored in a map. Because EnumMaps are cool.
	 */
	private EnumMap<LogSection, Optional<PrintWriter>> writers;
	
	/**
	 * Create a new LogWriterSet. It will also initialize PrintWriters 
	 * within the set. There will be a PrintWriter for each LogSection value 
	 * except STD_OUT (which is written to differently)
	 */
	public LogWriterSet()
	{
		writers = new EnumMap<>(LogSection.class);
		
		// Fill in the map values
		fillMap();
	}
	
	/**
	 * Obtain the PrintWriter associated with the log file for the given 
	 * section. On the chance the Writer could not be created, the Optional will be 
	 * empty. Comes as an Optional for easy null-checking.
	 * @param section The kind of log to get a writer to
	 * @return A PrintWriter to the requested log file wrapped in an Optional
	 */
	public Optional<PrintWriter> getWriter(LogSection section)
	{
		return writers.get(section);
	}
	
	/**
	 * Flush out all of the PrintWriters held in this set
	 */
	public void flushWriters()
	{
		// Lambdas! Flush all present PrintWriters
		writers.forEach((s, w) -> w.ifPresent((file) -> file.flush()));
	}
	
	/**
	 * Close all of the PrintWriters held in this set
	 */
	public void closeWriters()
	{
		// Lambdas! Close all present PrintWriters
		writers.forEach((s, w) -> w.ifPresent((file) -> file.close()));
	}
	
	/**
	 * For every LogSection, a PrintWriter to a specific log file will be opened 
	 * and placed into the map. When the file can't be opened, the value will 
	 * be an empty Optional.
	 */
	private void fillMap()
	{
		// Go through each LogSection
		LogSection[] sections = LogSection.values();
		for(LogSection section : sections)
		{
			// Each section will need a file created for it
			placeWriterInMap(section);
		}
	}
	
	/**
	 * In an attempt to break things into methods more (such a basic skill, I know...)
	 * Here we go, a method to get a PrintWriter and place it into the map
	 * @param section The LogSection to obtain a PrintWriter for
	 */
	private void placeWriterInMap(LogSection section)
	{
		// Request a file catered to the section
		PrintWriterRequest fileRequest = new PrintWriterRequest(FileAddressBuilder.newBuilder().createLogFileAddress(section.logName()));
		try {
			PrintWriter file = FileServer.instance().submitAndWaitForDescriptor(fileRequest);
			
			// Once we successfully have a file, wrap it and map it
			writers.put(section, Optional.of(file));
		} catch (FileRequestException e) {
			// There's a chance the file couldn't be created. Shout it out but leave the value empty
			System.err.println("[WARNING] Could not create a log file for " + section.toString());
			writers.put(section, Optional.empty());
		}
	}

}
