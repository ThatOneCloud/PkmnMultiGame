package net.cloud.server.tracking;

import java.time.LocalTime;

/**
 * For lack of a better name, this object contains all of the various statistics that the 
 * StatTracker has been collecting. 
 */
public class StatContainer implements Cloneable {
	
	/** The time this container's information is valid for */
	private LocalTime creationTime;
	
	/** Statistic on how many players are connected */
	private int playersOnline;
	
	/**
	 * Create a new StatContainer with all default or blank values. 
	 * To create a copy of the statistics as a record of a certain moment, use clone.
	 */
	public StatContainer()
	{
		creationTime = LocalTime.now();
		
		playersOnline = 0;
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
		c.creationTime = LocalTime.now();
		
		// Now deep copy each needed field to make it an independent copy
		c.playersOnline = playersOnline;
		
		// (Can clone fields that are not primitives)
		
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
	 * Set a new figure for how many players are currently online
	 * @param currentlyOnline The number of players currently online
	 */
	public void updatePlayersOnline(int currentlyOnline)
	{
		this.playersOnline = currentlyOnline;
	}
	
	/**
	 * Obtain the number of players that were online at the time of this record
	 * @return The current number of players online
	 */
	public int getPlayersOnlineStat()
	{
		return playersOnline;
	}

}
