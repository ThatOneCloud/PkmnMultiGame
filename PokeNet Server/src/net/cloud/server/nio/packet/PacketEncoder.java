package net.cloud.server.nio.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Responsible for having packets encode themselves, 
 * where previously a Packet was created, ready to be sent out.
 */
public class PacketEncoder extends MessageToByteEncoder<Packet> {

	@Override
	protected void encode(ChannelHandlerContext ctx, Packet msg, ByteBuf out) throws Exception {
		// Packet's opcode goes into a header before its data
		out.writeShort(msg.getOpcode());

		// All this needs to do is have the Packet write bytes into the buffer
		msg.encode(out);
	}

}
