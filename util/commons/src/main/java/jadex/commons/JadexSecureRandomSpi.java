package jadex.commons;

import java.lang.reflect.Method;
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
		byte[] ret = new byte[numbytes];
		try
		{
			Class<?> ssecurity = Class.forName("jadex.commons.security.SSecurity");
			Method getEntropySource = ssecurity.getDeclaredMethod("getEntropySource", new Class[0]);
			Method getEntropy = ssecurity.getDeclaredMethod("getEntropy", byte[].class);
			Object es = getEntropySource.invoke(null);
			getEntropy.invoke(es, ret);
		}
		catch (Exception e)
		{
			ret = SUtil.getJavaDefaultSecureRandom().generateSeed(numbytes);
		}
		
		return ret;
	}
}
