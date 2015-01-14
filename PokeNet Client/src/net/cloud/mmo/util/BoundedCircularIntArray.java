package net.cloud.mmo.util;

import java.util.Arrays;

/**
 * A terribly long name, I know. But it's [hopefully] descriptive. <br>
 * This array will be of a limited size, and when a new value is placed in it, the value 
 * will overwrite the oldest value if the array is already full. In other words, like a bounded 
 * queue that pushes out old values. 
 */
public final class BoundedCircularIntArray implements Cloneable {
	
	/** The primitive array of values */
	private int[] values;
	
	/** The current index in the array - where the next value will be placed */
	private int index = 0;
	
	/**
	 * Create a new array object that will be limited to the given size. 
	 * By default, the array will contain all default values. 
	 * @param size The bounded capacity of the array
	 */
	public BoundedCircularIntArray(int size)
	{
		values = new int[size];
	}
	
	/**
	 * Create a new array object that will be limited to the given size. 
	 * The array will be filled with the given value to start. 
	 * @param size The bounded capacity of the array
	 * @param initValue The initial value to fill the array with
	 */
	public BoundedCircularIntArray(int size, int initValue)
	{
		values = new int[size];
		Arrays.fill(values, initValue);
	}
	
	/**
	 * Create a copy of this object. This will create a new array with the same values. 
	 */
	@Override
	public BoundedCircularIntArray clone() throws CloneNotSupportedException
	{
		// Obtain the superclass's clone, just because recommended. (Good practice?)
		BoundedCircularIntArray c = (BoundedCircularIntArray) super.clone();
		
		// Create a new integer array. We don't want it to reference the same place
		c.values = new int[values.length];
		
		// Lambdas are cool. Java 8 is cool. This is like looping and setting each value
		Arrays.setAll(c.values, (index) -> values[index]);
		
		return c;
	}
	
	/**
	 * Place the given value into the array, removing the oldest value if the array is full already.
	 * @param value The value to insert
	 */
	public void pushValue(int value)
	{
		// Place it at the index, and move the index forward
		values[index] = value;
		index = (index + 1) % values.length;
	}
	
	/**
	 * Finds the average value of all values in the array. Note that this will 
	 * include default values from initialization of the object. 
	 * @return The average of <b>all</b> values in the array
	 */
	public double average()
	{
		// A chance to try out some Java 8
		return Arrays.stream(values).average().getAsDouble();
	}

}
