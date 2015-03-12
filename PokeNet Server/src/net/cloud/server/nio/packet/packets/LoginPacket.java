package net.cloud.server.nio.packet.packets;

import io.netty.buffer.ByteBuf;
import net.cloud.server.entity.player.LoginState;
import net.cloud.server.entity.player.Player;
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
	
	/** Possible values a response to a login request may have */
	public enum LoginResponse {VALID, INVALID_USERNAME, INVALID_PASSWORD};
	
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
		return PacketConstants.LOGIN_PACKET;
	}

	@Override
	public Packet decode(ByteBuf data) throws BufferableException
	{
System.out.println("decoding login packet");
		// Pull the username & password from the buffer
		String user = StringUtil.getFromBuffer(data);
		HashObj pass = HashObj.createFrom(data);
		
		// Return a packet with that new information contained in it
		return new LoginPacket(user, pass);
	}

	@Override
	public void handlePacket(Player player)
	{
		// TODO: Remove this short-circuit
		System.out.println("Login Packet, user: " + username);
		System.out.println("Login Packet, pass: " + password);
		int i = 1;
		if(i < 2)
		{
			return;
		}
		
		// We need to verify the correctness of the user & pass.
		// First - check for a player with the given username
		// TODO: Well.. without actual player saving.. can't do this.
		
		
		// player.setUsername(username) would normally be done here (and other data or whatever)
		
		
		// Now that we've found the requested player & loaded login data, compare the passwords
		// TODO: Use a hash of the password, stored that way and all
		if(player.getPassword().equals(password))
		{
			// The password matched! Tell them they're good to go
			player.setLoginState(LoginState.VERIFIED);
			player.getPacketSender().sendCompositePacket(player.getPacketSender().createLoginReponse(LoginResponse.VALID));
		}
		else {
			// Uh-oh, wrong password. Send back a packet letting them know
			player.getPacketSender().sendLoginReponse(LoginResponse.INVALID_PASSWORD);
		}
	}
	
	/** A packet for the response to a login request - telling if it was valid or not */
	public static class LoginResponsePacket extends SendOnlyPacket {
		
		/** The response we're going to send */
		private LoginResponse response;
		
		/** Default constructor leaves all data fields default or null */
		public LoginResponsePacket() {}
		
		/**
		 * @param response What we'll tell the server about their login request
		 */
		public LoginResponsePacket(LoginResponse response)
		{
			this.response = response;
		}

		@Override
		public short getOpcode()
		{
			return PacketConstants.LOGIN_RESPONSE_PACKET;
		}

		@Override
		public void encode(ByteBuf buffer)
		{
			// Put the response in the buffer. (via its index - I know, dangerous-ish)
			buffer.writeInt(response.ordinal());
		}
		
	}

}
