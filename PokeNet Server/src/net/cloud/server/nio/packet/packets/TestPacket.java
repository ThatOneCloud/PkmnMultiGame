package net.cloud.server.nio.packet.packets;

import io.netty.buffer.ByteBuf;
import net.cloud.server.entity.player.Player;
import net.cloud.server.logging.Logger;
import net.cloud.server.nio.packet.Packet;
import net.cloud.server.nio.packet.PacketConstants;

/** Packet for testing stuff. Like a box of chocolates, never know what it's gonna do */
public class TestPacket implements Packet {
	
	/** test value */
	private int testValue;
	
	/** Default constructor for PacketManager only */
	protected TestPacket()
	{
		this(-1);
	}

	/** Create a packet with the given test value in it */
	public TestPacket(int testValue)
	{
		// Would normally initialize some values here
		this.testValue = testValue;
	}
	
	@Override
	public short getOpcode()
	{
		return PacketConstants.TEST;
	}

	@Override
	public void encode(ByteBuf buffer)
	{
		// Write some data
		buffer.writeInt(testValue);

		Logger.writer().println("Test packet encoded");
		Logger.writer().flush();
	}

	@Override
	public Packet decode(ByteBuf data)
	{
		// This needs to create a new TestPacket, with the same data as it had when it was encoded
		return new TestPacket(data.readInt());
	}

	/** Displays a message about the packet */
	@Override
	public void handlePacket(Player player)
	{
		Logger.writer().println(player + ": Handling test packet, value: " + testValue);
		Logger.writer().flush();
	}

}
