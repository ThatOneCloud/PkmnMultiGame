package net.cloud.server.event.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * A runnable object which takes care of the I/O associated with 
 * reading a command string and shipping it off for handling
 */
public class CommandServiceThread implements Runnable {
	
	/** The amount of time between checks for readable data */
	private final int POLL_DELAY;
	
	/** The object used to read in commands */
	private final BufferedReader in;
	
	/** The object used to write messages back to the user */
	private final PrintWriter out;
	
	/** Flag - if the service is running or not */
	private volatile boolean running;
	
	/** The CommandReader to take care of reading from the input */
	private CommandReader cmdReader;

	/**
	 * Create a new object, the provided streams will be used for reading in the commands 
	 * and writing out result messages.
	 * @param pollDelay The amount of time between checks for readable data
	 * @param in An InputStream commands can be read from
	 * @param out An OutputStream results can be written to
	 */
	public CommandServiceThread(int pollDelay, BufferedReader in, PrintWriter out)
	{
		this.POLL_DELAY = pollDelay;
		
		// Wrap the input streams, buffered is good (and has line operations)
		this.in = in;
		this.out = out;
		
		this.cmdReader = new CommandReader(in, out);
		
		// By default, we aren't running
		this.running = false;
	}

	/**
	 * The logic loop. Will read all available commands from the input, 
	 * handle them, show their results, and then wait for the delay period 
	 * before checking for more input.<br>
	 * Stops if the thread is interrupted for the running flag is set to false.
	 */
	@Override
	public void run()
	{
		this.running = true;
		
		// Continue to loop as long as this thread is running
		while(running && !Thread.currentThread().isInterrupted())
		{
			try {
				// See if there are any lines to read and handle them
				readLines();
			} catch (IOException e) {
				// Couldn't read for some reason - Kick out a message
				messageOut("Could not read from input.");
				
				// Continue, keep trying. Note: This may just keep going haphazardly
				continue;
			} catch (InterruptedException e) {
				// Sleep was interrupted - if we're interrupted we'll stop
				messageOut("Command service interrupted");
				
				break;
			}
		}

	}

	/**
	 * Check to see if there are any lines to read from the input, and if so, 
	 * read them and make an attempt to have each handled. 
	 * After this, the thread will sleep for the delay before returning
	 * @throws IOException If the input could not be read from
	 * @throws InterruptedException If the thread was interrupted whilst sleeping
	 */
	private void readLines() throws IOException, InterruptedException
	{
		// Check if there is a line to read (Documentation says read() but it's readLine() this tests)
		if(in.ready())
		{
			// The CommandReader takes care of actually reading from the input
			cmdReader.readCommands();
			
		}
		else {
			// There was nothing to read. Wait a bit before polling again
			Thread.sleep(POLL_DELAY);
		}
	}
	
	/**
	 * Try to write a message to out. If it cannot be written, fails silently
	 * @param message A message to send back to the user, creator, or whatever
	 */
	private void messageOut(String message)
	{
		// Write the message out
		out.println(message);
		out.flush();
	}

	/**
	 * @return If the service is currently doing its loop
	 */
	public boolean isRunning()
	{
		return running;
	}

	/**
	 * Set the running flag - which will keep the io loop going or stop it
	 * @param running The flag. Once set false, the loop will not resume
	 */
	public void setRunning(boolean running)
	{
		this.running = running;
	}

}
