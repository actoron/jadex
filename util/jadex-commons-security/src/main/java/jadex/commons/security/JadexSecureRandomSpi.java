package jadex.commons.security;

import java.security.SecureRandomSpi;

/**
 *  Wrapper for the Jadex Secure Random implementation.
 *
 */
public class JadexSecureRandomSpi extends SecureRandomSpi
{
	/** Generated ID. */
	private static final long serialVersionUID = 2803171956482478139L;
	
	/**
	 *  Create the wrapper.
	 */
	public JadexSecureRandomSpi()
	{
//		System.out.println("Wrapper created");
	}
	
	protected void engineSetSeed(byte[] seed)
	{
	}

	protected void engineNextBytes(byte[] bytes)
	{
//		System.out.println("Called next bytes");
		SSecurity.SECURE_RANDOM.nextBytes(bytes);
	}

	protected byte[] engineGenerateSeed(int numbytes)
	{
		byte[] ret = new byte[numbytes];
		SSecurity.getEntropySource().getEntropy(ret);
		return ret;
	}
}
