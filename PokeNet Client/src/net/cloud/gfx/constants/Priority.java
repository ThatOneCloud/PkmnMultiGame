package net.cloud.gfx.constants;

/**
 * Constants to sort of standardize usage of priority in elements
 */
public class Priority {

	/** Minimum priority that should be used. Can use lower if you really want. */
	public static final int MIN = 1;
	
	/** Maximum priority that should be used. Can use higher if you really want. */
	public static final int MAX = 10;
	
	/** 25% */
	public static final int MED_LOW = PERCENTAGE(0.25);
	
	/** 50% */
	public static final int MED = PERCENTAGE(0.50);
	
	/** 75% */
	public static final int MED_HIGH = PERCENTAGE(0.75);
	
	/**
	 * A priority based on a percentage of the standard priority range. 
	 * For example, PERCENTAGE(0.50) is the middle of the range. 
	 * @param percentage Percent factor
	 * @return Priority based on the percent factor
	 */
	public static int PERCENTAGE(double percentage)
	{
		return (int) (((MAX - MIN) * percentage) + MIN);
	}
	
}
