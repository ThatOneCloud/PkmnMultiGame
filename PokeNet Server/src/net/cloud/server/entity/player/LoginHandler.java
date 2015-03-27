package net.cloud.server.entity.player;

import java.io.IOException;

import net.cloud.server.entity.player.save.PlayerLoadException;
import net.cloud.server.entity.player.save.PlayerLoadHandler;
import net.cloud.server.entity.player.save.PlayerSaveException;
import net.cloud.server.event.task.TaskEngine;
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
	
	/** How long a player has to reconnect */
	public static final long RECONNECT_TIMEOUT = 10_000;
	
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
		
		// We're going to load data next. New connection and reconnect are a tad different.
		Player dcPlayer = World.instance().getDisconnectMap().get(username);
		if(dcPlayer != null)
		{
			// We don't want to take any action yet, the caller needs to be able to work with the players
			return LoginResponse.RECONNECT;
		}
		else {
			// We're going to need to load this player's data from file
			try {
				loader.loadFromFile();
				
				return LoginResponse.OKAY;
			} catch (Exception e) {
				// This is not a happy thing. The player's data couldn't be loaded. Worth a global notice
				Logger.instance().logException("Player load failed during login", e);
				return LoginResponse.BAD_DATA;
			}
		}
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
	
	/**
	 * Do the log out process for a player. This will actually run through the actions needed 
	 * to log a player out of the game
	 * @param player The player that is about to leave the game
	 */
	public static void doLogout(Player player)
	{
		// We should probably make sure the player is actually logged in
		if(player.getLoginState() != LoginState.LOGGED_IN)
		{
			return;
		}
		
		// So we're going to proceed. Tell the client to run its end of the logout procedure
		player.setLoginState(LoginState.LOGGING_OUT);
		
		// Once this packet actually sends, we'll resume the process
		player.getPacketSender().sendLogout(player, LoginHandler::resumeLogout);
	}
	
	/**
	 * Pick up where doLogout left off, wrapping up the server side end of logging out
	 * @param player The player that was just told to log out
	 */
	private static void resumeLogout(Player player)
	{
		// Close the channel, the packet has been sent
		player.getPacketSender().channel().close();
		
		// We'll move into the "trap" LoginState
		player.setLoginState(LoginState.LOGGED_OUT);
		
		// Save the data
		try {
			player.saveToFile();
		} catch (PlayerSaveException e) {
			// Darn, they're logging out but we can't assure data integrity. Sad day for this player.
			Logger.instance().logException("Player data not saved at logout", e);
		}
		
		// No need to have them in the world anymore. Make this come last so they can't log back in before this is complete
		World.instance().getPlayerMap().remove(player.getPacketSender().channel());
	}
	
	/**
	 * Should be called when the player disconnects from the game. 
	 * It's okay if they're gracefully logging out, this will do nothing, then.
	 * Takes care of what should happen if the player abruptly disconnects from the game.
	 * @param player The player that is no longer connected to the server
	 */
	public static void handleDisconnect(Player player)
	{
		// We have some checks to go through to make sure this is indeed an abrupt disconnect
		if(player.getLoginState() != LoginState.LOGGED_IN)
		{
			return;
		}
		
		// It's abrupt. Rip the player out of the world they're no longer in
		World.instance().getPlayerMap().remove(player.getPacketSender().channel());
		
		// Let the player do whatever it personally needs to
		player.onDisconnect();
		
		// Then move them over to storage for disconnected players
		World.instance().getDisconnectMap().place(player);
		
		// Start a timer, they only have so long to reconnect before it's a done deal
		TaskEngine.instance().submitDelayed(RECONNECT_TIMEOUT, () -> doReconnectFailed(player));
	}
	
	/**
	 * Called when the timer runs up on the task to reconnect. 
	 * If the player has reconnected, this will simply return. 
	 * If they have not, then the player is considered to have failed reconnect, and action is taken for that.
	 * @param player The player that disconnected
	 */
	public static void doReconnectFailed(Player player)
	{
		// Check to see that they are still disconnected
		if(player.getLoginState() != LoginState.DISCONNECTED)
		{
			return;
		}
		
		// Pull them out of the disconnect map
		World.instance().getDisconnectMap().remove(player.getUsername());
		
		// Tell the player they failed to reconnect
		player.onReconnectFailed();
		
		// We should probably save their data so it isn't just lost
		try {
			player.saveToFile();
		} catch (PlayerSaveException e) {
			Logger.instance().logException("Could not save after reconnect failed", e);
		}
	}
	
	/**
	 * Called when the time is up between the reconnecting and logged in transition. 
	 * If the player has moved out of the reconnecting state, this will simply return. 
	 * Otherwise, the player is considered to have a failed reconnect, and action is taken for that.
	 * @param player The player that tried to reconnect
	 */
	public static void reconnectingTimeoutTask(Player player)
	{
		// Check to see that they are indeed reconnecting
		if(player.getLoginState() != LoginState.RECONNECTING)
		{
			return;
		}

		// Tell the player they failed to reconnect
		player.onReconnectFailed();

		// We still need to save their data, the above method won't execute if this one does
		try {
			player.saveToFile();
		} catch (PlayerSaveException e) {
			Logger.instance().logException("Could not save after reconnect failed", e);
		}
	}

}
