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

public class CommandServiceThread implements Runnable {
	
	private BufferedReader in;
	
	private BufferedWriter out;
	
	private boolean running;

	public CommandServiceThread(InputStream in, OutputStream out) {
		this.in = new BufferedReader(new InputStreamReader(in));
		this.out = new BufferedWriter(new OutputStreamWriter(out));
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

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}
	
	

}
