package net.cloud.mmo.nio.packet;

import net.cloud.mmo.entity.player.Player;
import io.netty.buffer.ByteBuf;

public interface Packet {
	
	/**
	 * @return The opcode associated with this Packet
	 */
	public short getOpcode();

	/**
	 * Packs the information contained in the Packet into a ByteBuf. 
	 * The Packet at this point should already be initialized. 
	 * @return A ByteBuf containing this Packet's serialized information
	 */
	public void encode(ByteBuf buffer);
	
	/**
	 * Decodes the Packet. Takes the data and creates a copy of the specific Packet 
	 * implementing class.  The Packet returned is a <i>copy</i> with the members 
	 * set according to the data, <u>not</u> the original.
	 * @param data The data required for the Packet, assumed to be without header.
	 * @return A copy of the Packet deserialized to reflect the provided data
	 */
	public Packet decode(ByteBuf data);
	
	/**
	 * Executes whatever action the Packet needs to take, to take action on the information 
	 * stored in it.  Split from the decoding process, but assumes the Packet has been 
	 * created and initialized. 
	 */
	public void handlePacket(Player player);

}
