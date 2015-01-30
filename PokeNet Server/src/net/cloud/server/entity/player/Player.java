package net.cloud.server.entity.player;

import java.util.Optional;

import io.netty.buffer.ByteBuf;
import net.cloud.server.entity.Entity;
import net.cloud.server.entity.player.save.PlayerSaveException;
import net.cloud.server.entity.player.save.PlayerSaveHandler;
import net.cloud.server.nio.bufferable.Bufferable;
import net.cloud.server.nio.bufferable.BufferableException;
import net.cloud.server.nio.packet.PacketSender;
import net.cloud.server.util.StringUtil;

public class Player extends Entity implements Bufferable {
	
	/** Describes the player's connectivity to the game */
	private LoginState loginState;
	
	/** The PacketSender assigned to this player. Used for communication. */
	private PacketSender packetSender;
	
	/** This player's save handler. They should hold onto it, it may save them one day... */
	private Optional<PlayerSaveHandler> saveHandler;
	
	/** The player's username. */
	private String username;
	
	/** The player's password. */
	private String password;
	
	/**
	 * Constructor that accepts the PacketSender
	 * @param packetSender PacketSender that will be used throughout lifetime of player
	 */
	public Player(PacketSender packetSender)
	{
		// TODO: remove. only a temporary solution until account creation implemented. 
		username = "username";
		password = "password";
		
		this.packetSender = packetSender;
		
		// They start off as a new player - only connected
		setLoginState(LoginState.CONNECTED);
		
		// Don't start with save handler. Only have that once the player has been loaded
		saveHandler = Optional.empty();
	}
	
	/**
	 * @return The player's username
	 */
	public String getUsername()
	{
		return username;
	}
	
	/**
	 * @return The player's password
	 */
	public String getPassword()
	{
		return password;
	}
	
	/**
	 * @return The LoginState for this player. Ie, which step of login process they are in
	 */
	public LoginState getLoginState() {
		return loginState;
	}

	/**
	 * @param loginState Which step of the login process the player is now in
	 */
	public void setLoginState(LoginState loginState) {
		this.loginState = loginState;
	}
	
	/**
	 * Get the PacketSender the server uses to communicate with the player
	 * @return The PacketSender allowing communication to the player
	 */
	public PacketSender getPacketSender()
	{
		return packetSender;
	}
	
	/**
	 * Tell this player that its data has been loaded and it's time for it to be able to save data
	 */
	public void finishedLoading()
	{
		// So this is the time to make a save handler. Since we now have data to save.
		saveHandler = Optional.of(new PlayerSaveHandler(this));
	}
	
	/**
	 * Save this player's data to file. Serializing data is done on the calling thread, writing to the file 
	 * is done on the File Server. 
	 * @throws PlayerSaveException If the data could not be saved. Not thrown if file could not be written. 
	 */
	public void saveToFile() throws PlayerSaveException
	{
		// Save handler existing means it's okay to try saving. 
		if(saveHandler.isPresent())
		{
			saveHandler.get().saveToFile();
		}
	}

	@Override
	public void save(ByteBuf buffer) throws BufferableException {
		// Well for now all we have is the fake username & password
		StringUtil.writeStringToBuffer(getUsername(), buffer);
		StringUtil.writeStringToBuffer(getPassword(), buffer);
	}

	@Override
	public void restore(ByteBuf buffer) throws BufferableException {
		// And for now, restore the user & pass just to verify it
		username = StringUtil.getFromBuffer(buffer);
		password = StringUtil.getFromBuffer(buffer);
	}

}
