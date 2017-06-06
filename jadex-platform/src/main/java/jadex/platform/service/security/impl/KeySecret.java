package jadex.platform.service.security.impl;

import java.util.logging.Logger;

import org.spongycastle.crypto.digests.Blake2bDigest;

import jadex.commons.Base64;
import jadex.commons.SUtil;

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
		this.key = Base64.decode(encodedkey.substring(ind + 1).getBytes(SUtil.UTF8));
		
		if (key.length < MIN_KEY_LENGTH)
			Logger.getLogger("sharedsecret").warning("Weak key detected: + " + key + ", please use at least " + MIN_KEY_LENGTH + " bytes.");
	}
	
	/**
	 *  Creates the secret.
	 */
	public KeySecret(byte[] key)
	{
		this.key = key;
		if (key.length < MIN_KEY_LENGTH)
			Logger.getLogger("sharedsecret").warning("Weak key detected: + " + key + ", please use at least " + MIN_KEY_LENGTH + " bytes.");
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
	public void setPassword(byte[] key)
	{
		this.key = key;
	}
	
	/**
	 *  Derives a key from the shared secret using a salt.
	 *  
	 *  @param keysize The target key size in bytes to generate.
	 *  @param salt Salt to use.
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
		return PREFIX + ":" + new String(Base64.encode(key), SUtil.UTF8);
	}
}
