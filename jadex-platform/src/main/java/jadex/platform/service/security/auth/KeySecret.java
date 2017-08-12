package jadex.platform.service.security.auth;

import java.util.logging.Logger;

import org.bouncycastle.crypto.digests.Blake2bDigest;

import jadex.commons.Base64;
import jadex.commons.SUtil;
import jadex.commons.security.SSecurity;

/**
 *  Authentication secret based on a shared key.
 *
 */
public class KeySecret extends SharedSecret
{
	/** Prefix used to encode secret type as strings. */
	public static final String PREFIX = "key";
	
	/** Key length warning threshold. */
	protected static final int MIN_KEY_LENGTH = 16;
	
	/** The password. */
	protected byte[] key;
	
	/**
	 *  Creates the secret.
	 */
	public KeySecret()
	{
	}
	
	/**
	 *  Creates the secret.
	 */
	public KeySecret(String encodedkey)
	{
		int ind = encodedkey.indexOf(':');
		String prefix = encodedkey.substring(0, ind);
		if (!PREFIX.equals(prefix))
			throw new IllegalArgumentException("Not a key secret: " + encodedkey);
		
		String keystr = encodedkey.substring(ind + 1);
		this.key = Base64.decodeNoPadding(keystr.getBytes(SUtil.UTF8));
		
		if (key.length < MIN_KEY_LENGTH)
			Logger.getLogger("sharedsecret").warning("Weak key detected: + " + key + ", please use at least " + MIN_KEY_LENGTH + " bytes.");
	}
	
	/**
	 *  Creates the secret.
	 */
	public KeySecret(byte[] key)
	{
		this(key, true);
	}
	
	/**
	 *  Creates the secret.
	 */
	public KeySecret(byte[] key, boolean warn)
	{
		this.key = key;
		if (key.length < MIN_KEY_LENGTH)
		{
			String weak = "Weak key detected: + " + key + ", please use at least " + MIN_KEY_LENGTH + " bytes.";
			if (warn)
				Logger.getLogger("sharedsecret").warning(weak);
			else
				throw new IllegalArgumentException(weak);
		}
	}
	
	/**
	 *  Gets the key.
	 *  
	 *  @return The key.
	 */
	public byte[] getKey()
	{
		return key;
	}
	
	/**
	 *  Sets the key.
	 *  
	 *  @param key The key.
	 */
	public void setKey(byte[] key)
	{
		this.key = key;
	}
	
	/**
	 *  Derives a key from the shared secret using a salt.
	 *  
	 *  @param keysize The target key size in bytes to generate.
	 *  @param salt Salt to use.
	 *  @param df Used derivation function.
	 *  @return Derived key.
	 */
	public byte[] deriveKey(int keysize, byte[] salt)
	{
		Blake2bDigest blake2b = new Blake2bDigest(keysize << 3);
		byte[] dk = new byte[blake2b.getDigestSize()];
		blake2b.update(salt, 0, salt.length);
		blake2b.update(key, 0, key.length);
		blake2b.doFinal(dk, 0);
		
		return dk;
	}
	
	/** 
	 *  Creates encoded secret.
	 *  
	 *  @return Encoded secret.
	 */
	public String toString()
	{
		return PREFIX + ":" + new String(Base64.encodeNoPadding(key), SUtil.UTF8);
	}
	
	/**
	 *  Creates a random shared key.
	 * 
	 *  @return Random shared key.
	 */
	public static final KeySecret createRandom()
	{
		byte[] rawkey = new byte[32];
		SSecurity.getSecureRandom().nextBytes(rawkey);
		return new KeySecret(rawkey);
	}
	
	/**
	 *  Creates a random shared key.
	 * 
	 *  @return Random shared key.
	 */
	public static final String createRandomAsString()
	{
		return createRandom().toString();
	}
}
