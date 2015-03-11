package net.cloud.client.nio.packet;

import java.util.Optional;

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
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		// The object has been decoded into a Packet. Grab it
		Packet packet = (Packet) msg;

		// Channel context provides a Channel - which is how we find the Player that sent the packet
		Optional<Player> player = Optional.ofNullable(World.getInstance().getPlayer());

		// Handle the packet, or throw an exception if the Player came back null from World
		String nullMsg = "Error: No Player in World associated with Channel";
		packet.handlePacket(player.orElseThrow(() -> new NullPointerException(nullMsg)));
	}

	/**
	 * An exception occurred somewhere while reading a packet. Log it, close the channel
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		// Exception occurred, close the connection
		Logger.writer().println("[ERR] " + cause.getClass().getName() + " :\n   " + cause.getMessage() + "\n   Closing connection.");
		Logger.writer().flush();
		ctx.close();
	}

}
