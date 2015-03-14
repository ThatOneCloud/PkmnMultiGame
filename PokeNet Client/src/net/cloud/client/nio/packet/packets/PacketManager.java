package net.cloud.client.nio.packet.packets;

import io.netty.buffer.ByteBuf;
import net.cloud.client.nio.bufferable.BufferableException;
import net.cloud.client.nio.packet.Packet;
import net.cloud.client.nio.packet.PacketConstants;
import net.cloud.client.nio.packet.packets.LoginPacket.LoginResponsePacket;
import net.cloud.client.nio.packet.packets.LoginPacket.LoginDataRequestPacket;
import net.cloud.client.nio.packet.packets.LoginPacket.LoginDataResponsePacket;
import net.cloud.client.nio.packet.packets.ShowMessageDialogPacket;

/**
 * In short, keeps a record of the different packets. 
 * More specifically, has a default instance of each packet. 
 * From there, a more detailed packet can be constructed from a ByteBuf. 
 * Handing off creation to a Packet instance is essentially a means of using dynamic binding.
 */
public class PacketManager {
	
	/** An array which holds default objects for each Packet implementor */
	private static final Packet[] packets = new Packet[PacketConstants.NUM_PACKETS];
	
	// Static initializer block, to instantiate each default packet
	static
	{
		packets[PacketConstants.TEST] = new TestPacket();
		packets[PacketConstants.COMPOSITE] = new CompositePacket();
		packets[PacketConstants.LOGIN] = new LoginPacket();
		packets[PacketConstants.LOGIN_RESPONSE] = new LoginResponsePacket();
		packets[PacketConstants.LOGIN_DATA_REQUEST] = new LoginDataRequestPacket();
		packets[PacketConstants.LOGIN_DATA_RESPONSE] = new LoginDataResponsePacket();
		packets[PacketConstants.SHOW_MSG_DIALOG] = new ShowMessageDialogPacket();
	}
	
	/**
	 * Returns a new Packet, which aligns with the given opcode.  The Packet 
	 * uses the data provided to reconstruct itself.
	 * @param opcode The 2 byte opcode of the packet
	 * @param data The rest of the data (excluding length and opcode header)
	 * @return A Packet of the right class, deserialized from the data
	 * @throws BufferableException There was an issue decoding a Bufferable object
	 * @throws IllegalArgumentException The packet opcode is unknown
	 */
	public static Packet decodeCopy(short opcode, ByteBuf data) throws IllegalArgumentException, BufferableException
	{
		// Make sure the opcode is valid
		if(opcode < 0 || opcode >= packets.length) {
			throw new IllegalArgumentException("Packet opcode out of range: " + opcode);
		}
		
		// Get a decoded packet using the existing prototype
		if(packets[opcode] != null)
		{
			return packets[opcode].decode(data);
		}
		else {
			throw new IllegalArgumentException("Packet " + opcode + " does not exist");
		}
		
		
	}

}
