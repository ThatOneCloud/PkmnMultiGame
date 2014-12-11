package net.cloud.mmo.nio;

import net.cloud.mmo.entity.player.Player;
import net.cloud.mmo.game.World;
import net.cloud.mmo.nio.packet.PacketConstants;
import net.cloud.mmo.nio.packet.PacketDecoder;
import net.cloud.mmo.nio.packet.PacketEncoder;
import net.cloud.mmo.nio.packet.PacketHandler;
import net.cloud.mmo.nio.packet.PacketSender;
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

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		// Place a Player in the world for this new connection
		PacketSender packetSender = new PacketSender(ch);
		Player newPlayer = new Player(packetSender);
		World.getInstance().placePlayer(ch, newPlayer);
		
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
		ch.pipeline().addLast(new LengthFieldPrepender(2),
				new PacketEncoder());
		
	}

}
