package net.cloud.client.entity.player;

import io.netty.buffer.ByteBuf;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import net.cloud.client.entity.Entity;
import net.cloud.client.nio.packet.PacketSender;
import net.cloud.client.entity.player.LoginState;
import net.cloud.client.nio.bufferable.Bufferable;
import net.cloud.client.nio.bufferable.BufferableException;
import net.cloud.client.util.HashObj;
import net.cloud.client.util.StringUtil;

/**
 * The Player object. Home of the player. Holds the player's data. 
 * Keeps the player's data safe. A hub for all of the player's information. 
 * The root of the save data. Yes.
 */
@XStreamAlias("Player")
public class Player extends Entity implements Bufferable {
	
	/** Describes the player's connectivity to the game */
	private transient LoginState loginState;
	
	/**
	 * The PacketSender assigned to this player. Used for communication.
	 * Only valid for the World Player, since player to player communication won't directly happen.
	 */
	private transient PacketSender packetSender;
	
	/** The player's username. */
	private String username;
	
	/** The player's password, as a SHA-1 hash. */
	private HashObj password;
	
	/**
	 * Constructor that accepts the PacketSender, for the World Player. 
	 * @param packetSender PacketSender that will be used throughout lifetime of player
	 */
	public Player(PacketSender packetSender)
	{
		this.packetSender = packetSender;
	}
	
	/** @return The player's username */
	public String getUsername()
	{
		return username;
	}
	
	/** @param username the username to set */
	public void setUsername(String username) {
		this.username = username;
	}

	/** @return The player's password */
	public HashObj getPassword()
	{
		return password;
	}
	
	/** @param password the password to set */
	public void setPassword(String password) {
		this.password = new HashObj(password);
	}
	
	/** @return The LoginState for this player. Ie, which step of login process they are in */
	public LoginState getLoginState() {
		return loginState;
	}

	/** @param loginState Which step of the login process the player is now in */
	public void setLoginState(LoginState loginState) {
		this.loginState = loginState;
	}
	
	/**
	 * Get the PacketSender the World Player uses to communicate with the server.
	 * Players other than the World Player will not have a packetSender
	 * @return The PacketSender allowing communication to the server
	 */
	public PacketSender getPacketSender()
	{
		return packetSender;
	}
	
	/**
	 * Save all of the non-transient player data to the buffer
	 */
	@Override
	public void save(ByteBuf buffer) throws BufferableException
	{
		// Well for now all we have is the username & password
		StringUtil.writeStringToBuffer(getUsername(), buffer);
		
		password.save(buffer);
	}

	/**
	 * Restore all of the non-transient player data from the buffer
	 */
	@Override
	public void restore(ByteBuf buffer) throws BufferableException
	{
		// And for now, restore the user & pass just to verify it
		username = StringUtil.getFromBuffer(buffer);
		
		password = HashObj.createFrom(buffer);
	}

}
