package jadex.platform.service.security.auth;

/**
 *  Authentication based on a shared secret.
 *
 */
public abstract class SharedSecret extends AbstractAuthenticationSecret
{
	/**
	 *  Tests if the secret can be used for signing or, alternatively, verification only.
	 *  @return True, if the secret can be used for signing.
	 */
	public boolean canSign()
	{
		return true;
	}
	
	/**
	 *  Derives a key from the shared secret using a salt.
	 *  
	 *  @param keysize The target key size in bytes to generate.
	 *  @param salt Salt to use.
	 *  @param df Used derivation function.
	 *  @return Derived key.
	 */
	public abstract byte[] deriveKey(int keysize, byte[] salt, int df);
}
