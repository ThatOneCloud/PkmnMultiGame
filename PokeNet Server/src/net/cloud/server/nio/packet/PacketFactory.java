package net.cloud.server.nio.packet;

import net.cloud.server.entity.player.LoginResponse;
import net.cloud.server.entity.player.Player;
import net.cloud.server.nio.packet.packets.*;
import net.cloud.server.nio.packet.packets.LoginPacket.LoginResponsePacket;
import net.cloud.server.nio.packet.packets.LoginPacket.LoginDataResponsePacket;

/**
 * Typical factory class, meant to create Packets. 
 * The methods are not static - a factory object must be created for use. 
 * {@link PacketSender} will use this factory to create and write packets. 
 * PacketSender has methods to create, write, and send each packet. 
 * Direct usage of this factory is also possible, but not recommended to maintain design structure.
 */
public class PacketFactory {
	
	/**
	 * Creates a TestPacket, which is just an integer of the given value
	 */
	public TestPacket createTestPacket(int value)
	{
		return new TestPacket(value);
	}
	
	/**
	 * Creates a CompositePacket, a Packet made of other packets.
	 * (Possibly including another composite packet...)
	 * This is useful for ensuring multiple packets are sent together 
	 * and dealt with in a precise order.
	 * @param first The first packet that the composite will consist of
	 * @param others Any other packets the new packet will consist of
	 * @return A packet consisting of the given packets
	 */
	public CompositePacket createCompositePacket(Packet first, Packet... others)
	{
		return new CompositePacket(first, others);
	}
	
	/**
	 * Create a LoginResponsePacket with the given response. 
	 * @param response The response
	 * @return A packet to respond to a login request with
	 */
	public LoginResponsePacket createLoginResponsePacket(LoginResponse response)
	{
		return new LoginResponsePacket(response);
	}
	
	/**
	 * Create a LoginDataResponsePacket with the given data in it
	 * @param player The player that is logging in
	 * @return A packet to respond to a login data request with
	 */
	public LoginDataResponsePacket createLoginDataResponsePacket(Player player)
	{
		return new LoginDataResponsePacket(player);
	}
	
	/**
	 * Create a packet to show a modal message dialog
	 * @param title The title to put on the dialog's frame
	 * @param message The message to show
	 * @return A packet to show a modal message dialog with
	 */
	public ShowMessageDialogPacket createShowMessageDialogPacket(String title, String message)
	{
		return new ShowMessageDialogPacket(title, message);
	}
	
	/**
	 * Create a packet to make the client log out
	 * @return A packet to show a modal message dialog with
	 */
	public LogoutPacket createLogoutPacket()
	{
		return new LogoutPacket();
	}

}
