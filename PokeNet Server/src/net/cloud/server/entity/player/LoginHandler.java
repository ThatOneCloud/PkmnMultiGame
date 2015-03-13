package net.cloud.server.entity.player;

import java.io.IOException;

import net.cloud.server.entity.player.save.PlayerLoadException;
import net.cloud.server.entity.player.save.PlayerLoadHandler;
import net.cloud.server.game.World;
import net.cloud.server.logging.Logger;
import net.cloud.server.util.HashObj;

/**
 * Plain old static handler class.  Encapsulates a good chunk of the code the server uses 
 * for the login process. 
 */
public class LoginHandler {
	
	/** How long will we wait for the client to follow up before giving up */
	public static final long TIMEOUT = 5000;
	
	/**
	 * Determine what the appropriate response is for when someone is trying to login with the given 
	 * username and password. Checks to make sure the account exists, the password matches, and that the 
	 * account is not already logged in.
	 * @param player The player object that is going to be logged into
	 * @param username The supposed username
	 * @param password The supposed password
	 * @return A LoginResponse suitable to whatever conditions are or are not met
	 */
	public static LoginResponse validationResponseFor(Player player, String username, HashObj password)
	{
		// To check the password, we need to pull data into the player object
		PlayerLoadHandler loader = new PlayerLoadHandler(player);
		try {
			loader.loadUserAndPass(username);
		} catch (PlayerLoadException | IOException e) {
			// Well, either the account didn't exist or some file error occurred. We sort of mask the underlying issue
			return LoginResponse.INVALID_CREDENTIALS;
		}
		
		// Now we have data in the player object. Do the password from the data and given password match?
		if(!player.getPassword().equivalentTo(password))
		{
			// They do not - so of course, wrong information
			return LoginResponse.INVALID_CREDENTIALS;
		}
		
		// So far so good, but is the account already logged in?
		if(World.instance().getPlayerMap().hasMatchingPlayer((p) -> p.getUsername().equalsIgnoreCase(username)))
		{
			// A player already in the world has the same username
			return LoginResponse.ALREADY_LOGGED_IN;
		}
		
		// All of our checks have passed
		return LoginResponse.OKAY;
	}
	
	/**
	 * Aborts the connection the player is using. Consider this a forceful disconnect, there is 
	 * not attempt to save the player data. Useful for when the login process fails.
	 * @param player The player to terminate
	 */
	public static void abortConnection(Player player)
	{
		// We'll remove the player from the global list immediately
		World.instance().getPlayerMap().remove(player.getPacketSender().channel());
		
		// And then disconnect the channel that player was connected on
		try {
			player.getPacketSender().channel().close().sync();
		} catch (InterruptedException e) {
			// There's not much we can do if the channel does not close, except shout about it
			Logger.instance().logException("Could not close channel while aborting newly connected player.", e);
		}
	}

}
