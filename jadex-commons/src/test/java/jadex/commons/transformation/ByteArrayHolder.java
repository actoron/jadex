package jadex.commons.transformation;

import java.util.Arrays;

import jadex.commons.SUtil;

public class ByteArrayHolder
{
	//-------- attributes --------
	
	/** The  data. */
	protected byte[]	data;
	
	//-------- constructors --------
	
	/**
	 *  Bean constructor.
	 */
	public ByteArrayHolder()
	{
	}
	
	//-------- bean accessors --------
	
	/**
	 *  The authentication data.
	 *  The data is calculated by building an MD5 hash from the target platform password and the timestamp.
	 */
	public byte[]	getData()
	{
		return data;
	}
	
	/**
	 *  Set the data.
	 */
	public void	setData(byte[] data)
	{
		this.data	= data;
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
		return obj instanceof ByteArrayHolder && SUtil.arrayEquals(data, ((ByteArrayHolder)obj).getData());
	}
}
