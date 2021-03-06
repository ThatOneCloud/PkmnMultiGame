package net.cloud.server.nio;

import net.cloud.server.entity.player.LoginHandler;
import net.cloud.server.entity.player.Player;
import net.cloud.server.entity.player.PlayerChannelConfig;
import net.cloud.server.entity.player.PlayerFactory;
import net.cloud.server.event.task.TaskEngine;
import net.cloud.server.event.task.voidtasks.ConnectTimeoutTask;
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
	protected void initChannel(SocketChannel channel) throws Exception
	{
		// Create a new Player for this new connection
		PacketSender packetSender = new PacketSender(channel);
		PlayerChannelConfig config = new PlayerChannelConfig();
		Player newPlayer = PlayerFactory.createOnNewConnection(packetSender, config);
		
		// Add a listener which will call handle disconnect when the channel is closed
		config.setDcListener((f) -> LoginHandler.handleDisconnect(newPlayer));
		channel.closeFuture().addListener(config.getDcListener());
		
		// At this point, state is CONNECTED. They should be following up to become VERIFIED soon.
		// so we use a task to time-out and abort the player if they fail to do so
		config.setConnectTimeoutTask(new ConnectTimeoutTask(newPlayer));
		TaskEngine.instance().submitDelayed(LoginHandler.TIMEOUT, config.getConnectTimeoutTask());
		
		PacketHandler packetHandler = new PacketHandler(newPlayer);
		config.setPacketHandler(packetHandler);
		
		// Inbound handlers
		channel.pipeline().addLast(new LengthFieldBasedFrameDecoder(
				PacketConstants.MAX_PACKET_LENGTH, 
				PacketConstants.LENGTH_FIELD_OFFSET,
				PacketConstants.LENGTH_FIELD_LENGTH, 
				PacketConstants.LENGTH_FIELD_ADJUSTMENT, 
				PacketConstants.BYTES_TO_STRIP),
				new PacketDecoder(),
				packetHandler);

		// Outbound handlers
		channel.pipeline().addLast(new LengthFieldPrepender(PacketConstants.LENGTH_FIELD_LENGTH),
				new PacketEncoder());
		
	}

}
