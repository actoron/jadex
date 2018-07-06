/**
 * 
 */
package org.activecomponents.udp;

/**
 *  Implements a verification-less approach (MITM attacks are possible!)
 *
 */
public class NullKeyVerifier implements IKeyVerifier
{
	public static final byte MAGIC_BYTE = 0x53;
	
	/**
	 *  Signs the public key.
	 * 
	 *  @param input Raw data of the key.
	 *  @return Signed key.
	 */
	public byte[] sign(byte[] input)
	{
		return new byte[0];
	}
	
	/**
	 *  Verifies a signed input..
	 * 
	 *  @param input Raw data sent by remote.
	 *  @param signature The signature.
	 *  @return True, if the input could be verified, false if not.
	 */
	public boolean verify(byte[] input, byte[] signature)
	{
		return true;
	}
}
