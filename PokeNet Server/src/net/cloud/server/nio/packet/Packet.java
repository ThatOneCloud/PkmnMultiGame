package net.cloud.server.nio.packet;

import net.cloud.server.entity.player.Player;
import net.cloud.server.nio.bufferable.BufferableException;
import io.netty.buffer.ByteBuf;

/**
 * Common methods each Packet must implement. 
 * Each Packet is a unit of information, capable of encoding/decoding itself and acting on its information.
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
	 */
	public void handlePacket(Player player);

}
