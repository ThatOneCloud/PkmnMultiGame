package net.cloud.mmo.event.command;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class CommandServiceThread implements Runnable {
	
	private BufferedReader in;
	
	private BufferedWriter out;
	
	private boolean running;

	public CommandServiceThread(InputStream in, OutputStream out) {
		// TODO Auto-generated constructor stub
		this.in = new BufferedReader(new InputStreamReader(in));
		this.out = new BufferedWriter(new OutputStreamWriter(out));
		this.running = false;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
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
				messageOut("CommandServiceThread could not read command from input.");
				
				// Continue, keep trying. Note: This may just keep going haphazardly
				continue;
			}
			
			// CommandHandler takes over, to deal with.. well.. handling.
			CommandHandler.getInstance().handleCommand(commandLine);
		}

	}
	
	private void messageOut(String message)
	{
		try {
			// Write the message to whatever output we were provided
			out.write(message);
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
