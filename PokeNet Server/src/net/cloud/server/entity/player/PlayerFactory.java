package net.cloud.server.entity.player;

import net.cloud.server.entity.player.LoginState;
import net.cloud.server.entity.player.Player;
import net.cloud.server.entity.player.save.PlayerSaveException;
import net.cloud.server.file.FileServer;
import net.cloud.server.file.address.FileAddressBuilder;
import net.cloud.server.logging.Logger;
import net.cloud.server.nio.packet.PacketSender;
import net.cloud.server.util.StringUtil;

/**
 * Static factory. <br>
 * Static methods for the various use cases for creating player objects. 
 * A centralized location for all the different ways players are made. 
 */
public class PlayerFactory {
	
	/**
	 * Create a Player object for usage after a connection has been made. 
	 * The player can be placed in the world map, and is pretty much a blank slate.
	 * @param packetSender The PacketSender object the Player will use
	 * @return A new Player object with minimal details
	 */
	public static Player createOnNewConnection(PacketSender packetSender)
	{
		Player newPlayer = new Player(packetSender);
		
		// It's a new connection, so they start out connected in this situation
		newPlayer.setLoginState(LoginState.CONNECTED);
		
		return newPlayer;
	}
	
	/**
	 * Attempt to create a new player account. Will fail if the account already 
	 * exists, of course. May also fail if the process in general cannot complete. 
	 * A new player is created and then their data is saved to file. It will not treat 
	 * it like the player has logged in, so the player cannot be interacted with. 
	 * @param username The username the player wants
	 * @param password The password that will be set to the account
	 * @return A result with a message that may be used if desired
	 */
	public static AccountCreationResult createNewAccount(String username, String password)
	{
		// Validate the information first, so nothing bad slips past
		if(!StringUtil.isAlphaNumeric(username))
		{
			return AccountCreationResult.INVALID_USER;
		}
		if(!StringUtil.isAlphaNumericSpecial(password))
		{
			return AccountCreationResult.INVALID_PASS;
		}
		
		// Make sure the account doesn't already exist
		if(accountExists(username))
		{
			return AccountCreationResult.EXISTS;
		}
		
		// Okay, create a player object so we have some data to work with
		Player newPlayer = createNewPlayer(username, password);
		
		// That data needs to be saved to file. 
		newPlayer.finishedLoading();
		try {
			newPlayer.saveToFile();
		} catch (PlayerSaveException e) {
			// Rather than throw the exception (complicating the usage of this method) return another enum value
			Logger.instance().logException("Could not save data for new player account", e);
			return AccountCreationResult.SAVE_ERROR;
		}
		
		// Well, we got to the end. So return a success value.
		return AccountCreationResult.SUCCESS;
	}
	
	/**
	 * @param username Username of the prospective account
	 * @return True if the account name is taken
	 */
	private static boolean accountExists(String username)
	{
		// Not per say a formal request to the file server. But we maintain going through its file space. 
		return FileServer.instance().fileExists(FileAddressBuilder.createPlayerDataAddress(username));
	}
	
	/**
	 * Create a new Player object with default data state. It will not have any constructs 
	 * for sending data, and is just intended for creating a new account. To create and save a 
	 * new account, use <code>createNewAccount(...)</code>
	 * @param username Username
	 * @param password Password
	 * @return A new, fresh player object
	 */
	public static Player createNewPlayer(String username, String password)
	{
		Player newPlayer = new Player(null);
		
		newPlayer.setLoginState(LoginState.INITIAL);
		
		newPlayer.setUsername(username);
		newPlayer.setPassword(password);
		
		return newPlayer;
	}
	
	/**
	 * Create a lightweight player object for when save data is being updated to the latest format. 
	 * Only has a username set, nothing else - so that the data can be loaded from this. 
	 * @param username I.e. the file name
	 * @return A player object to load data into 
	 */
	public static Player createPlayerForDataUpdate(String username)
	{
		Player p = new Player(null);
		
		p.setUsername(username);
		
		return p;
	}

}
