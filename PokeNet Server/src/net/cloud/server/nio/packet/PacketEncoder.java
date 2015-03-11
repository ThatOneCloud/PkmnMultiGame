package net.cloud.server.nio.packet;

import net.cloud.server.logging.Logger;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Responsible for having packets encode themselves, 
 * where previously a Packet was created, ready to be sent out.
 */
public class PacketEncoder extends MessageToByteEncoder<Packet> {

	/**
	 * Encodes the given packet into the given byte buffer. 
	 * The packet's opcode is appended before the packet's data
	 */
	@Override
	protected void encode(ChannelHandlerContext ctx, Packet msg, ByteBuf out) throws Exception
	{
		// Packet's opcode goes into a header before its data
		out.writeShort(msg.getOpcode());

		// All this needs to do is have the Packet write bytes into the buffer
		try {
			msg.encode(out);
		} catch(Exception e) {
			// We could let the exception go beyond this method, but since we're favoring a VoidPromise 
			// and no future listener, exceptions otherwise go silent.
			logException(e);
		}
	}
	
	/**
	 * An exception occurred while trying to encode the message somewhere along the lines. 
	 * Simply has it logged. 
	 * This method seems not to get called, due to the asynchronous nature of writing and my usage 
	 * of a VoidPromise while writing to the channel.
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
	{
		logException(cause);
	}
	
	/**
	 * Send the exception to the Logger
	 * @param cause What went wrong
	 */
	private void logException(Throwable cause)
	{
		Logger.instance().logException("Exception caught while encoding packet", cause);
	}

}
