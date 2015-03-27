package net.cloud.server.util;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import io.netty.buffer.ByteBuf;
import net.cloud.server.nio.bufferable.Bufferable;

/**
 * Both the SimpleDate and SimpleTime combined into one, for when you need a date AND a time
 */
@XStreamAlias("SimpleDateTime")
public class SimpleDateTime implements Bufferable {
	
	/** The date */
	private SimpleDate date;
	
	/** The time */
	private SimpleTime time;
	
	/** 
	 * Dummy constructor for createFrom
	 * @param dummy Does not matter
	 */
	private SimpleDateTime(boolean dummy) {}
	
	/**
	 * Create it using the current date and time
	 */
	public SimpleDateTime()
	{
		this(new SimpleDate(), new SimpleTime());
	}
	
	/**
	 * Create a combined date and time with the given date and time
	 * @param date The date...
	 * @param time The time...
	 */
	public SimpleDateTime(SimpleDate date, SimpleTime time)
	{
		setDate(date);
		setTime(time);
	}
	
	/**
	 * Create a date and time with all the parameters
	 * @param month Month, 1 -12
	 * @param day Day, 1 - 31
	 * @param year Year, 0 - max short value
	 * @param hours Hours, 0 - 23
	 * @param minutes Minutes, 0 - 59
	 * @param seconds Seconds, 0 - 59
	 */
	public SimpleDateTime(int month, int day, int year, int hours, int minutes, int seconds)
	{
		setDate(new SimpleDate(month, day, year));
		setTime(new SimpleTime(hours, minutes, seconds));
	}
	
	/**
	 * Create a new SimpleDateTime using the data in the buffer
	 * @param buffer A buffer with data for a simple date and time
	 * @return A new SimpleDateTime
	 */
	public static SimpleDateTime createFrom(ByteBuf buffer)
	{
		SimpleDateTime newDateTime = new SimpleDateTime(true);
		
		newDateTime.restore(buffer);
		
		return newDateTime;
	}
	
	/** The formatted date and time strings, space separated */
	@Override
	public String toString()
	{
		return date.toString() + " " + time.toString();
	}

	/**
	 * @return The date
	 */
	public SimpleDate getDate()
	{
		return date;
	}

	/**
	 * @param date The date to set
	 */
	public void setDate(SimpleDate date)
	{
		this.date = date;
	}

	/**
	 * @return The time
	 */
	public SimpleTime getTime()
	{
		return time;
	}

	/**
	 * @param time The time to set
	 */
	public void setTime(SimpleTime time)
	{
		this.time = time;
	}

	@Override
	public void save(ByteBuf buffer)
	{
		date.save(buffer);
		time.save(buffer);
	}

	@Override
	public void restore(ByteBuf buffer)
	{
		this.date = SimpleDate.createFrom(buffer);
		this.time = SimpleTime.createFrom(buffer);
	}

}
