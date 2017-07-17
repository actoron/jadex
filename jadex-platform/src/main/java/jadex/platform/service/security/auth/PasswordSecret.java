package jadex.platform.service.security.auth;

import java.util.logging.Logger;

import org.bouncycastle.crypto.generators.SCrypt;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

import jadex.commons.SUtil;
import jadex.commons.security.SSecurity;

/**
 *  A secret password used for authentication.
 *
 */
public class PasswordSecret extends SharedSecret
{
	/** Prefix used to encode secret type as strings.*/
	public static final String PREFIX = "pw";
	
	/** Password length warning threshold. */
	protected static final int WARN_PASSWORD_LENGTH = 12;
	
	/** SCrypt work factor / hardness for password strengthening. */
	protected static final int SCRYPT_N = 16384;
	
	/** SCrypt block size. */
	protected static final int SCRYPT_R = 8;
	
	/** SCrypt parallelization. */
	protected static final int SCRYPT_P = 4;
	
	/** The password. */
	protected String password;
	
	/**
	 *  Creates the secret.
	 */
	public PasswordSecret()
	{
	}
	
	/**
	 *  Creates the secret.
	 */
	public PasswordSecret(String encodedpassword)
	{
		this(encodedpassword, true);
	}
	
	/**
	 *  Creates the secret.
	 */
	public PasswordSecret(String password, boolean encoded)
	{
		
		
		if (encoded)
		{
			int ind = password.indexOf(':');
			String prefix = password.substring(0, ind);
			
			if (!PREFIX.startsWith(prefix))
				throw new IllegalArgumentException("Not a password secret: " + password);
			this.password = password.substring(ind + 1);
		}
		
		if (this.password.length() < WARN_PASSWORD_LENGTH)
			Logger.getLogger("sharedsecret").warning("Weak password detected: + " + this.password + ", please use at least " + WARN_PASSWORD_LENGTH + " random characters.");
	}
	
	/**
	 *  Gets the password.
	 *  
	 *  @return The password.
	 */
	public String getPassword()
	{
		return password;
	}
	
	/**
	 *  Sets the password.
	 *  
	 *  @param password The password.
	 */
	public void setPassword(String password)
	{
		this.password = password;
	}
	
	/**
	 *  Gets the key derivation parameters.
	 *  @return Key derivation parameters.
	 */
	public byte[] getKdfParams()
	{
		byte[] ret = new byte[16];
		
		Pack.intToLittleEndian(SCRYPT_N, ret, 4);
		Pack.intToLittleEndian(SCRYPT_R, ret, 8);
		Pack.intToLittleEndian(SCRYPT_P, ret, 12);
		
		return ret;
	}
	
	/**
	 *  Derives a key from the password with appropriate hardening.
	 *  
	 *  @param keysize The target key size in bytes to generate.
	 *  @param salt Salt to use.
	 *  @return Derived key.
	 */
	public byte[] deriveKey(int keysize, byte[] salt)
	{
		return SCryptParallel.generate(password.getBytes(SUtil.UTF8), salt, SCRYPT_N, SCRYPT_R, SCRYPT_P, keysize);
	}
	
	/**
	 *  Derives a key from the password with appropriate hardening.
	 *  
	 *  @param keysize The target key size in bytes to generate.
	 *  @param salt Salt to use.
	 *  @param dfparams Key derivation parameters.
	 *  @return Derived key.
	 */
	public byte[] deriveKey(int keysize, byte[] salt, byte[] dfparams)
	{
		if (dfparams.length != 16)
			return null;
		
		int n = Pack.littleEndianToInt(dfparams, 4);
		int r = Pack.littleEndianToInt(dfparams, 8);
		int p = Pack.littleEndianToInt(dfparams, 12);
		
		return SCryptParallel.generate(password.getBytes(SUtil.UTF8), salt, n, r, p, keysize);
	}
	
	/** 
	 *  Creates encoded secret.
	 *  
	 *  @return Encoded secret.
	 */
	public String toString()
	{
		return PREFIX + ":" + password;
	}
	
	public static void main(String[] args)
	{
		PasswordSecret pws = new PasswordSecret("pw:sdjfsd@#@FIsad90sj");
		byte[] salt = new byte[32];
		SSecurity.getSecureRandom().nextBytes(salt);
		
		long ts = System.currentTimeMillis();
//		pws.deriveKey(64, salt);
		byte[] single = SCrypt.generate(pws.getPassword().getBytes(SUtil.UTF8), salt, SCRYPT_N, SCRYPT_R, SCRYPT_P, 64);
		System.out.println(System.currentTimeMillis() - ts);
		
		ts = System.currentTimeMillis();
//		pws.deriveKey(64, salt);
		byte[] par = SCryptParallel.generate(pws.getPassword().getBytes(SUtil.UTF8), salt, SCRYPT_N, SCRYPT_R, SCRYPT_P, 64);
		System.out.println(System.currentTimeMillis() - ts);
		
		System.out.println(Arrays.areEqual(single, par));
	}
}
