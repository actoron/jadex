/**
 * 
 */
package org.activecomponents.udp;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;

/**
 * Interface for providing randomly generated public/private key pairs.
 *
 */
public interface IKeyProvider
{
	/**
	 *  Returns a random key pair.
	 *  
	 *  @return Key pair.
	 */
	public AsymmetricCipherKeyPair getKeyPair();
}
