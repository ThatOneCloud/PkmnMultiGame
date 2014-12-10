package net.cloud.mmo.nio.packet;

import java.util.Optional;

import net.cloud.mmo.entity.player.Player;
import net.cloud.mmo.game.World;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Once information has been received and decoded, the Packet needs to be handled. 
 * This class will take the packet, and execute its action via handlePacket()
 */
public class PacketHandler extends ChannelInboundHandlerAdapter {
	
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

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		// Exception occurred, close the connection
		System.err.println(cause.getMessage() + "\n   Closing connection.");
		ctx.close();
	}

}
