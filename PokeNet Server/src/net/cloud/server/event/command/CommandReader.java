package net.cloud.server.event.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import net.cloud.server.logging.Logger;

/**
 * Despite the name, no this class is not a subclass of Reader. 
 * Rather, it takes a BufferedReader and will read lines from it 
 * in order to parse commands and have them executed. 
 * The execution of commands this way is blocking, all available 
 * commands will run before the <code>readCommands()</code> method returns.
 */
public class CommandReader {
	
	/** The input that will provide lines of commands */
	private BufferedReader in;
	
	/** Used to send messages back out */
	private PrintWriter out;
	
	/**
	 * Create a new CommandReader, which will then be able to read commands 
	 * from the provided input and display results to the provided output
	 * @param in The input that will provides lines of commands
	 * @param out Where result messages will be sent
	 */
	public CommandReader(BufferedReader in, PrintWriter out)
	{
		this.in = in;
		this.out = out;
	}
	
	/**
	 * Read all lines available from the input and take action on the commands provided. 
	 * Results from the execution of the commands will be shown via the provided output. 
	 * The handling is blocking in nature - this will not return until all of the available 
	 * commands have been handled.
	 */
	public void readCommands()
	{
		try {
			// See if there are any lines to read and handle them
			readLines();
		} catch (IOException e) {
			// Couldn't read for some reason - Kick out a message
			messageOut("Could not read command from input.");
		}
	}

	/**
	 * Read from the input all available lines and have them handled. 
	 * @throws IOException If the input could not be read from
	 */
	private void readLines() throws IOException
	{
		while(in.ready())
		{
			String commandLine = null;
			
			// Read a line from the input stream
			commandLine = in.readLine();
			
			// Have the command move on for handling
			handleCommand(commandLine);
		}
	}
	
	/**
	 * Handle the command passed in. The result of the command 
	 * is displayed to the output specified during construction of this object
	 * @param commandLine The entirety of the text for the command
	 */
	private void handleCommand(String commandLine) {
		// CommandHandler takes over, to deal with.. well.. handling.
		try {
			// A Future is returned. It's value is a result message from the command once executed
			Future<String> commandFuture = CommandHandler.instance().handleCommand(commandLine);
			
			// Wait until the future has a result - then show it
			messageOut(commandFuture.get());
			
			// Since we've got results now, log that a command was used
			Logger.instance().logCommand(commandLine, commandFuture.get());
		} catch (CommandException e) {
			// Oops, something came up while handling the command.
			// Message in the exception is meant for display
			messageOut(e.getMessage());
			
			// The results are an exception message, but that's something to log as well
			Logger.instance().logCommand(commandLine, e.getMessage());
		} catch (InterruptedException | ExecutionException | CancellationException e) {
			// The future's result couldn't be retrieved
			messageOut("Results of command not available.");
			
			// There are no results, but log that nonetheless
			Logger.instance().logCommand(commandLine, "Results of command not available.");
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
	
}
