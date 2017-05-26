package jadex.platform.service.security;

import java.util.Arrays;
import java.util.List;

import org.spongycastle.crypto.digests.Blake2bDigest;

import jadex.commons.SUtil;
import jadex.commons.security.ChaChaBlockGenerator;
import jadex.commons.security.SSecurity;

/**
 *  Password-based authentication based on Chacha20 encryption and Blake2b hashing/MACs.
 *
 */
public class Chacha20Blake2bPasswordAuthenticator
{
	/** Authenticator ID. */
	protected static final int AUTHENTICATOR_ID = 0;
	
	/**
	 *  Returns the authenticator type ID.
	 *  
	 *  @return The authenticator type ID.
	 */
	public int getAuthenticatorTypeId()
	{
		return AUTHENTICATOR_ID;
	}
	
	/**
	 *  Creates an authentication token for a message based on an abstract 
	 *  implementation-dependent "key".
	 *  
	 *  @param msg The message being authenticated.
	 *  @param key The key used for authentication.
	 *  @return Authentication token.
	 */
	public byte[] createAuthenticationToken(byte[] msg, Object key)
	{
		String password = convertKey(key);
		
		// Generate random salt.
		byte[] salt = new byte[32];
		SSecurity.getSecureRandom().nextBytes(salt);
		
		// Generate random key
		byte[] rkey = new byte[64];
		SSecurity.getSecureRandom().nextBytes(rkey);
		
		// Generate MAC used for authentication based on the random key.
		Blake2bDigest blake2b = new Blake2bDigest(rkey);
		byte[] mac = new byte[blake2b.getDigestSize()];
		blake2b.update(msg, 0, msg.length);
		blake2b.doFinal(mac, 0);
		
		byte[] kek = deriveKeyEncryptionKey(password, salt);
		rkey = chacha20EncryptDecrypt(rkey, kek);
		
		// Generate authenticator: Salt, encrypted rkey, MAC.
		return SUtil.mergeData(salt, rkey, mac);
	}
	
	/**
	 *  Creates an authentication token for a message based on an abstract 
	 *  implementation-dependent "key".
	 *  
	 *  @param msg The message being authenticated.
	 *  @param key The key used for authentication.
	 *  @param authtoken Authentication token.
	 *  @return True if authenticated, false otherwise.
	 */
	public boolean verifyAuthenticationToken(byte[] msg, Object key, byte[] authtoken)
	{
		String password = convertKey(key);
		
		// Decode token.
		byte[] salt = null;
		byte[] rkey = null;
		byte[] mac = null;
		try
		{
			List<byte[]> splittoken = SUtil.splitData(authtoken);
			if (splittoken.size() != 3)
				return false;
			salt = splittoken.get(0);
			rkey = splittoken.get(1);
			mac = splittoken.get(2);
			
			// Sanity check
			if (salt.length != 32 || rkey.length != 64 || mac.length != 64)
				return false;
		}
		catch (Exception e)
		{
			return false;
		}
		
		// Decrypt the random key.
		byte[] kek = deriveKeyEncryptionKey(password, salt);
		rkey = chacha20EncryptDecrypt(rkey, kek);
		
		// Generate MAC
		Blake2bDigest blake2b = new Blake2bDigest(rkey);
		byte[] gmac = new byte[blake2b.getDigestSize()];
		blake2b.update(msg, 0, msg.length);
		blake2b.doFinal(gmac, 0);
		
		return  Arrays.equals(gmac, gmac);
	}
	
	/** 
	 *  Checks and converts the key to password string.
	 *  
	 *  @param key The key.
	 *  @return The password.
	 */
	protected String convertKey(Object key)
	{
		if (!(key instanceof String))
			throw new IllegalArgumentException("Password authenticator " + getClass().getSimpleName() + " used for non-password key: " + key);
		
		return (String) key;
	}
	
	/**
	 *  Derive the key encryption key from password and salt.
	 *  
	 *  @param password The password.
	 *  @param salt The salt.
	 *  @return Derived key encryption key.
	 */
	protected byte[] deriveKeyEncryptionKey(String password, byte[] salt)
	{
		// Derive key using password and salt.
		Blake2bDigest blake2b = new Blake2bDigest(password.getBytes(SUtil.UTF8));
		byte[] tmp = new byte[blake2b.getDigestSize()];
		blake2b.update(salt, 0, salt.length);
		blake2b.doFinal(tmp, 0);
		
		// Generate key encryption by shrinking the derived key to ChaCha20-size.
		blake2b = new Blake2bDigest(384);
		byte[] kek = new byte[blake2b.getDigestSize()];
		blake2b.update(tmp, 0, tmp.length);
		blake2b.doFinal(kek, 0);
		
		return kek;
	}
	
	/**
	 *  Processes the random key with the key encryption key using ChaCha20.
	 *  Operation is symmetric / used for both encryption/decrpytion.
	 *  
	 *  @param rkey Randomly generated key.
	 *  @param kek Key encryption key.
	 *  @return Encrpyted / Decrypted key.
	 */
	byte[] chacha20EncryptDecrypt(byte[] rkey, byte[] kek)
	{
		// Encrypt / Decrypt random key with key encryption key.
		ChaChaBlockGenerator chacha20blockgen = new ChaChaBlockGenerator();
		chacha20blockgen.initState(kek);
		byte[] block = new byte[64];
		chacha20blockgen.nextBlock(block);
		SSecurity.xor(rkey, block);
		return rkey;
	}
}
