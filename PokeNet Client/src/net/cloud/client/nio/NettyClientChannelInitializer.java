package net.cloud.client.nio;

import net.cloud.client.nio.packet.PacketConstants;
import net.cloud.client.nio.packet.PacketDecoder;
import net.cloud.client.nio.packet.PacketEncoder;
import net.cloud.client.nio.packet.PacketHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

/**
 * Top level class for the channel initializer. It's a specialized ChannelInboundAdapter
 * Configures a Channel when a connection is first made. 
 * Afterwards, this is removed from the channel pipeline.
 */
public class NettyClientChannelInitializer extends ChannelInitializer<SocketChannel> {

	/**
	 * Initialize a channel pipeline, which will send packets through various decoders and then a PacketHandler, 
	 * and also through various encoders going the other direction.
	 */
	@Override
	protected void initChannel(SocketChannel ch) throws Exception
	{
		// Inbound handlers
		ch.pipeline().addLast(
				new LengthFieldBasedFrameDecoder(
					PacketConstants.MAX_PACKET_LENGTH, 
					PacketConstants.LENGTH_FIELD_OFFSET,
					PacketConstants.LENGTH_FIELD_LENGTH, 
					PacketConstants.LENGTH_FIELD_ADJUSTMENT, 
					PacketConstants.BYTES_TO_STRIP),
				new PacketDecoder(),
				new PacketHandler());

		// Outbound handlers
		ch.pipeline().addLast(
				new LengthFieldPrepender(PacketConstants.LENGTH_FIELD_LENGTH),
				new PacketEncoder());
	}

}
