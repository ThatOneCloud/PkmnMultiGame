package net.cloud.client.util;

import java.time.LocalDate;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import io.netty.buffer.ByteBuf;
import net.cloud.client.Client;
import net.cloud.client.nio.bufferable.Bufferable;
import net.cloud.client.nio.bufferable.BufferableException;

/**
 * Simple Date object useful to me. 
 * Uses mm/dd/yyyy for format, and implements Bufferable
 */
@XStreamAlias("SimpleDate")
public class SimpleDate implements Bufferable {
	
	/** The month. Range 1 - 12 */
	private byte month;
	
	/** The day. Range 1 - 31 */
	private byte day;
	
	/** The year. Range 0 - whatever, I guess */
	private short year;
	
	/** Dummy constructor for createFrom to use */
	private SimpleDate(boolean dummy) {}
	
	/**
	 * This creates a SimpleDate using the current date
	 */
	public SimpleDate()
	{
		LocalDate date = LocalDate.now(Client.CLOCK);
		
		setMonth(date.getMonthValue());
		setDay(date.getDayOfMonth());
		setYear(date.getYear());
	}
	
	/**
	 * Create a new Date object with the given month, day, and year
	 * @param month Month, range [1, 12]
	 * @param day Day, range [1, 31]
	 * @param year Year, range [0, max]
	 */
	public SimpleDate(int month, int day, int year)
	{
		// Run through these, they'll verify
		setMonth(month);
		setDay(day);
		setYear(year);
	}
	
	/**
	 * Create a new SimpleDate object from data in the buffer
	 * @param buffer The buffer, currently positioned for simple date data
	 * @return A new SimpleDate reflecting the data in the buffer
	 * @throws BufferableException If the data could not be restored
	 */
	public static SimpleDate createFrom(ByteBuf buffer)
	{
		SimpleDate newDate = new SimpleDate(true);
		
		newDate.restore(buffer);
		
		return newDate;
	}
	
	/** Formatted as mm/dd/yyyy (although without digits enforced) */
	@Override
	public String toString()
	{
		return month + "/" + day + "/" + year;
	}
	
	/**
	 * @return The month, range 1 - 12
	 */
	public byte getMonth()
	{
		return month;
	}

	/**
	 * @param month The month to set, range 1 - 12
	 * @throws IllegalArgumentException Month is not in range
	 */
	public void setMonth(int month)
	{
		// Verify range
		if(month < 1 || month > 12)
		{
			throw new IllegalArgumentException("Month not in range [1, 12]");
		}
		
		this.month = (byte) month;
	}

	/**
	 * @return The day, range 1 - 31
	 */
	public byte getDay()
	{
		return day;
	}

	/**
	 * @param day The day to set, range 1 - 31
	 * @throws IllegalArgumentException Day is not in range
	 */
	public void setDay(int day)
	{
		// Verify range
		if(day < 1 || day > 31)
		{
			throw new IllegalArgumentException("Day not in range [1, 31]");
		}

		this.day = (byte) day;
	}

	/**
	 * @return The year, range 0 - max
	 */
	public short getYear()
	{
		return year;
	}

	/**
	 * @param year The year to set, range 0 - max
	 * @throws IllegalArgumentException Year is not in range
	 */
	public void setYear(int year)
	{
		// Verify range
		if(year < 0)
		{
			throw new IllegalArgumentException("Month not in range [0, MAX]");
		}

		this.year = (short) year;
	}

	@Override
	public void save(ByteBuf buffer)
	{
		buffer.writeByte(month);
		buffer.writeByte(day);
		buffer.writeShort(year);
	}

	@Override
	public void restore(ByteBuf buffer)
	{
		this.month = buffer.readByte();
		this.day = buffer.readByte();
		this.year = buffer.readShort();
	}

}
