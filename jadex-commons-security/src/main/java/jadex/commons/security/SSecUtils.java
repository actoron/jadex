package jadex.commons.security;

import java.io.File;
import java.io.FileInputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import org.spongycastle.crypto.Mac;
import org.spongycastle.crypto.digests.SHA512Digest;
import org.spongycastle.crypto.engines.AESFastEngine;
import org.spongycastle.crypto.macs.HMac;
import org.spongycastle.crypto.prng.EntropySource;
import org.spongycastle.crypto.prng.EntropySourceProvider;
import org.spongycastle.crypto.prng.SP800SecureRandomBuilder;

/**
 *  Various security methods.
 *
 */
public class SSecUtils
{
	/** Common secure random number source. */
	private static volatile SecureRandom RANDOM;
	
	/**
	 *  Gets access to the common secure PRNG.
	 *  @return Common secure PRNG.
	 */
	public static final SecureRandom getSecureRandom()
	{
		if (RANDOM == null)
		{
			synchronized (SSecUtils.class)
			{
				if (RANDOM == null)
				{
					RANDOM = generateSecureRandom();
				}
			}
		}
		return RANDOM;
	}
	
	/**
	 *  Generates a secure PRNG. The setup attempts to prepare a PRNG that avoids relying
	 *  on a single approach.
	 *  @return Secure PRNG.
	 */
	public static final SecureRandom generateSecureRandom()
	{
		SecureRandom ret = null;
		EntropySourceProvider esp = new EntropySourceProvider()
		{
			/** One random number source. */
			private SecureRandom seedrandom = new SecureRandom();
			
			public EntropySource get(int bitsRequired)
			{
				// Convert to bytes.
				int numbytes = (int) Math.ceil(bitsRequired / 8.0);
				byte[] seed = null;
				File urandom = new File("/dev/urandom");
				if (urandom.exists())
				{
					// Using /dev/urandom as seed source
					byte[] urseed = new byte[numbytes];
					int offset = 0;
					FileInputStream urandomin = null;
					try
					{
						urandomin = new FileInputStream(urandom);
						while (offset != urseed.length)
						{
							offset += urandomin.read(urseed, offset, urseed.length - offset);
						}
						urandomin.close();
						seed = urseed;
					}
					catch (Exception e)
					{
						if (urandomin != null)
						{
							try
							{
								urandomin.close();
							}
							catch (Exception e1)
							{
							}
						}
					}
				}
				
				// For Windows, use Windows API to gather seed data
				String osname = System.getProperty("os.name");
				String osversion = System.getProperty("os.version");
				int minmajwinversion = 6;
				if (osname != null &&
					osname.startsWith("Windows") &&
					osversion != null &&
					osversion.contains(".") &&
					Integer.parseInt(osversion.substring(0, osversion.indexOf('.'))) >= minmajwinversion &&
					seed == null)
				{
					try
					{
						seed = WinCrypt.getRandomFromWindows(numbytes);
					}
					catch (Exception e)
					{
					}
				}
				
				if (seed == null)
				{
					// Fallback to Java mechanism
					seed = seedrandom.generateSeed(numbytes);
				}
				
				/** Beef it up with some Java PRNG data to avoid single failure point */
				final byte[] fseed = seed;
				seed = new byte[fseed.length];
				seedrandom.nextBytes(seed);
				for (int i = 0; i < seed.length; ++i)
				{
					fseed[i] = (byte) (fseed[i] ^ seed[i]);
				}
				
				EntropySource ret = new EntropySource()
				{
					public boolean isPredictionResistant()
					{
						return true;
					}
					
					public byte[] getEntropy()
					{
						return fseed;
					}
					
					public int entropySize()
					{
						return fseed.length * 8;
					}
				};
				return ret;
			}
		};
		
		// Combine AES-CTR-DRBG, AES-HMAC-DRBG and Java SecureRandom
		List<SecureRandom> prngs = new ArrayList<SecureRandom>();
		SP800SecureRandomBuilder builder = new SP800SecureRandomBuilder(esp);
		AESFastEngine eng = new AESFastEngine();
		prngs.add(builder.buildCTR(eng, 256, esp.get(128).getEntropy(), false));
		
		Mac m = new HMac(new SHA512Digest());
		prngs.add(builder.buildHMAC(m, esp.get(512).getEntropy(), false));
		
		prngs.add(new SecureRandom());
		
		final SecureRandom[] randsources = prngs.toArray(new SecureRandom[prngs.size()]);
		ret = new SecureRandom()
		{
			/** ID */
			private static final long serialVersionUID = -3198322750442762871L;
			
			public synchronized void nextBytes(byte[] bytes)
			{
				randsources[0].nextBytes(bytes);
				if (randsources.length > 1)
				{
					byte[] addbytes = new byte[bytes.length];
					for (int i = 1; i < randsources.length; ++i)
					{
						randsources[i].nextBytes(addbytes);
						for (int j = 0; j < bytes.length; ++j)
						{
							bytes[j] = (byte) (bytes[j] ^ addbytes[j]);
						}
					}
				}
			}
		};
		return ret;
	}
}