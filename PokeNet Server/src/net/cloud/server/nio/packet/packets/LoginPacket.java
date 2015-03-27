package net.cloud.server.nio.packet.packets;

import io.netty.buffer.ByteBuf;
import net.cloud.server.entity.player.LoginHandler;
import net.cloud.server.entity.player.LoginResponse;
import net.cloud.server.entity.player.LoginState;
import net.cloud.server.entity.player.Player;
import net.cloud.server.entity.player.PlayerChannelConfig;
import net.cloud.server.event.task.TaskEngine;
import net.cloud.server.game.World;
import net.cloud.server.logging.Logger;
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
	
	/** 
	 * Create a LoginPacket which contains the given login credentials
	 * @param username Username of the player
	 * @param password The player's password
	 */
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
		
		// Okay, just now logging in
		if(response == LoginResponse.OKAY)
		{
			normalLogin(player);
		}
		// Okay, reconnecting
		else if(response == LoginResponse.RECONNECT)
		{
			reconnectLogin(player);
		}
		// Wait, what? I don't even
		else {
			negativeResponse(player, response);
		}
	}

	/**
	 * Tell the player they can login, timing out on their movement forward in the login process
	 * @param player The player trying to login
	 */
	private void normalLogin(Player player)
	{
		// Move the player's login state up
		player.setLoginState(LoginState.VERIFIED);

		// Reply to the client telling them they should proceed with login
		player.getPacketSender().sendLoginResponse(LoginResponse.OKAY);

		// We expect that soon the client will request login data. Time out on that action
		TaskEngine.instance().submitDelayed(LoginHandler.TIMEOUT, () ->
		{
			// Body of the task. Is the player still sitting in the VERIFIED state?
			if(player.getLoginState() == LoginState.VERIFIED)
			{
				// Flag login as failed
				player.setLoginState(LoginState.LOGIN_FAILED);

				// Since they are, we never got a login data request from the client
				LoginHandler.abortConnection(player);
			}
		});
	}
	
	/**
	 * Tell the player they can reconnect, timing out on their movement forward in the reconnecting process
	 * @param player The player trying to reconnect
	 */
	private void reconnectLogin(Player player)
	{
		// The old player object is still in the reconnect map - get and remove
		Player oldPlayer = World.instance().getDisconnectMap().remove(player.getUsername());
		
		// Login state of the old player was disconnected, but they're trying to reconnect now
		oldPlayer.setLoginState(LoginState.RECONNECTING);
		
		// We have two player objects - old and new - we want to pick up using the old one (it's easier to track the new one at this point)
		// So our aim is to abandon the new object and replace it with the old one everywhere it already exists
		oldPlayer.setPacketSender(player.getPacketSender());
		PlayerChannelConfig config = player.getChannelConfig();
		
		// Change the disconnect listener to use the old player object
		player.getPacketSender().channel().closeFuture().removeListener(config.getDcListener());
		config.setDcListener((f) -> LoginHandler.handleDisconnect(oldPlayer));
		player.getPacketSender().channel().closeFuture().addListener(config.getDcListener());
		
		// Cancel the existing timeout task, the new player will not be proceeding. We have a different timeout
		config.getConnectTimeoutTask().cancel();
		
		// Change the packet handler to use the old player, to correctly route packets
		config.getPacketHandler().setPlayer(oldPlayer);
		
		// Just like with packet sender, we'll move the config over to the old player, since it has new connection information
		oldPlayer.setChannelConfig(config);
		
		// Tell the client to proceed reconnecting
		oldPlayer.getPacketSender().sendLoginResponse(LoginResponse.RECONNECT);
		TaskEngine.instance().submitDelayed(LoginHandler.TIMEOUT, () ->
		{
				LoginHandler.reconnectingTimeoutTask(oldPlayer);
		});
	}
	
	/**
	 * Tell the player they cannot login, and terminate the connection
	 * @param player The player attempting to login
	 * @param response The response to send
	 */
	private void negativeResponse(Player player, LoginResponse response)
	{
		// Flag login as failed
		player.setLoginState(LoginState.LOGIN_FAILED);

		// Reply to the client telling them not to proceed
		player.getPacketSender().sendLoginResponse(response);

		// Sever the connection, as login has failed
		LoginHandler.abortConnection(player);
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
			LoginState prevState = player.getLoginState();
			
			// Make sure the state transition is okay
			if(prevState != LoginState.VERIFIED && prevState != LoginState.RECONNECTING)
			{
				Logger.instance().logMessage("[NOTICE] Player in invalid state requesting data: " + prevState.toString());
			}
			
			// Getting this request signifies the player finally being totally logged in
			player.setLoginState(LoginState.LOGGED_IN);
			
			// Place the player in the world, now that we consider them logged in
			World.instance().getPlayerMap().place(player.getPacketSender().channel(), player);
			
			// Split decision based on state they came in with
			if(prevState == LoginState.VERIFIED)
			{
				handleNormalLogin(player);
			}
			else if(prevState == LoginState.RECONNECTING)
			{
				handleReconnect(player);
			}
			
			// Update tracking field after we've formed the message
			player.updateLastLogin();
		}
		
		/**
		 * Respond to a data request during normal login conditions
		 * @param player The player that is requesting data
		 */
		private void handleNormalLogin(Player player)
		{
			// TODO: combine into composite packet
			player.getPacketSender().sendLoginDataResponse(player);
			
			String loginMsg = player.getLastLogin() == null ? "login success" : "login success."+System.lineSeparator()+"last login: "+player.getLastLogin().toString();
			player.getPacketSender().sendShowMessageDialog("login", loginMsg);
		}
		
		/**
		 * Respond to a data request for a player that is in the process of reconnecting
		 * @param player The player that is requesting data
		 */
		private void handleReconnect(Player player)
		{
			player.getPacketSender().sendLoginDataResponse(player);
			
			String loginMsg = "reconnect success."+System.lineSeparator()+"last login: "+player.getLastLogin().toString();
			player.getPacketSender().sendShowMessageDialog("reconnect", loginMsg);
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
