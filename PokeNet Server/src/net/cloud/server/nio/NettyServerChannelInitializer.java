package net.cloud.server.nio;

import net.cloud.server.entity.player.Player;
import net.cloud.server.entity.player.PlayerFactory;
import net.cloud.server.game.World;
import net.cloud.server.nio.packet.PacketConstants;
import net.cloud.server.nio.packet.PacketDecoder;
import net.cloud.server.nio.packet.PacketEncoder;
import net.cloud.server.nio.packet.PacketHandler;
import net.cloud.server.nio.packet.PacketSender;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

/**
 * Top level class for the channel initializer. It's a specialized ChannelInboundAdapter
 * Configures a Channel when a connection is first made. 
 * Afterwards, this is removed from the channel pipeline.
 */
public class NettyServerChannelInitializer extends ChannelInitializer<SocketChannel> {

	/**
	 * Initialize a channel pipeline, which will send packets through various decoders and then a PacketHandler, 
	 * and also through various encoders going the other direction. 
	 * Notably, this creates and adds a blank player to the world.
	 */
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		// Place a Player in the world for this new connection
		PacketSender packetSender = new PacketSender(ch);
		Player newPlayer = PlayerFactory.createOnNewConnection(packetSender);
		World.getInstance().getPlayerMap().place(ch, newPlayer);
		
		// Inbound handlers
		ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(
				PacketConstants.MAX_PACKET_LENGTH, 
				PacketConstants.LENGTH_FIELD_OFFSET,
				PacketConstants.LENGTH_FIELD_LENGTH, 
				PacketConstants.LENGTH_FIELD_ADJUSTMENT, 
				PacketConstants.BYTES_TO_STRIP),
				new PacketDecoder(),
				new PacketHandler());

		// Outbound handlers
		ch.pipeline().addLast(new LengthFieldPrepender(PacketConstants.LENGTH_FIELD_LENGTH),
				new PacketEncoder());
		
	}

}
