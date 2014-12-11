package net.cloud.mmo.entity.player;

import net.cloud.mmo.entity.Entity;
import net.cloud.mmo.nio.packet.PacketSender;

public class Player extends Entity {
	
	public static final String USER = "username";
	public static final String PASS = "1234";
	
	/** Describes the player's connectivity to the game */
	private LoginState loginState;
	
	/**
	 * The PacketSender assigned to this player. Used for communication.
	 * Only valid for the World Player, since player to player communication won't directly happen.
	 */
	private PacketSender packetSender;
	
	/**
	 * Constructor that accepts the PacketSender, for the World Player. 
	 * @param packetSender PacketSender that will be used throughout lifetime of player
	 */
	public Player(PacketSender packetSender)
	{
		this.packetSender = packetSender;
	}
	
	public String getUsername()
	{
		return USER;
	}
	public String getPassword()
	{
		return PASS;
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
	 * Get the PacketSender the World Player uses to communicate with the server.
	 * Players other than the World Player will not have a packetSender
	 * @return The PacketSender allowing communication to the server
	 */
	public PacketSender getPacketSender()
	{
		return packetSender;
	}

}
