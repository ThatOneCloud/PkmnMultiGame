package net.cloud.server.event.command.argument;

import net.cloud.server.event.command.CommandException;

/**
 * This is a pretty cool argument. It will parse an actual enum value from a generic enum type. 
 * You cannot use a catch-all prototype, a different prototype is needed for each enum type. 
 * Given the enum class, it will find which enum value the argument is for. The name must match exactly, it is even 
 * case sensitive.
 * Really, I can't believe this actually works. Worth the time it took to hammer out.
 * 
 * @param <E> The enum type
 */
public class EnumArgument<E extends Enum<E>> extends CommandArgument<Enum<E>> {
	
	/** We need a concrete class object to look up a value from */
	private Class<E> enumType;
	
	/**
	 * Consider this the prototype constructor. Commands may need to use it themselves to create it with the proper generic type.
	 * @param enumType The class object of the enum class
	 */
	public EnumArgument(Class<E> enumType)
	{
		super();
		
		this.enumType = enumType;
	}
	
	/**
	 * The parse constructor. The argument will contain the given value from the given enum type
	 * @param enumType The class object of the enum class
	 * @param value The enum value itself
	 */
	public EnumArgument(Class<E> enumType, E value)
	{
		super(value);
		
		this.enumType = enumType;
	}

	/**
	 * Parses the value string into an enum constant belonging to the enum class the prototype was built on. 
	 * The value string is case sensitive.
	 */
	@Override
	public CommandArgument<Enum<E>> newParsedInstance(String valueString) throws CommandException
	{
		// This shouldn't happen but anyways, it offers a better error message
		if(!enumType.isEnum())
		{
			throw new CommandException("Argument is non-functional");
		}
		
		try {
			// Cool method, case sensitive, takes in the class so we don't need a concrete enum object to look-up from
			E value = Enum.valueOf(enumType, valueString);
			
			// Get back a parsed instance with the parsed value
			return new EnumArgument<>(enumType, value);
		} catch(Exception e) {
			// Plenty of reasons look-up may not work, most likely there is no enum constant or the name was typed wrong
			throw new CommandException("Unknown identifier");
		}
	}

}
