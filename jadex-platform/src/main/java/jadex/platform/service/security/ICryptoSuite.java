package jadex.platform.service.security;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.types.security.IMsgSecurityInfos;
import jadex.commons.Tuple2;
import jadex.commons.future.IFuture;

public interface ICryptoSuite
{
	/**
	 *  Encrypts and signs the message for a receiver.
	 *  
	 *  @param receiver The receiver.
	 *  @param content The content
	 *  @return Encrypted/signed message.
	 */
	public IFuture<byte[]> encryptAndSign(IComponentIdentifier receiver, byte[] content);
	
	/**
	 *  Decrypt and authenticates the message from a sender.
	 *  
	 *  @param sender The sender.
	 *  @param content The content.
	 *  @return Decrypted/authenticated message or null on invalid message.
	 */
	public IFuture<Tuple2<IMsgSecurityInfos,byte[]>> decryptAndAuth(IComponentIdentifier sender, byte[] content);
}
