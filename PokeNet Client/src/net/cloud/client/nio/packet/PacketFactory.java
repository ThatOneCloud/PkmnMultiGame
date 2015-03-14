package net.cloud.client.nio.packet;

import net.cloud.client.entity.player.Player;
import net.cloud.client.game.World;
import net.cloud.client.nio.packet.packets.*;
import net.cloud.client.nio.packet.packets.LoginPacket.LoginDataRequestPacket;

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
	 * @return A packet consisting of the other packets
	 */
	public CompositePacket createCompositePacket(Packet first, Packet... others)
	{
		return new CompositePacket(first, others);
	}
	
	/**
	 * Create a LoginPacket, requesting the server validate our login information. 
	 * Uses the credentials from the World Player. 
	 * @return A login packet
	 */
	public LoginPacket createLogin()
	{
		Player p = World.instance().getPlayer();
		LoginPacket loginPacket = new LoginPacket(p.getUsername(), p.getPassword());
		
		return loginPacket;
	}
	
	/**
	 * Create a login data request packet
	 * @return A login data request packet
	 */
	public LoginDataRequestPacket createLoginDataRequest()
	{
		return new LoginDataRequestPacket();
	}

}
