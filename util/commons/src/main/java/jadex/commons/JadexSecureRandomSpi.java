package jadex.commons;

import java.lang.reflect.Method;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.SecureRandomSpi;
import java.security.Security;
import java.security.Provider.Service;

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
			String alg = "SHA1PRNG";
			Provider p = Security.getProvider("SUN");
			if (p != null)
			{
				for (Service serv : p.getServices())
				{
		            if (serv.getType().equals("SecureRandom"))
		            {
		                alg = serv.getAlgorithm();
		                break;
		            }
		        }
			}
			try
			{
				SecureRandom r = SecureRandom.getInstance(alg);
				ret = r.generateSeed(numbytes);
			}
			catch (NoSuchAlgorithmException e1)
			{
				throw SUtil.throwUnchecked(e1);
			}
		}
		
		return ret;
	}
}
