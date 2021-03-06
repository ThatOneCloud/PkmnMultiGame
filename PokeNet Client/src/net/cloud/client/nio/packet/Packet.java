package net.cloud.client.nio.packet;

import net.cloud.client.entity.player.Player;
import net.cloud.client.nio.bufferable.BufferableException;
import io.netty.buffer.ByteBuf;

/**
 * Interface defining a packet. This is the data type for objects sent between the client 
 * and server for communication purposes. Each packet has on opcode defining which packet 
 * it is, like a GUID. Each packet also knows how to encode, decode, and handle itself.
 */
public interface Packet {
	
	/**
	 * @return The opcode associated with this Packet
	 */
	public short getOpcode();

	/**
	 * Packs the information contained in the Packet into a ByteBuf. 
	 * The Packet at this point should already be initialized. 
	 * @param buffer The ByteBuf that data will be placed into, from the current position
	 * @throws BufferableException If there is an issue encoding a Bufferable object
	 */
	public void encode(ByteBuf buffer) throws BufferableException;
	
	/**
	 * Decodes the Packet. Takes the data and creates a copy of the specific Packet 
	 * implementing class.  The Packet returned is a <i>copy</i> with the members 
	 * set according to the data, <u>not</u> the original.
	 * @param data The data required for the Packet, assumed to be without header.
	 * @return A copy of the Packet deserialized to reflect the provided data
	 * @throws BufferableException If there is an issue decoding a Bufferable object
	 */
	public Packet decode(ByteBuf data) throws BufferableException;
	
	/**
	 * Executes whatever action the Packet needs to take, to take action on the information 
	 * stored in it.  Split from the decoding process, but assumes the Packet has been 
	 * created and initialized. 
	 * @param player The player receiving the packet
	 */
	public void handlePacket(Player player);

}
