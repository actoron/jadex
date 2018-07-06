package org.activecomponents.udp.asymciphers;

/**
 *  Interface for key exchanges.
 *
 */
public interface IKeyExchange
{
	/**
	 *  Returns the generated public key.
	 *  
	 *  @return Generated public key.
	 */
	public byte[] getPublicKey();
	
	/**
	 *  Generates the shared symmetric key given the remote public key.
	 *  
	 * 	@param remotekey The remote public key.
	 *  @return Shared symmetric key;
	 */
	public byte[] generateSymKey(byte[] remotekey);
}
