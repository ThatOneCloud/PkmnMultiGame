package net.cloud.mmo.nio.packet.packets;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import net.cloud.mmo.entity.player.Player;
import net.cloud.mmo.nio.packet.Packet;
import net.cloud.mmo.nio.packet.PacketConstants;

/**
 * A CompositePacket is a Packet made up of other Packets. 
 * This is useful for sending several packets together, and assuring 
 * they will arrive in a determinate order and be handled in that order. 
 * The behavior of each individual packet is the same, and they happen 
 * in the order they are composed in. 
 */
public class CompositePacket implements Packet {
	
	// Composed via a list of other packets
	List<Packet> packets;
	
	/** Default constructor leaves all data fields default or null */
	public CompositePacket() {}
	
	/** A CompositePacket created from one or more other packets */
	public CompositePacket(Packet first, Packet... others)
	{
		packets = new LinkedList<Packet>();
		
		// Add the first packet
		packets.add(first);
		
		// and any that may follow it
		Arrays.stream(others).forEach(p -> packets.add(p));
	}

	@Override
	public short getOpcode() {
		// Not entirely opaque - this packet has its own opcode
		return PacketConstants.COMPOSITE_PACKET;
	}

	@Override
	public void encode(ByteBuf buffer) {
		// Place the number of packets first (may not be necessary, but clears things up)
		buffer.writeInt(packets.size());
		
		// Encode each packet into the composite, back to back
		for(Packet p : packets)
		{
			// Need the opcode manually written so we can decode it
			// WARNING: This circumvents PacketEncoder
			buffer.writeShort(p.getOpcode());
			
			// And then it can place its data into the buffer
			p.encode(buffer);
		}
	}

	@Override
	public Packet decode(ByteBuf data) {
		// Sorta different - create a blank Packet and initialize its list
		CompositePacket newPacket = new CompositePacket();
		newPacket.packets = new LinkedList<Packet>();
		
		// Find out how many packets this one is composed of
		int numPackets = data.readInt();
		
		// Each packet should decode itself and consume no more of the data, until data is gone
		for(int i = 0; i < numPackets; ++i)
		{
			// Need the opcode so we know what Packet we're dealing with
			// WARNING: This circumvents PacketDecoder
			short opCode = data.readShort();
			
			// Then hand it off to PacketManager, which deals with decoding
			newPacket.packets.add(PacketManager.decodeCopy(opCode, data));
		}
		
		return newPacket;
	}

	@Override
	public void handlePacket(Player player) {
		// Handle each of the packets in turn
		packets.stream().forEach(packet -> packet.handlePacket(player));
	}

}
