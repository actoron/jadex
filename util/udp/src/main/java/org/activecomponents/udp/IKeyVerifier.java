/**
 * 
 */
package org.activecomponents.udp;

/**
 *  Class verifying the authenticity of remote public keys.
 *
 */
public interface IKeyVerifier
{
	/**
	 *  Signs the public key.
	 * 
	 *  @param input Raw data of the key.
	 *  @return Signature of key.
	 */
	public byte[] sign(byte[] input);
	
	/**
	 *  Verifies a signed input..
	 * 
	 *  @param input Raw data sent by remote.
	 *  @param signature The signature.
	 *  @return True, if the input could be verified, false if not.
	 */
	public boolean verify(byte[] input, byte[] signature);
}
