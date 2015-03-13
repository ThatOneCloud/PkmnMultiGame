package net.cloud.client.nio.packet.packets;

import io.netty.buffer.ByteBuf;
import net.cloud.client.entity.player.Player;
import net.cloud.client.nio.bufferable.BufferableException;
import net.cloud.client.nio.packet.Packet;
import net.cloud.client.nio.packet.PacketConstants;
import net.cloud.client.nio.packet.ReceiveOnlyPacket;
import net.cloud.client.nio.packet.SendOnlyPacket;
import net.cloud.client.util.HashObj;
import net.cloud.client.util.StringUtil;
import net.cloud.client.entity.player.LoginResponse;

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
			// TODO Actual implementation
			System.out.println("got login response: " + response.name());
		}
		
	}

}
