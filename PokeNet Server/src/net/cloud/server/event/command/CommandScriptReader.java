package net.cloud.server.event.command;

import java.io.BufferedReader;
import java.io.PrintWriter;

import net.cloud.server.file.FileRequestException;
import net.cloud.server.file.FileServer;
import net.cloud.server.file.address.FileAddressBuilder;
import net.cloud.server.file.request.BufferedReaderRequest;

/**
 * This despite its name is not a Reader, either. 
 * It will read from a command script and execute all 
 * of the commands that are detailed in the script.  Like the 
 * {@link CommandReader} it is blocking in nature and will 
 * not return until all available commands have been executed.
 */
public class CommandScriptReader {
	
	/** Name of the command script to run */
	private String scriptName;
	
	/** The input to read commands from */
	private BufferedReader in;
	
	/** The output to show result messages to */
	private PrintWriter out;
	
	/** The CommandReader will handle reading from the input */
	private CommandReader cmdReader;
	
	/**
	 * Create a new CommandScriptReader which will open the given script file 
	 * and then execute all of the commands specified in the file.
	 * @param scriptName The name of the command script to execute
	 * @param out Where the results will be shown to
	 */
	public CommandScriptReader(String scriptName, PrintWriter out)
	{
		this.scriptName = scriptName;
		this.out = out;
	}
	
	/**
	 * Read all of the commands available from the file and run them. 
	 * If the file cannot be opened, no commands will be run and an 
	 * error message will be shown to the output. 
	 * This will not return until all of the commands have executed.
	 */
	public void readCommands()
	{
		// Get the input reader ready to go
		try {
			initBR();
		} catch (FileRequestException e) {
			messageOut("Command script could not be loaded");
			return;
		}
		
		// Then create a CommandReader - which we'll delegate actual reading work to
		initCmdReader();
		
		// And now we can delegate off. We've done all we needed to
		cmdReader.readCommands();
	}
	
	/**
	 * Obtain and initialize a BufferedReader to the command file
	 * @throws FileRequestException If the file could not be opened
	 */
	private void initBR() throws FileRequestException
	{
		BufferedReaderRequest req = new BufferedReaderRequest(FileAddressBuilder.newBuilder().createCommandScriptAddress(scriptName));
		in = FileServer.instance().submitAndWaitForDescriptor(req);
	}
	
	/** Initialize the CommandReader which will be used to read from the input */
	private void initCmdReader()
	{
		cmdReader = new CommandReader(in, out);
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
