package net.cloud.client.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * A class filled with utility methods concerning 
 * I/O classes and their usage. For example, there are 
 * methods to convert and wrap an i/o object to another type.
 */
public class IOUtil {
	
	/** A BufferedReader which will read from System.in */
	public static final BufferedReader SYS_IN = streamToReader(System.in);
	
	/** A PrintWriter which will write to System.out */
	public static final PrintWriter SYS_OUT = streamToWriter(System.out);
	
	/**
	 * Wraps the given stream into a buffered reader
	 * @param in The InputStream to convert into a BufferedReader
	 * @return A BufferedReader that will read from the InputStream
	 */
	public static BufferedReader streamToReader(InputStream in)
	{
		return new BufferedReader(new InputStreamReader(in));
	}
	
	/**
	 * Wraps the given stream into a PrintWriter. The writer will be buffered.
	 * @param out The OutputStream to wrap into a PrintWriter
	 * @return A PrintWriter that when written to, will write to the OutputStream
	 */
	public static PrintWriter streamToWriter(OutputStream out)
	{
		return new PrintWriter(new BufferedWriter(new OutputStreamWriter(out)));
	}
	
	/**
	 * Wraps the given StringWriter into a PrintWriter so that the Writer 
	 * may be used throughout the server. 
	 * @param stringWriter The StringWriter which will ultimately collect output
	 * @return A PrintWriter which will write to the StringWriter
	 */
	public static PrintWriter writerToString(StringWriter stringWriter)
	{
		return new PrintWriter(stringWriter);
	}

}
