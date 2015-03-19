package net.cloud.server.nio.packet;

/**
 * Contains constants related to the network and packet code. 
 */
public class PacketConstants {
	
	// Begin Packet Opcodes //
	public static final short TEST = 0;
	public static final short COMPOSITE = 1;
	public static final short LOGIN = 2;
	public static final short LOGIN_RESPONSE = 3;
	public static final short LOGIN_DATA_REQUEST = 4;
	public static final short LOGIN_DATA_RESPONSE = 5;
	public static final short SHOW_MSG_DIALOG = 6;
	public static final short BUTTON_ACTION = 7;
	public static final short LOGOUT = 8;
	// End Packet Opcodes //
	
	/** The number of packets (Ie the limit on the op code) */
	public static final int NUM_PACKETS = 9;
	
	/** Max bytes that can be in a single packet */
	public static final int MAX_PACKET_LENGTH = 4096;
	
	/** Offset of the length field in packet header */
	public static final int LENGTH_FIELD_OFFSET = 0;
	
	/** Bytes in the length field in packet header */
	public static final int LENGTH_FIELD_LENGTH = 2;
	
	/** Adjustment for if the length field counts itself */
	public static final int LENGTH_FIELD_ADJUSTMENT = 0;
	
	/** The number of bytes to strip from the header, after reading length field */
	public static final int BYTES_TO_STRIP = 2;
	
	/** The number of bytes used by the packet's opcode */
	public static final int OPCODE_LENGTH = 2;
	
}
