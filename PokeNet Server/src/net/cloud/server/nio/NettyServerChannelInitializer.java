package net.cloud.server.nio;

import net.cloud.server.entity.player.LoginState;
import net.cloud.server.entity.player.Player;
import net.cloud.server.entity.player.PlayerFactory;
import net.cloud.server.event.task.TaskEngine;
import net.cloud.server.game.World;
import net.cloud.server.logging.Logger;
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
	
	/** How long will we wait for the client to follow up before giving up */
	private static long TIMEOUT = 5000;

	/**
	 * Initialize a channel pipeline, which will send packets through various decoders and then a PacketHandler, 
	 * and also through various encoders going the other direction. 
	 * Notably, this creates and adds a blank player to the world.
	 */
	@Override
	protected void initChannel(SocketChannel channel) throws Exception
	{
		// Place a Player in the world for this new connection
		PacketSender packetSender = new PacketSender(channel);
		Player newPlayer = PlayerFactory.createOnNewConnection(packetSender);
		World.instance().getPlayerMap().place(channel, newPlayer);
		
		// At this point, state is CONNECTED. They should be following up to become VERIFIED soon.
		// so we use a task to time-out and abort the player if they fail to do so
		TaskEngine.getInstance().submitDelayed(TIMEOUT, () ->
		{
			// Body of the task. Is the player still sitting in the CONNECTED state?
			if(newPlayer.getLoginState() != LoginState.CONNECTED)
			{
				// They are not, so this task need not proceed.
				return;
			}
			
			// They are stuck in CONNECTED... terminate the connection
			abortConnection(newPlayer, channel);
		});
		
		// Inbound handlers
		channel.pipeline().addLast(new LengthFieldBasedFrameDecoder(
				PacketConstants.MAX_PACKET_LENGTH, 
				PacketConstants.LENGTH_FIELD_OFFSET,
				PacketConstants.LENGTH_FIELD_LENGTH, 
				PacketConstants.LENGTH_FIELD_ADJUSTMENT, 
				PacketConstants.BYTES_TO_STRIP),
				new PacketDecoder(),
				new PacketHandler());

		// Outbound handlers
		channel.pipeline().addLast(new LengthFieldPrepender(PacketConstants.LENGTH_FIELD_LENGTH),
				new PacketEncoder());
		
	}
	
	/**
	 * Called when a player fails to follow up with a login request after connecting. 
	 * Removes the player from the world and terminates their channel connection. 
	 * @param player The player that failed to login
	 * @param channel The channel that player had connected with
	 */
	private void abortConnection(Player player, SocketChannel channel)
	{
		// We'll remove the player from the global list immediately
		World.instance().getPlayerMap().remove(channel);
		
		// And then disconnect the channel that player was connected on
		try {
			channel.close().sync();
		} catch (InterruptedException e) {
			// There's not much we can do if the channel does not close, except shout about it
			Logger.instance().logException("Could not close channel while aborting newly connected player.", e);
		}
	}

}
