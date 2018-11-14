/**
 * 
 */
package org.activecomponents.udp.symciphers;

import java.security.SecureRandom;

import org.activecomponents.udp.SUdpUtil;

/**
 *  Class representing an unsigned 128 bit value used as nonce;
 *
 */
public class Nonce
{
	/** Size of the nonce in bytes. */
	public static final int SIZE = 16;
	
	/** Low part of the nonce. */
	long low;
	
	/** High part of the nonce. */
	long high;
	
	/** Creates nonce with value 0. */
	public Nonce()
	{
	}
	
	/**
	 *  Initializes nonce with given value.
	 *  @param input The value.
	 */
	public Nonce(byte[] input)
	{
		if (input.length != SIZE)
		{
			throw new IllegalArgumentException("Invalid array size.");
		}
		high = SUdpUtil.longFromByteArray(input, 0);
		low = SUdpUtil.longFromByteArray(input, 8);
	}
	
	/**
	 *  Initializes nonce with random value.
	 *  @param secrandom The random number source.
	 */
	public Nonce(SecureRandom secrandom)
	{
		byte[] input = new byte[SIZE];
		secrandom.nextBytes(input);
		high = SUdpUtil.longFromByteArray(input, 0);
		low = SUdpUtil.longFromByteArray(input, 8);
	}
	
	/**
	 *  Increases the value by one.
	 */
	public void inc()
	{
		++low;
		if (low == Long.MIN_VALUE)
		{
			++high;
		}
	}
	
	/**
	 *  Decreases the value by one.
	 */
	public void dec()
	{
		--low;
		if (low == Long.MAX_VALUE)
		{
			--high;
		}
	}
	
	/**
	 *  Returns the value as a byte array.
	 *  @return Byte array representing the value, big endian.
	 */
	public byte[] getAsBytes()
	{
		byte[] ret = new byte[SIZE];
		SUdpUtil.longIntoByteArray(ret, 0, high);
		SUdpUtil.longIntoByteArray(ret, 8, low);
		return ret;
	}
}
