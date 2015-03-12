package net.cloud.server.event.command.commands;

import net.cloud.server.event.command.parameter.OptionalParameter;
import net.cloud.server.event.command.parameter.RequiredParameter;

/**
 * An abstract class that can be extended. 
 * A convenience of sorts for Commands which do not accept any parameters, 
 * so the getParameter methods simply return null and do not have to be overridden.
 */
public abstract class NoParameterCommand extends AbstractCommand {

	/**
	 * Since subclasses do not accept parameters, simply returns null
	 * @return null
	 */
	@Override
	protected OptionalParameter<?>[] getAllOptionalParameters()
	{
		return null;
	}

	/**
	 * Since subclasses do not accept parameters, simply returns null
	 * @return null
	 */
	@Override
	protected RequiredParameter<?>[] getAllRequiredParameters()
	{
		return null;
	}

}
