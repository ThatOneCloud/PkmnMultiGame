package net.cloud.mmo.nio.packet.packets;

import io.netty.buffer.ByteBuf;

import java.util.Optional;

import net.cloud.mmo.nio.packet.Packet;
import net.cloud.mmo.nio.packet.PacketConstants;

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
	static {
		packets[PacketConstants.TEST_PACKET] = new TestPacket();
	}
	
	/**
	 * Returns a new Packet, which aligns with the given opcode.  The Packet 
	 * uses the data provided to reconstruct itself.
	 * @param opcode The 2 byte opcode of the packet
	 * @param data The rest of the data (excluding length & opcode header)
	 * @return A Packet of the right class, deserialized from the data
	 */
	public static Packet decodeCopy(short opcode, ByteBuf data) {
		// Make sure the opcode is valid
		if(opcode < 0 || opcode >= packets.length) {
			throw new IllegalArgumentException("Packet opcode out of range: " + opcode);
		}
		
		// My first lambda!
		// Have a packet decoded if it exists, or throw exception if it's null
		String nullMsg = "Packet " + opcode + " does not exist.";
		return Optional.ofNullable(packets[opcode]).orElseThrow(() -> new IllegalArgumentException(nullMsg)).decode(data);
	}

}
