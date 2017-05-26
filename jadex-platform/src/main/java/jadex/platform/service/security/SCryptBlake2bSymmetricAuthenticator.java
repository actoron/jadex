package jadex.platform.service.security;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.spongycastle.crypto.digests.Blake2bDigest;
import org.spongycastle.crypto.generators.SCrypt;

import jadex.commons.Base64;
import jadex.commons.SUtil;
import jadex.commons.security.ChaChaBlockGenerator;
import jadex.commons.security.SSecurity;

/**
 *  Password-based authentication based on Chacha20 encryption and Blake2b hashing/MACs.
 *
 */
public class SCryptBlake2bSymmetricAuthenticator implements IAuthenticator
{
	/** Authenticator ID. */
	protected static final int AUTHENTICATOR_ID = 0;
	
	/** SCrypt work factor / hardness for password strengthening. */
	protected static final int SCRYPT_N = 32768;
	
	/** Password length warning threshold. */
	protected static final int MIN_PASSWORD_LENGTH = 12;
	
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
		// Generate random salt.
		byte[] salt = new byte[32];
		SSecurity.getSecureRandom().nextBytes(salt);
		
		byte[] dk = deriveKey(key, salt);
		
		// Generate MAC used for authentication.
		Blake2bDigest blake2b = new Blake2bDigest(dk);
		byte[] mac = new byte[blake2b.getDigestSize()];
		blake2b.update(msg, 0, msg.length);
		blake2b.doFinal(mac, 0);
		
		// Generate authenticator: Salt, MAC.
		return SUtil.mergeData(salt, mac);
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
		// Decode token.
		byte[] salt = null;
		byte[] mac = null;
		try
		{
			List<byte[]> splittoken = SUtil.splitData(authtoken);
			if (splittoken.size() != 2)
				return false;
			salt = splittoken.get(0);
			mac = splittoken.get(1);
			
			// Sanity check
			if (salt.length != 32 || mac.length != 64)
				return false;
		}
		catch (Exception e)
		{
			return false;
		}
		
		// Decrypt the random key.
		byte[] dk = deriveKey(key, salt);
		
		// Generate MAC
		Blake2bDigest blake2b = new Blake2bDigest(dk);
		byte[] gmac = new byte[blake2b.getDigestSize()];
		blake2b.update(msg, 0, msg.length);
		blake2b.doFinal(gmac, 0);
		
		return Arrays.equals(gmac, mac);
	}
	
	/**
	 *  Derive the key encryption key from password and salt.
	 *  
	 *  @param password The password.
	 *  @param salt The salt.
	 *  @return Derived key encryption key.
	 */
	protected byte[] deriveKey(Object key, byte[] salt)
	{
		byte[] dk = null;
		if (key instanceof String)
		{
			String pw = (String) key;
			if (pw.length() < MIN_PASSWORD_LENGTH)
				Logger.getLogger("authenticator").warning("Weak password detected: + " + pw + ", please use at least 20 random characters.");
			
			long ts = System.currentTimeMillis();
//			for (int i = 0; i < 100; ++i)
			dk = SCrypt.generate(pw.getBytes(SUtil.UTF8), salt, SCRYPT_N, 8, 1, 64);
			System.out.println(System.currentTimeMillis() - ts);
		}
		else if (key instanceof byte[])
		{
			Blake2bDigest blake2b = new Blake2bDigest((byte[]) key);
			dk = new byte[blake2b.getDigestSize()];
			blake2b.update(salt, 0, salt.length);
			blake2b.doFinal(dk, 0);
		}
		else
			throw new IllegalArgumentException("Symmetric authenticator " + getClass().getSimpleName() + " used for invalid key: " + key);
		
		byte[] test = new byte[32];
		SSecurity.getSecureRandom().nextBytes(test);
		System.out.println(new String(Base64.encode(test), SUtil.ASCII));
		
		return dk;
	}
	
	public static void main(String[] args)
	{
		SCryptBlake2bSymmetricAuthenticator auth = new SCryptBlake2bSymmetricAuthenticator();
		byte[] token = auth.createAuthenticationToken("Test".getBytes(SUtil.UTF8), "sooperdoopersecruit");
		System.out.println("toklen: " + token.length);
		System.out.println(auth.verifyAuthenticationToken("Test".getBytes(SUtil.UTF8), "sooperdoopersecruit", token));
		System.out.println(auth.verifyAuthenticationToken("Test".getBytes(SUtil.UTF8), "superdupersecret", token));
	}
}
