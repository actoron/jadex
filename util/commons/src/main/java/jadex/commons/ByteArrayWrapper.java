package jadex.commons;

import java.util.Arrays;

/**
 *  Wrapper to allow byte arrays as hash keys.
 *
 */
public class ByteArrayWrapper
{
	/** The wrapped byte array. */
	protected byte[] array;
	
	/**
	 *  Creates the wrapper.
	 */
	public ByteArrayWrapper()
	{
	}
	
	/**
	 *  Creates the wrapper.
	 */
	public ByteArrayWrapper(byte[] array)
	{
		this.array = array;
	}

	/**
	 *  Gets the array.
	 *
	 *  @return The array.
	 */
	public byte[] getArray()
	{
		return array;
	}

	/**
	 *  Sets the array.
	 *
	 *  @param array The array.
	 */
	public void setArray(byte[] array)
	{
		this.array = array;
	}
	
	/**
	 *  Creates a hash code.
	 */
	public int hashCode()
	{
		if (array == null)
			return 0;
		
		return Arrays.hashCode(array);
	}
	
	/**
	 *  Compares two arrays.
	 */
	public boolean equals(Object obj)
	{
		boolean ret = false;
		if (obj instanceof ByteArrayWrapper)
		{
			ByteArrayWrapper other = (ByteArrayWrapper) obj;
			if (other.getArray() == null && array == null)
			{
				ret = true;
			}
			else
			{
				ret = Arrays.equals(array, other.getArray());
			}
		}
		return ret;
	}
}
