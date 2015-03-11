package net.cloud.client.nio.packet;

import java.util.List;

import net.cloud.client.nio.packet.packets.PacketManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * Will wait until a packet has been entirely received, and then 
 * create a Packet, which will then decode itself.  
 * The decoded packet is added to the pipeline for handling. 
 */
public class PacketDecoder extends ByteToMessageDecoder {

	/**
	 * Figure out which packet is coming through the pipeline (by reading its opcode) 
	 * then pass it off to the PacketManager
	 */
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception
	{
		// Before this in the pipeline was a length based decoder, so now we need to figure out which Packet 
		// to create, create it, and pass it on to the PacketHandler, which is next in the pipeline.
		
		// This method occasionally has an empty buffer provided to it.  Need at least the opcode.
		if(in.readableBytes() <= PacketConstants.OPCODE_LENGTH) {
			return;
		}
		
		// Read the opcode of the Packet
		short opcode = in.readShort();
		
		// Now that we have the opcode, we can create a specific instance of a Packet
		out.add(PacketManager.decodeCopy(opcode, in));
	}

}
