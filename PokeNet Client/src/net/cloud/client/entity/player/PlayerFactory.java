package net.cloud.client.entity.player;

import net.cloud.client.nio.packet.PacketSender;

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
		return new Player(packetSender);
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
		newPlayer.setUsername(username);
		newPlayer.setPassword(password);
		
		return newPlayer;
	}

}
