package net.cloud.client.tracking;

import java.time.LocalTime;

import net.cloud.client.Client;
import net.cloud.client.ConfigConstants;
import net.cloud.client.util.BoundedCircularIntArray;

/**
 * For lack of a better name, this object contains all of the various statistics that the 
 * StatTracker has been collecting. 
 */
public class StatContainer implements Cloneable {
	
	/** The size of the fps array. Can be fine-tuned to improve accuracy and such. */
	private static final int FPS_ARRAY_SIZE = 5;
	
	/** The time this container's information is valid for */
	private LocalTime creationTime;
	
	/** The fps (frames per second) statistic */
	private BoundedCircularIntArray fps;
	
	/**
	 * Create a new StatContainer with all default or blank values. 
	 * To create a copy of the statistics as a record of a certain moment, use clone.
	 */
	public StatContainer()
	{
		creationTime = LocalTime.now(Client.CLOCK);
		
		fps = new BoundedCircularIntArray(FPS_ARRAY_SIZE, ConfigConstants.FRAME_RATE);
	}
	
	/**
	 * Create a copy of this object, to create a record of the statistics at a certain moment. 
	 */
	@Override
	public StatContainer clone() throws CloneNotSupportedException
	{
		// As recommended, start by modifying a clone of the superclass. (Even though it's Object. Just because.)
		StatContainer c = (StatContainer) super.clone();
		
		// Update the creation time to whatever it now is
		c.creationTime = LocalTime.now(Client.CLOCK);
		
		// Now deep copy each needed field to make it an independent copy
		c.fps = fps.clone();
		
		return c;
	}
	
	/**
	 * Obtain the time that this stat container was created. This also serves as a time-stamp on when the information 
	 * of this container is valid. 
	 * @return The time this object was created
	 */
	public LocalTime getCreationTime()
	{
		return creationTime;
	}
	
	/**
	 * Update the FPS statistic with a more recent value.
	 * @param fpsValue An FPS value
	 */
	public void updateFpsStat(int fpsValue)
	{
		// Place the value into the data object
		fps.pushValue(fpsValue);
	}
	
	/**
	 * Obtain the value this object currently has on record for the FPS statistic
	 * @return The current FPS statistic
	 */
	public double getFpsStat()
	{
		return fps.average();
	}

}
