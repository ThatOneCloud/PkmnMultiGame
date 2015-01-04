package net.cloud.mmo.nio.packet.packets;

import io.netty.buffer.ByteBuf;
import net.cloud.mmo.entity.player.Player;
import net.cloud.mmo.logging.Logger;
import net.cloud.mmo.nio.packet.Packet;
import net.cloud.mmo.nio.packet.PacketConstants;

public class TestPacket implements Packet {
	
	private int testValue;
	
	// Default constructor for PacketManager only
	protected TestPacket() {
		this(-1);
	}

	public TestPacket(int testValue) {
		// Would normally initialize some values here
		this.testValue = testValue;
	}
	
	@Override
	public short getOpcode() {
		return PacketConstants.TEST_PACKET;
	}

	@Override
	public void encode(ByteBuf buffer) {
		// Write some data
		buffer.writeInt(testValue);

		Logger.writer().println("Test packet encoded");
		Logger.writer().flush();
	}

	@Override
	public Packet decode(ByteBuf data) {
		// This needs to create a new TestPacket, with the same data as it had when it was encoded
		return new TestPacket(data.readInt());
	}

	@Override
	public void handlePacket(Player player) {
		Logger.writer().println(player + ": Handling test packet, value: " + testValue);
		Logger.writer();
	}

}
