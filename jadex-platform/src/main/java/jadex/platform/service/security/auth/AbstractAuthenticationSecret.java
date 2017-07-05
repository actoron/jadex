package jadex.platform.service.security.auth;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *  Class representing a secret used for authentication.
 *
 */
public abstract class AbstractAuthenticationSecret
{
	/** Types of authentication secret. */
	public static Map<String, Class<?>> SECRET_TYPES = Collections.synchronizedMap(new HashMap<String, Class<?>>());
	static
	{
		AbstractAuthenticationSecret.SECRET_TYPES.put(PasswordSecret.PREFIX, PasswordSecret.class);
		AbstractAuthenticationSecret.SECRET_TYPES.put(KeySecret.PREFIX, KeySecret.class);
		AbstractAuthenticationSecret.SECRET_TYPES.put(X509PemFileSecret.PREFIX, X509PemFileSecret.class);
		AbstractAuthenticationSecret.SECRET_TYPES.put(X509PemSecret.PREFIX, X509PemSecret.class);
	}
	
	/**
	 *  Tests if the secret can be used for signing or, alternatively, verification only.
	 *  @return True, if the secret can be used for signing.
	 */
	public abstract boolean canSign();
	
	/**
	 *  Decodes a secret from a string.
	 *  
	 *  @param secret The secret as string.
	 *  @return The instantiated secret.
	 */
	public static final AbstractAuthenticationSecret fromString(String secret)
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
			throw new IllegalArgumentException("Could not decode authentication secret: " + secret);
		
		return ret;
	}
}
