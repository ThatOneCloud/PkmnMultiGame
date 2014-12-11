package net.cloud.mmo.nio.packet.packets;

import io.netty.buffer.ByteBuf;
import net.cloud.mmo.entity.player.LoginState;
import net.cloud.mmo.entity.player.Player;
import net.cloud.mmo.nio.packet.Packet;
import net.cloud.mmo.nio.packet.PacketConstants;
import net.cloud.mmo.nio.packet.ReceiveOnlyPacket;
import net.cloud.mmo.nio.packet.SendOnlyPacket;
import net.cloud.mmo.util.StringUtil;

/**
 * Deals with the login process. 
 * Sends username/password to the server - 
 * which then responds with whether or not the credentials were valid
 */
public class LoginPacket extends SendOnlyPacket {
	
	/** Possible values a response to a login request may have */
	private enum LoginResponse {VALID, INVALID_USERNAME, INVALID_PASSWORD};
	
	/** Username of the player trying to login */
	private String username;
	/** Password of the player trying to login */
	private String password;
	
	/** Default constructor leaves all data fields default or null */
	public LoginPacket() {}
	
	/** Create a LoginPacket which contains the given login credentials */
	public LoginPacket(String username, String password) {
		this.username = username;
		this.password = password;
	}

	@Override
	public short getOpcode() {
		return PacketConstants.LOGIN_PACKET;
	}

	@Override
	public void encode(ByteBuf buffer) {
		// Place the username and password into the buffer
		StringUtil.writeStringToBuffer(username, buffer);
		StringUtil.writeStringToBuffer(password, buffer);
	}

	public static class LoginResponsePacket extends ReceiveOnlyPacket {
		
		/** The response we're going to send */
		private LoginResponse response;
		
		/** Default constructor leaves all data fields default or null */
		public LoginResponsePacket() {}
		
		/**
		 * @param response What we'll tell the server about their login request
		 */
		public LoginResponsePacket(LoginResponse response) {
			this.response = response;
		}

		@Override
		public short getOpcode() {
			return PacketConstants.LOGIN_RESPONSE_PACKET;
		}

		@Override
		public Packet decode(ByteBuf data) {
			// The packet has a response in it - from the enum
			LoginResponse response = LoginResponse.values()[data.readInt()];
			
			return new LoginResponsePacket(response);
		}

		@Override
		public void handlePacket(Player player) {
			// TODO Take real action based on the response, but no GUI yet
			switch(response) {
			
			case VALID:
				// Yay, we can login!
				player.setLoginState(LoginState.VERIFIED);
				System.out.println("Got login validation from server");
				break;
				
			case INVALID_PASSWORD:
				System.out.println("Invalid password");
				break;
				
			case INVALID_USERNAME:
				System.out.println("Invalid username");
				break;
				
			}
		}
		
	}

}
