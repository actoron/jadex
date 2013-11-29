package jadex.commons.transformation;

import jadex.commons.SUtil;

import java.util.Arrays;


/**
 *  Object holding a multi array as attribute.
 */
public class ArrayHolder
{
	/** The array data. */
	protected int[] data;
	
	/**
	 *  Create an empty array holder.
	 */
	public ArrayHolder()
	{
		// bean constructor
	}
	
	/**
	 * Create a new array holder.
	 */
	public ArrayHolder(int[] data)
	{
		this.data = data;
	}

	/**
	 * Get the data.
	 * 
	 * @return the data.
	 */
	public int[] getData()
	{
		return data;
	}

	/**
	 * Set the data.
	 * 
	 * @param data The data to set.
	 */
	public void setData(int[] data)
	{
		this.data = data;
	}
	
	/** 
	 *  Get the hashcode.
	 */
	public int hashCode()
	{
		return Arrays.hashCode(data);
	}
	
	/**
	 *  Test if two array holder are equal.
	 */
	public boolean equals(Object obj)
	{
		return obj instanceof ArrayHolder && SUtil.arrayEquals(data, ((ArrayHolder)obj).getData());
	}
}
