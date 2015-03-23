package net.cloud.server.nio.packet.packets;

import io.netty.buffer.ByteBuf;
import net.cloud.server.entity.player.LoginHandler;
import net.cloud.server.entity.player.LoginResponse;
import net.cloud.server.entity.player.LoginState;
import net.cloud.server.entity.player.Player;
import net.cloud.server.event.task.TaskEngine;
import net.cloud.server.game.World;
import net.cloud.server.nio.bufferable.BufferableException;
import net.cloud.server.nio.packet.Packet;
import net.cloud.server.nio.packet.PacketConstants;
import net.cloud.server.nio.packet.ReceiveOnlyPacket;
import net.cloud.server.nio.packet.SendOnlyPacket;
import net.cloud.server.util.HashObj;
import net.cloud.server.util.StringUtil;

/**
 * Deals with the login process. 
 * Receives username and password from the client. 
 * Then validates the login information, before responding back to the client. 
 */
public class LoginPacket extends ReceiveOnlyPacket {
	
	/** Username of the player trying to login */
	private String username;
	
	/** Password of the player trying to login */
	private HashObj password;
	
	/** Default constructor leaves all data fields default or null */
	public LoginPacket() {}
	
	/** Create a LoginPacket which contains the given login credentials */
	public LoginPacket(String username, HashObj password)
	{
		this.username = username;
		this.password = password;
	}

	@Override
	public short getOpcode()
	{
		return PacketConstants.LOGIN;
	}

	@Override
	public Packet decode(ByteBuf data) throws BufferableException
	{
		// Pull the username & password from the buffer
		String user = StringUtil.getFromBuffer(data);
		HashObj pass = HashObj.createFrom(data);
		
		// Return a packet with that new information contained in it
		return new LoginPacket(user, pass);
	}

	/**
	 * Takes the request to login, and determines what the correct response is. 
	 * Then replies by sending that response.
	 */
	@Override
	public void handlePacket(Player player)
	{
		// What is our response going to be?
		LoginResponse response = LoginHandler.validationResponseFor(player, username, password);
		
		if(response == LoginResponse.OKAY)
		{
			// Move the player's login state up
			player.setLoginState(LoginState.VERIFIED);
			
			// Reply to the client telling them they should proceed with login
			player.getPacketSender().sendLoginResponse(response);
			
			// We expect that soon the client will request login data. Time out on that action
			TaskEngine.getInstance().submitDelayed(LoginHandler.TIMEOUT, () ->
			{
				// Body of the task. Is the player still sitting in the VERIFIED state?
				if(player.getLoginState() == LoginState.VERIFIED)
				{
					// Since they are, we never got a login data request from the client
					LoginHandler.abortConnection(player);
				}
			});
		}
		else {
			// Flag login as failed
			player.setLoginState(LoginState.FAILED);
			
			// Reply to the client telling them not to proceed
			player.getPacketSender().sendLoginResponse(response);
			
			// Sever the connection, as login has failed
			LoginHandler.abortConnection(player);
		}
	}
	
	
	/**
	 * A packet which the server sends as a response to the LoginPacket (a response 
	 * to a client's request to login)
	 */
	public static class LoginResponsePacket extends SendOnlyPacket {
		
		/** The response we're going to send */
		private LoginResponse response;
		
		/** For prototype */
		public LoginResponsePacket() {}
		
		/**
		 * Create a new LoginResponsePacket that will send the given response
		 * @param response The response
		 */
		public LoginResponsePacket(LoginResponse response)
		{
			this.response = response;
		}

		@Override
		public short getOpcode()
		{
			return PacketConstants.LOGIN_RESPONSE;
		}

		@Override
		public void encode(ByteBuf buffer) throws BufferableException
		{
			// To keep it simple, we'll use the ordinal of the enum value this time
			buffer.writeInt(response.ordinal());
		}
		
	}
	
	
	/**
	 * A symbolic packet that the client sends us to indicate that we should now send the login data to it
	 */
	public static class LoginDataRequestPacket extends ReceiveOnlyPacket {

		/** Prototype constructor */
		public LoginDataRequestPacket() {}
		
		@Override
		public short getOpcode()
		{
			return PacketConstants.LOGIN_DATA_REQUEST;
		}

		@Override
		public Packet decode(ByteBuf data) throws BufferableException
		{
			// There's really nothing to read, but we have a dummy integer to burn
			data.readInt();
			
			return new LoginDataRequestPacket();
		}

		@Override
		public void handlePacket(Player player)
		{
			// Getting this request signifies the player finally being totally logged in
			player.setLoginState(LoginState.LOGGED_IN);
			
			// Place the player in the world, now that we consider them logged in
			World.instance().getPlayerMap().place(player.getPacketSender().channel(), player);
			
			// Reply back with the data
			// TODO: combine into composite packet
			String loginMsg = player.getLastLogin() == null ? "login success" : "login success."+System.lineSeparator()+"last login: "+player.getLastLogin().toString();
			player.getPacketSender().sendLoginDataResponse(player);
			player.getPacketSender().sendShowMessageDialog("login", loginMsg);
			
			// Update tracking field after we've formed the message
			player.updateLastLogin();
		}
		
	}
	
	
	/**
	 * A sizable packet. We send this to the client in the final stage of logging in, to send the client all of the 
	 * data that it needs on initialization to get the player in the game. 
	 */
	public static class LoginDataResponsePacket extends SendOnlyPacket {
		
		/** The player that is logging in. We're going to send them all of their data */
		private Player player;
		
		// In the future, expect additions here. There will be map data and more to send as well.
		
		/** Prototype constructor */
		public LoginDataResponsePacket() {}
		
		/**
		 * Create a login data response that will send the given data
		 * @param player The player that is about to login
		 */
		public LoginDataResponsePacket(Player player)
		{
			this.player = player;
		}

		@Override
		public short getOpcode()
		{
			return PacketConstants.LOGIN_DATA_RESPONSE;
		}

		@Override
		public void encode(ByteBuf buffer) throws BufferableException
		{
			// Pack the player data into the buffer (its data should already be loaded from file)
			player.save(buffer);
		}
		
	}

}
