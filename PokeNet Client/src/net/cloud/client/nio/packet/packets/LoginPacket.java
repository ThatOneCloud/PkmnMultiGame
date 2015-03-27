package net.cloud.client.nio.packet.packets;

import io.netty.buffer.ByteBuf;
import net.cloud.client.Client;
import net.cloud.client.entity.player.LoginHandler;
import net.cloud.client.entity.player.LoginState;
import net.cloud.client.entity.player.Player;
import net.cloud.client.logging.Logger;
import net.cloud.client.nio.bufferable.BufferableException;
import net.cloud.client.nio.packet.Packet;
import net.cloud.client.nio.packet.PacketConstants;
import net.cloud.client.nio.packet.ReceiveOnlyPacket;
import net.cloud.client.nio.packet.SendOnlyPacket;
import net.cloud.client.util.HashObj;
import net.cloud.client.util.StringUtil;
import net.cloud.client.entity.player.LoginResponse;
import net.cloud.client.event.task.TaskEngine;
import net.cloud.client.game.World;

/**
 * Deals with the login process. 
 * Sends username/password to the server - 
 * which then responds with whether or not the credentials were valid. 
 */
public class LoginPacket extends SendOnlyPacket {
	
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
	public void encode(ByteBuf buffer) throws BufferableException
	{
		// Place the username and password into the buffer
		StringUtil.writeStringToBuffer(username, buffer);
		
		password.save(buffer);
	}
	
	
	/**
	 * This packet is received by the client after we request to login. 
	 * This is the server's response to that request.
	 */
	public static class LoginResponsePacket extends ReceiveOnlyPacket {
		
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
		public Packet decode(ByteBuf data) throws BufferableException
		{
			// The response was encoded as its ordinal
			return new LoginResponsePacket(LoginResponse.values()[data.readInt()]);
		}

		/**
		 * Takes action according to the server's response. 
		 * If we cannot login, then a message is shown and the connection is dropped. 
		 * If we can, then we'll follow up by requesting login data.
		 */
		@Override
		public void handlePacket(Player player)
		{
			// We expect to be in the CONNECTED state
			if(player.getLoginState() != LoginState.CONNECTED)
			{
				fail(player, "Invalid username and/or password");
				Logger.instance().logMessage("Not in CONNECTED state while receiving login response");
				return;
			}
			
			switch(response)
			{
			
			case INVALID_CREDENTIALS:
				fail(player, "Invalid username and/or password");
				break;
				
			case ALREADY_LOGGED_IN:
				fail(player, "That account is already logged in");
				break;
				
			case BAD_DATA:
				fail(player, "Your account data could not be loaded");
				break;
				
			case OKAY:
				succeed(player, "Logging in. Please wait...");
				break;
				
			case RECONNECT:
				succeed(player, "Reconnecting. Please wait...");
				break;
				
			default:
				fail(player, "Unknown response from server.");
				
				// We're also going to log this, it is unintended that we reach here
				Logger.instance().logMessage("Login response unknown: " + response.name());
				break;
				
			}
		}
		
		/**
		 * Failure response procedure.
		 * @param player The player
		 * @param message The message to show the player
		 */
		private void fail(Player player, String message)
		{
			// Mark login as a failed attempt
			player.setLoginState(LoginState.LOGIN_FAILED);
			
			// Disconnect (this prepares us for trying again, in a way)
			Client.instance().nettyClient().disconnect();
			
			// Show the player a message, letting them know what's going on
			LoginHandler.message(message);
		}
		
		/**
		 * The response is that we're good to go and should request login data
		 * @param player The player
		 */
		private void succeed(Player player, String message)
		{
			// Mark that we've successfully been verified, our credentials were correct
			player.setLoginState(LoginState.VERIFIED);
			
			// Show the player a message to keep them updated
			LoginHandler.message(message);
			
			// Next we need data from the server, lots of it to initialize with
			player.getPacketSender().sendLoginDataRequest();
			
			// As before, we're going to time out if we don't get a response
			TaskEngine.instance().submitDelayed(LoginHandler.TIMEOUT, () ->
			{
				// This is the body of the task. If we've moved past verified, this task does not need to take action
				if(World.instance().getPlayer().getLoginState() == LoginState.VERIFIED)
				{
					fail(player, "Timeout: No response from server");
				}
			});
		}
		
	}
	
	
	/**
	 * A request we will send to the server once our login information has been verified. 
	 * This will request that the server send us all of the data we need to get in game.
	 * It's more of a symbolic packet, in that it has no contents.
	 */
	public static class LoginDataRequestPacket extends SendOnlyPacket {
		
		/** Prototype constructor */
		public LoginDataRequestPacket() {}

		@Override
		public short getOpcode()
		{
			return PacketConstants.LOGIN_DATA_REQUEST;
		}

		@Override
		public void encode(ByteBuf buffer) throws BufferableException
		{
			// We got nothing. Do a dummy write
			buffer.writeInt(-1);
		}
		
	}
	
	
	/**
	 * We've gotten the login data we need! This is the last packet in the login process.
	 */
	public static class LoginDataResponsePacket extends ReceiveOnlyPacket {
		
		/** The player that is logging in. We're going to send them all of their data */
		private Player player;
		
		// In the future, expect additions here. There will be map data and more to send as well.
		
		/** Prototype constructor */
		public LoginDataResponsePacket() {}

		@Override
		public short getOpcode()
		{
			return PacketConstants.LOGIN_DATA_RESPONSE;
		}

		@Override
		public Packet decode(ByteBuf data) throws BufferableException
		{
			LoginDataResponsePacket packet = new LoginDataResponsePacket();
			
			// For locality, we'll bring in the world player
			this.player = World.instance().getPlayer();
			
			// Read in the player data
			player.restore(data);
			
			return packet;
		}

		@Override
		public void handlePacket(Player player)
		{
			// We expect to be in the VERIFIED state before this
			if(player.getLoginState() != LoginState.VERIFIED)
			{
				Logger.instance().logMessage("Not in VERIFIED state when receiving login data response");
				return;
			}
			
			// The player was loaded during decode. But this is like saying we're logged in, now
			player.setLoginState(LoginState.LOGGED_IN);
			
			// It may not be visible for long, but show a message anyways
			LoginHandler.message("Login successful");
		}
		
	}

}
