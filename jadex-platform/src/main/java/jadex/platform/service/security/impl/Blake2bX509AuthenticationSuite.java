package jadex.platform.service.security.impl;

import java.util.Arrays;

import org.spongycastle.crypto.digests.Blake2bDigest;
import org.spongycastle.util.Pack;

import jadex.commons.SUtil;
import jadex.commons.security.SSecurity;
import jadex.platform.service.security.AbstractAuthenticationSecret;

/**
 *  Symmetric authentication based on Blake2b MACs.
 */
public class Blake2bX509AuthenticationSuite implements IAuthenticationSuite
{
	/** Authentication Suite ID. */
	protected static final int AUTH_SUITE_ID = 93482103;
	
	/** Size of the MAC. */
	protected static final int MAC_SIZE = 64;
	
	/** Size of the derived key. */
	protected static final int DERIVED_KEY_SIZE = 64;
	
	/** Size of the salt. */
	protected static final int SALT_SIZE = 32;
	
	/**
	 *  Gets the authentication suite ID.
	 *  
	 *  @return The authentication suite ID.
	 */
	public int getId()
	{
		return AUTH_SUITE_ID;
	}
	
	/**
	 *  Creates an authentication token for a message based on an abstract 
	 *  implementation-dependent "key".
	 *  
	 *  @param msg The message being authenticated.
	 *  @param secret The secret used for authentication.
	 *  @return Authentication token.
	 */
	public byte[] createAuthenticationToken(byte[] msg, AbstractAuthenticationSecret secret)
	{
		if (!(secret instanceof SharedSecret))
			throw new IllegalArgumentException("Authenticator cannot handle non-shared secrets: " + secret);
		SharedSecret ssecret = (SharedSecret) secret;
		
		// Generate random salt.
		byte[] salt = new byte[SALT_SIZE];
		SSecurity.getSecureRandom().nextBytes(salt);
		
		byte[] dk = ssecret.deriveKey(DERIVED_KEY_SIZE, salt);
		
		// Hash the message.
		byte[] msghash = getMessageHash(msg);
		
		// Generate MAC used for authentication.
		Blake2bDigest blake2b = new Blake2bDigest(dk);
		byte[] ret = new byte[SALT_SIZE + MAC_SIZE + 4];
		Pack.intToLittleEndian(AUTH_SUITE_ID, ret, 0);
		System.arraycopy(salt, 0, ret, 4, salt.length);
		blake2b.update(msghash, 0, msghash.length);
		blake2b.doFinal(ret, salt.length + 4);
		
		// Generate authenticator: Salt, MAC.
		return ret;
	}
	
	/**
	 *  Creates an authentication token for a message based on an abstract 
	 *  implementation-dependent "key".
	 *  
	 *  @param msg The message being authenticated.
	 *  @param secret The secret used for authentication.
	 *  @param authtoken Authentication token.
	 *  @return True if authenticated, false otherwise.
	 */
	public boolean verifyAuthenticationToken(byte[] msg, AbstractAuthenticationSecret secret, byte[] authtoken)
	{
		if (!(secret instanceof SharedSecret))
			throw new IllegalArgumentException("Authenticator cannot handle non-shared secrets: " + secret);
		SharedSecret ssecret = (SharedSecret) secret;
		
		if (authtoken.length != SALT_SIZE + MAC_SIZE + 4)
			return false;
		
		if (Pack.littleEndianToInt(authtoken, 0) != AUTH_SUITE_ID)
			return false;
		
		// Decode token.
		byte[] salt = new byte[SALT_SIZE];
		byte[] mac = new byte[MAC_SIZE];
		System.arraycopy(authtoken, 4, salt, 0, salt.length);
		System.arraycopy(authtoken, SALT_SIZE + 4, mac, 0, mac.length);
		
		// Decrypt the random key.
		byte[] dk = ssecret.deriveKey(DERIVED_KEY_SIZE, salt);
		
		byte[] msghash = getMessageHash(msg);
		
		// Generate MAC
		Blake2bDigest blake2b = new Blake2bDigest(dk);
		byte[] gmac = new byte[MAC_SIZE];
		blake2b.update(msghash, 0, msghash.length);
		blake2b.doFinal(gmac, 0);
		
		return Arrays.equals(gmac, mac);
	}
	
	/**
	 *  Create message hash.
	 * 
	 *  @param msg The message.
	 *  @return Hashed message.
	 */
	protected static final byte[] getMessageHash(byte[] msg)
	{
		Blake2bDigest blake2b = new Blake2bDigest(512);
		byte[] msghash = new byte[64];
		blake2b.update(msg, 0, msg.length);
		blake2b.doFinal(msghash, 0);
		return msghash;
	}
	
	/**
	 *  Main
	 */
	public static void main(String[] args)
	{
		Blake2bX509AuthenticationSuite auth = new Blake2bX509AuthenticationSuite();
		byte[] token = auth.createAuthenticationToken("Test".getBytes(SUtil.UTF8), new PasswordSecret("password:sooperdoopersecruit"));
		System.out.println("toklen: " + token.length);
		System.out.println(auth.verifyAuthenticationToken("Test".getBytes(SUtil.UTF8), new PasswordSecret("password:sooperdoopersecruit"), token));
		System.out.println(auth.verifyAuthenticationToken("Test".getBytes(SUtil.UTF8), new PasswordSecret("password:superdupersecret"), token));
	}
}
