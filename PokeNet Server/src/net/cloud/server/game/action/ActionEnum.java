package net.cloud.server.game.action;

/**
 * An interface describing the methods that the identifier enums for actions need to have.
 * It's pretty cool that Java allows us to implement an interface in an enum
 */
public interface ActionEnum {
	
	/**
	 * Essentially, a filename.
	 * @return The canonical name
	 */
	public String getCanonicalName();

}
