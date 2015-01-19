package net.cloud.client.util;

import java.io.IOException;
import java.io.Writer;

/**
 * A classic tee which will pass all of the actions 
 * through to the constituent writers
 */
public class TeeWriter extends Writer {
	
	/** The first of the outputs */
	protected Writer one;
	
	/** The second of the outputs */
	protected Writer two;
	
	/**
	 * Create a new TeeWriter which will direct all writes to both of the constituent writers
	 * @param outOne The first of the outputs
	 * @param outTwo  The second writer to pass output to
	 */
	public TeeWriter(Writer outOne, Writer outTwo)
	{
		this.one = outOne;
		this.two = outTwo;
	}
	
	@Override
	public Writer append(char c) throws IOException
	{
		one.append(c);
		two.append(c);
		
		return this;
	}
	
	@Override
	public Writer append(CharSequence csq) throws IOException
	{
		one.append(csq);
		two.append(csq);
		
		return this;
	}
	
	@Override
	public Writer append(CharSequence csq, int start, int end) throws IOException
	{
		one.append(csq, start, end);
		two.append(csq, start, end);
		
		return this;
	}
	
	@Override
	public void close() throws IOException {
		one.close();
		two.close();
	}
	
	@Override
	public void flush() throws IOException {
		one.flush();
		two.flush();
	}
	
	@Override
	public void write(char[] cbuf) throws IOException {
		one.write(cbuf);
		two.write(cbuf);
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		one.write(cbuf, off, len);
		two.write(cbuf, off, len);
	}
	
	@Override
	public void write(int c) throws IOException {
		one.write(c);
		two.write(c);
	}

	@Override
	public void write(String str) throws IOException {
		one.write(str);
		two.write(str);
	}

	@Override
	public void write(String str, int off, int len) throws IOException {
		one.write(str, off, len);
		two.write(str, off, len);
	}

}
