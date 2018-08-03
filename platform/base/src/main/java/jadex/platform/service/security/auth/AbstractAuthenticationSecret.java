package jadex.platform.service.security.auth;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import jadex.commons.security.PemKeyPair;
import jadex.commons.security.SSecurity;

/**
 *  Class representing a secret used for authentication.
 *
 */
public abstract class AbstractAuthenticationSecret implements Cloneable
{
	/** Types of authentication secret. */
	public static Map<String, Class<?>> SECRET_TYPES = Collections.synchronizedMap(new HashMap<String, Class<?>>());
	static
	{
		AbstractAuthenticationSecret.SECRET_TYPES.put(PasswordSecret.PREFIX, PasswordSecret.class);
		AbstractAuthenticationSecret.SECRET_TYPES.put(KeySecret.PREFIX, KeySecret.class);
		AbstractAuthenticationSecret.SECRET_TYPES.put(X509PemStringsSecret.PREFIX, X509PemStringsSecret.class);
		AbstractAuthenticationSecret.SECRET_TYPES.put(X509PemFilesSecret.PREFIX, X509PemFilesSecret.class);
	}
	
	/**
	 *  Tests if the secret can be used for signing or, alternatively, verification only.
	 *  @return True, if the secret can be used for signing.
	 */
	public abstract boolean canSign();
	
	/**
	 *  Clone support.
	 */
	public Object clone() throws CloneNotSupportedException
	{
		return fromString(toString(), true);
	}
	
	/**
	 *  Check if equal (String representation matches).
	 */
	public boolean equals(Object obj)
	{
		if (obj instanceof AbstractAuthenticationSecret && toString().equals(obj.toString()))
			return true;
		
		return super.equals(obj);
	}
	
	/**
	 *  Hashcode is string representation hash code.
	 */
	public int hashCode()
	{
		return toString().hashCode();
	}
	
	/**
	 *  Creates an authentication secret based on a pem key pair.
	 *  
	 *  @param keypair The key pair.
	 *  @param ignorekey If true, the secret will not include the key (verify-only).
	 *  @param chain Rest of the authentication chain.
	 *  @return The secret.
	 */
	public static final AbstractX509PemSecret fromKeyPair(PemKeyPair keypair, boolean ignorekey, PemKeyPair... chain)
	{
		String cert = keypair.getCertificate();
		if (chain != null && chain.length > 0)
		{
			for (PemKeyPair pair : chain)
				cert += pair.getCertificate();
		}
		X509PemStringsSecret ret = new X509PemStringsSecret(cert, ignorekey ? null : keypair.getKey());
		return ret;
	}
	
	/**
	 *  Decodes a secret from a string.
	 *  
	 *  @param secret The secret as string.
	 *  @return The instantiated secret.
	 */
	public static final AbstractAuthenticationSecret fromString(String secret)
	{
		return fromString(secret, false);
	}
	
	/**
	 *  Decodes a secret from a string.
	 *  
	 *  @param secret The secret as string.
	 *  @param strict If false, interpret invalid string as a password.
	 *  @return The instantiated secret.
	 */
	public static final AbstractAuthenticationSecret fromString(String secret, boolean strict)
	{
		if (secret == null)
			throw new IllegalArgumentException("Secret is null: " + secret);
		
		AbstractAuthenticationSecret ret = null;
		
		int ind = secret.indexOf(':');
		if (ind > 0)
		{
			String prefix = secret.substring(0, ind);
			Class<?> secrettype = SECRET_TYPES.get(prefix);
			if (secrettype != null)
			{
				try
				{
					Constructor<?> con = secrettype.getConstructor(String.class);
					ret = (AbstractAuthenticationSecret) con.newInstance(secret);
				}
				catch (Exception e)
				{
				}
			}
		}
		
		if (ret == null)
		{
			if (strict)
				throw new IllegalArgumentException("Could not decode authentication secret: " + secret);
			else
				ret = new PasswordSecret(secret, false);
		}
		
		return ret;
	}
	
	public static void main(String[] args)
	{
		PemKeyPair ca = SSecurity.createTestCACert();
		PemKeyPair cert = SSecurity.createTestCert(ca);
		AbstractAuthenticationSecret clientsecret = AbstractAuthenticationSecret.fromKeyPair(ca, true);
		AbstractAuthenticationSecret serversecret = AbstractAuthenticationSecret.fromKeyPair(cert, false, ca);
		System.out.println(clientsecret.toString());
		System.out.println(serversecret.toString());
	}
}
