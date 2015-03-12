package net.cloud.client.logging;

import java.io.IOException;
import java.io.Writer;

import net.cloud.client.util.TeeWriter;

/**
 * A TeeWriter which specifically will direct writes to standard out (SYS_OUT) 
 * and to the writer being used for the logging file. 
 * Flushing this writer will only affect the standard out writer, 
 * and closing this writer will only affect the logging writer.
 */
public class LoggingTeeWriter extends TeeWriter {

	/**
	 * Create a TeeWriter which will only close the second writer, 
	 * and will only flush the first writer.
	 * @param stdOut Writer intended to be console output
	 * @param logOut Writer intended to write to the log file
	 */
	public LoggingTeeWriter(Writer stdOut, Writer logOut)
	{
		super(stdOut, logOut);
	}
	
	/**
	 * This will not close either of the writers
	 */
	@Override
	public void close() throws IOException
	{
		// Do nothing. Closing standard out is without effect 
		// and the log file should be protected from outside users closing it.
	}
	
	/**
	 * Flush only the first writer, rather than both
	 */
	@Override
	public void flush() throws IOException
	{
		// The first writer is the std output. Only flush it.
		one.flush();
	}

}
