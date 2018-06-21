package jadex.commons.security;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

/**
 *  Test for the entropy source functionality.
 *
 */

public class EntropySourceTest
{
	/**
	 *  Tests if the entropy source delivers a usable output.
	 */
	@Test
	public void testEntropySource()
	{
		byte[] empty = new byte[64];
		byte[] output = new byte[64];
		SSecurity.getEntropySource().getEntropy(output);
		System.out.println("Entropy Source Output: " + Arrays.toString(output));
		Assert.assertTrue(!Arrays.equals(output, empty));
	}
	
	/**
	 *  Tests if the entropy source fallback delivers a usable output.
	 */
	@Test
	public void testEntropySourceFallback()
	{
		// Use smaller size since the Java-based fallback on Linux uses
		// /dev/random, bad Oracle!
		byte[] empty = new byte[12];
		byte[] output = new byte[12];
		SSecurity.TEST_ENTROPY_FALLBACK = true;
		SSecurity.getEntropySource().getEntropy(output);
		SSecurity.TEST_ENTROPY_FALLBACK = false;
		System.out.println("Entropy Source Fallback Output: " + Arrays.toString(output));
		Assert.assertTrue(!Arrays.equals(output, empty));
	}
}