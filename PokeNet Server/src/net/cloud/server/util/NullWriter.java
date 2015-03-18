package net.cloud.server.util;

import java.io.PrintWriter;
import java.io.Writer;

/**
 * This Writer writes to... the void o_o
 * Apparently there's some /dev/null "file" that just writes to nowhere and acts successful
 */
public class NullWriter extends Writer {
	
	/** Useful if you want to avoid object instantiations */
	public static final NullWriter TO_NOWHERE = new NullWriter();
	
	/** A PrintWriter wrapped around NullWriter */
	public static final PrintWriter NULL_PRINT_WRITER = new PrintWriter(TO_NOWHERE);
	
	/** Create a new writer to the void */
	public NullWriter() {}
	
	@Override
	public Writer append(char c) { return this; }
	
	@Override
	public Writer append(CharSequence csq) { return this; }
	
	@Override
	public Writer append(CharSequence csq, int start, int end) { return this; }
	
	@Override
	public void close() {}
	
	@Override
	public void flush() {}
	
	@Override
	public void write(char[] cbuf) {}

	@Override
	public void write(char[] cbuf, int off, int len) {}
	
	@Override
	public void write(int c) {}

	@Override
	public void write(String str) {}

	@Override
	public void write(String str, int off, int len) {}

}
