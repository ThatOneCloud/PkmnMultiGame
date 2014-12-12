package net.cloud.mmo.nio.packet;

import net.cloud.mmo.entity.player.Player;
import net.cloud.mmo.game.World;
import net.cloud.mmo.nio.packet.packets.*;

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
	public Packet createTestPacket(int value)
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
	 */
	public Packet createCompositePacket(Packet first, Packet... others)
	{
		return new CompositePacket(first, others);
	}
	
	/**
	 * Create a LoginPacket, requesting the server validate our login information. 
	 * Uses the credentials from the World Player. 
	 */
	public Packet createLogin()
	{
		Player p = World.getInstance().getPlayer();
		LoginPacket loginPacket = new LoginPacket(p.getUsername(), p.getPassword());
		
		return loginPacket;
	}

}
