package net.cloud.server.event.command.commands;

import java.io.StringWriter;

import net.cloud.server.event.command.Command;
import net.cloud.server.event.command.CommandScriptReader;
import net.cloud.server.event.command.argument.ArgumentPrototypes;
import net.cloud.server.event.command.parameter.OptionalParameter;
import net.cloud.server.event.command.parameter.RequiredParameter;
import net.cloud.server.util.IOUtil;

/**
 * A command which will execute a command script. 
 * It has one required parameter - the name of the script
 */
public class CmdScriptCommand extends AbstractCommand {
	
	/** Only one parameter - the name of the script */
	private static RequiredParameter<?>[] allReqParams =
	{
		new RequiredParameter<String>(ArgumentPrototypes.STRING)
	};

	/** Create an empty CmdScriptCommand */
	public CmdScriptCommand()
	{
		super();
	}
	
	@Override
	public Command newPrototypedInstance()
	{
		return new CmdScriptCommand();
	}

	/**
	 * Run the command script. 
	 * @return The results of running the script
	 */
	@Override
	public String doCommand()
	{
		// A StringWriter collects what is written to it into a String. Handy for getting results
		StringWriter results = new StringWriter();
		
		// We know that the first required parameter is there and will be the name of the script as a String
		String scriptName = super.<String>getReqParam(0).getArgValue();
		
		// Use a CommandScriptReader to read the file and run the commands		
		new CommandScriptReader(scriptName, IOUtil.writerToString(results)).readCommands();
		
		// Thanks to the StringWriter, our results are nice and available
		return results.toString();
	}

	@Override
	protected OptionalParameter<?>[] getAllOptionalParameters()
	{
		return null;
	}

	@Override
	protected RequiredParameter<?>[] getAllRequiredParameters()
	{
		return allReqParams;
	}

}
