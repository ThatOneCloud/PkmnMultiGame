package net.cloud.server.entity.player;

import net.cloud.server.event.task.voidtasks.CancellableVoidTask;
import net.cloud.server.nio.packet.PacketHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

/**
 * Storage class for some of the objects needed during channel initialization
 */
public class PlayerChannelConfig {
	
	/** Channel listener for when the channel is closed, to handle disconnects */
	private GenericFutureListener<Future<? super Void>> dcListener;
	
	/** Task to time out when the player fails to move past connected */
	private CancellableVoidTask connectTimeoutTask;
	
	/** PacketHandler in the channel pipeline */
	private PacketHandler packetHandler;
	
	/**
	 * Default constructor leaves all fields null
	 */
	public PlayerChannelConfig() {}
	
	/**
	 * @return Channel listener for when the channel is closed, to handle disconnects
	 */
	public GenericFutureListener<Future<? super Void>> getDcListener()
	{
		return dcListener;
	}

	/**
	 * @param dcListener Channel listener for when the channel is closed, to handle disconnects
	 */
	public void setDcListener(GenericFutureListener<Future<? super Void>> dcListener)
	{
		this.dcListener = dcListener;
	}

	/**
	 * @return Task to time out when the player fails to move past connected
	 */
	public CancellableVoidTask getConnectTimeoutTask()
	{
		return connectTimeoutTask;
	}

	/**
	 * @param connectTimeoutTask Task to time out when the player fails to move past connected
	 */
	public void setConnectTimeoutTask(CancellableVoidTask connectTimeoutTask)
	{
		this.connectTimeoutTask = connectTimeoutTask;
	}

	/**
	 * @return PacketHandler in the channel pipeline
	 */
	public PacketHandler getPacketHandler()
	{
		return packetHandler;
	}

	/**
	 * @param packetHandler PacketHandler in the channel pipeline
	 */
	public void setPacketHandler(PacketHandler packetHandler)
	{
		this.packetHandler = packetHandler;
	}
	
}
