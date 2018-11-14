package jadex.commons;

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
		SUtil.getSecureRandom().nextBytes(bytes);
	}

	protected byte[] engineGenerateSeed(int numbytes)
	{
		byte[] ret = null;
		if (SUtil.SECURE_RANDOM != null)
			ret = SUtil.getSecureRandom().generateSeed(numbytes);
		else
			ret = SUtil.getJavaDefaultSecureRandom().generateSeed(numbytes);
		
		return ret;
	}
}
