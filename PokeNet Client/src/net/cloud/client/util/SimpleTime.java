package net.cloud.client.util;

import java.time.LocalTime;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import io.netty.buffer.ByteBuf;
import net.cloud.client.Client;
import net.cloud.client.nio.bufferable.Bufferable;

/**
 * A simple Time object useful to me. Tracks time as in hours:minutes:seconds of a day.
 */
@XStreamAlias("SimpleTime")
public class SimpleTime implements Bufferable {
	
	/** Hours, 0 to 23 */
	private byte hours;
	
	/** Minutes, 0 to 59 */
	private byte minutes;
	
	/** Seconds, 0 to 59 */
	private byte seconds;
	
	/** 
	 * Dummy constructor for createFrom
	 * @param dummy Does not matter
	 */
	private SimpleTime(boolean dummy) {}
	
	/**
	 * This creates a SimpleTime using the current time
	 */
	public SimpleTime()
	{
		LocalTime time = LocalTime.now(Client.CLOCK);
		
		setHours(time.getHour());
		setMinutes(time.getMinute());
		setSeconds(time.getSecond());
	}

	/**
	 * Create a simple time with the given time
	 * @param hours Hours, 0 to 23
	 * @param minutes Minutes, 0 to 59
	 * @param seconds Seconds, 0 to 59
	 */
	public SimpleTime(int hours, int minutes, int seconds)
	{
		setHours(hours);
		setMinutes(minutes);
		setSeconds(seconds);
	}
	
	/**
	 * Create a SimpleTime object from data lying in the buffer
	 * @param buffer Buffer positioned at SimpleTime data
	 * @return A new SimpleTime object reflecting the serialized data
	 */
	public static SimpleTime createFrom(ByteBuf buffer)
	{
		SimpleTime newTime = new SimpleTime(true);
		
		newTime.restore(buffer);
		
		return newTime;
	}
	
	/**
	 * Time, formatted as hours:minutes:seconds
	 */
	@Override
	public String toString()
	{
		return hours + ":" + minutes + ":" + seconds;
	}

	/**
	 * @return Hours, range 0 to 23
	 */
	public byte getHours()
	{
		return hours;
	}

	/**
	 * @param hours The hours to set, 0 to 23
	 */
	public void setHours(int hours)
	{
		// Verify range
		if(hours < 0 || hours > 59)
		{
			throw new IllegalArgumentException("Hours not in range [0, 23]");
		}
		
		this.hours = (byte) hours;
	}

	/**
	 * @return Minutes, 0 to 59
	 */
	public byte getMinutes()
	{
		return minutes;
	}

	/**
	 * @param minutes The minutes to set, 0 to 59
	 */
	public void setMinutes(int minutes)
	{
		// Verify range
		if(minutes < 0 || minutes > 59)
		{
			throw new IllegalArgumentException("Minutes not in range [0, 59]");
		}
		
		this.minutes = (byte) minutes;
	}

	/**
	 * @return The seconds, 0 to 59
	 */
	public byte getSeconds()
	{
		return seconds;
	}

	/**
	 * @param seconds The seconds to set, 0 to 59
	 */
	public void setSeconds(int seconds)
	{
		// Verify range
		if(seconds < 0 || seconds > 59)
		{
			throw new IllegalArgumentException("Seconds not in range [0, 59]");
		}
		
		this.seconds = (byte) seconds;
	}

	@Override
	public void save(ByteBuf buffer)
	{
		buffer.writeByte(hours);
		buffer.writeByte(minutes);
		buffer.writeByte(seconds);
	}

	@Override
	public void restore(ByteBuf buffer)
	{
		this.hours = buffer.readByte();
		this.minutes = buffer.readByte();
		this.seconds = buffer.readByte();
	}

}
