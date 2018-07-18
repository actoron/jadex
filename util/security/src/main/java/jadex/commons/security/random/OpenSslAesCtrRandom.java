package jadex.commons.security.random;

import java.security.SecureRandom;

import jadex.commons.security.SSecurity;

/**
 *  Access to OpenSSL AES counter mode random.
 *
 */
public class OpenSslAesCtrRandom extends SecureRandom
{
	/** ID */
	private static final long serialVersionUID = -3198322342777501L;
	
	OpenSslAesCtr aesctr = new OpenSslAesCtr();
	{
		if (!isEnabled())
			throw new IllegalStateException("OpenSslAesCtrRandom: OpenSSL not found.");
		reseed(null);
	}
	
	private byte[] buffer;
	private int pos;
	private int numbytes = 0;
	
	public synchronized void nextBytes(byte[] bytes)
	{
		int opos = 0;
		while (bytes.length - opos > buffer.length - pos)
		{
			int len = buffer.length - pos;
			System.arraycopy(buffer, pos, bytes, opos, len);
			opos += len;
			pos += len;
			
			if (numbytes < 0)
			{
				byte[] carryover = aesctr.nextBytes();
				reseed(carryover);
			}
			else
			{
				buffer = aesctr.nextBytes();
				numbytes += buffer.length;
				pos = 0;
			}
		}
		
		int len = bytes.length - opos;
		System.arraycopy(buffer, pos, bytes, opos, len);
		pos += len;
	}
	
	/**
	 *  Reseeds the PRNG.
	 */
	public void reseed(byte[] carryover)
	{
		byte[] key = new byte[32];
		SSecurity.getEntropySource().getEntropy(key);
		byte[] iv = new byte[16];
		SSecurity.getEntropySource().getEntropy(iv);
		
		if (carryover != null)
		{
			byte[] cokey = new byte[32];
			System.arraycopy(carryover, 0, cokey, 0, cokey.length);
			SSecurity.xor(key, cokey);
			byte[] coiv = new byte[16];
			System.arraycopy(carryover, cokey.length, coiv, 0, coiv.length);
			SSecurity.xor(iv, coiv);
		}
		
		aesctr.init(key, iv);
		buffer = aesctr.nextBytes();
		pos = 0;
		numbytes = buffer.length;
	}
	
	/**
	 *  Generates a seed value from OS source.
	 */
	public byte[] generateSeed(int numbytes)
	{
		byte[] ret = new byte[numbytes];
		SSecurity.getEntropySource().getEntropy(ret);
		return ret;
	}
	
	/** Checks if the library is in a usable state. */
	public static final boolean isEnabled()
	{
		return OpenSslAesCtr.isEnabled();
	}
}
