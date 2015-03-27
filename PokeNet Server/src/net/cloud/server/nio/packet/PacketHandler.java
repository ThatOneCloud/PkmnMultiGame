package net.cloud.server.nio.packet;

import net.cloud.server.entity.player.Player;
import net.cloud.server.logging.Logger;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Once information has been received and decoded, the Packet needs to be handled. 
 * This class will take the packet, and execute its action via handlePacket()
 */
public class PacketHandler extends ChannelInboundHandlerAdapter {
	
	/** The player object these packets are coming from */
	private Player player;
	
	/**
	 * Create a packet handler linked to the given player. This is like a state variable, 
	 * so this handler cannot be used in multiple pipelines.
	 * @param player The player
	 */
	public PacketHandler(Player player)
	{
		this.player = player;
	}
	
	/**
	 * Take a constructed packet from the pipeline, and then have it handle itself
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
	{
		// The object has been decoded into a Packet. Grab it
		Packet packet = (Packet) msg;
		
		// Then have it handle itself, giving it the player that sent it
		packet.handlePacket(player);
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
	
	/**
	 * @return The player we will route packets to
	 */
	public Player getPlayer()
	{
		return player;
	}
	
	/**
	 * @param player The player packets will be routed to from now on
	 */
	public void setPlayer(Player player)
	{
		this.player = player;
	}

}
