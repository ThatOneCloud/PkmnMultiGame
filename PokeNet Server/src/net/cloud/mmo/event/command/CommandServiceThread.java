package net.cloud.mmo.event.command;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * A runnable object which takes care of the I/O associated with 
 * reading a command string and shipping it off for handling
 */
public class CommandServiceThread implements Runnable {
	
	/** The object used to read in commands */
	private BufferedReader in;
	
	/** The object used to write messages back to the user */
	private BufferedWriter out;
	
	/** Flag - if the service is running or not */
	private boolean running;

	/**
	 * Create a new object, the provided streams will be used for reading in the commands 
	 * and writing out result messages.
	 * @param in An InputStream commands can be read from
	 * @param out An OutputStream results can be written to
	 */
	public CommandServiceThread(InputStream in, OutputStream out) {
		// Wrap the input streams, buffered is good (and has line operations)
		this.in = new BufferedReader(new InputStreamReader(in));
		this.out = new BufferedWriter(new OutputStreamWriter(out));
		
		// By default, we aren't running
		this.running = false;
	}

	@Override
	public void run() {
		this.running = true;
		
		// Continue to loop as long as this thread is running
		while(running)
		{
			String commandLine = null;
			
			// Read a line from the input stream
			try {
				commandLine = in.readLine();
			} catch (IOException e) {
				// Couldn't read for some reason - Kick out a message
				messageOut("Could not read command from input.");
				
				// Continue, keep trying. Note: This may just keep going haphazardly
				continue;
			}
			
			// CommandHandler takes over, to deal with.. well.. handling.
			try {
				// A Future is returned. It's value is a result message from the command once executed
				Future<String> commandFuture = CommandHandler.getInstance().handleCommand(commandLine);
				
				// Wait until the future has a result - then show it
				messageOut(commandFuture.get());
			} catch (CommandException e) {
				// Oops, something came up while handling the command.
				// Message in the exception is meant for display
				messageOut(e.getMessage());
			} catch (InterruptedException | ExecutionException | CancellationException e) {
				// The future's result couldn't be retrieved
				messageOut("Results of command not available.");
			}
		}

	}
	
	/**
	 * Try to write a message to out. If it can't be written to the output, 
	 * the exception will be caught and printed to standard error
	 * @param message A message to send back to the user, creator, or whatever
	 */
	private void messageOut(String message)
	{
		try {
			// Write the message to whatever output we were provided
			out.write(message + System.lineSeparator());
			out.flush();
		} catch (IOException e) {
			// Output didn't work. Exception gets shown in standard err, then.
			e.printStackTrace();
		}
	}

	/**
	 * @return If the service is currently doing its loop
	 */
	public boolean isRunning() {
		return running;
	}

	/**
	 * Set the running flag - which will keep the io loop going or stop it
	 * @param running The flag. Once set false, the loop will not resume
	 */
	public void setRunning(boolean running) {
		this.running = running;
	}

}
