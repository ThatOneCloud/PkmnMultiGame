package net.cloud.client.nio.packet;

import net.cloud.client.entity.player.Player;
import net.cloud.client.game.World;
import net.cloud.client.logging.Logger;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Once information has been received and decoded, the Packet needs to be handled. 
 * This class will take the packet, and execute its action via handlePacket()
 */
public class PacketHandler extends ChannelInboundHandlerAdapter {
	
	/**
	 * Take a constructed packet from the pipeline, and then have it handle itself
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
	{
		// The object has been decoded into a Packet. Grab it
		Packet packet = (Packet) msg;

		// Pass off to the packet for handling
		Player player = World.instance().getPlayer();
		if(player != null)
		{
			packet.handlePacket(player);
		}
		else {
			throw new NullPointerException("Error: Player is currently null - cannot receive packets");
		}
	}

	/**
	 * An exception occurred somewhere while reading a packet. Log it, close the channel
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
	{
		// Exception occurred, close the connection
		Logger.instance().logException("Exception caught handling packet. Closing connection.", cause);
		ctx.close();
	}

}
