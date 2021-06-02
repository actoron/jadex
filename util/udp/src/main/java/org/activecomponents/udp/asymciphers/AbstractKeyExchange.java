package org.activecomponents.udp.asymciphers;

import java.io.IOException;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.BasicAgreement;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.crypto.util.SubjectPublicKeyInfoFactory;

public abstract class AbstractKeyExchange implements IKeyExchange
{
	/** Generated key pair. */
	protected AsymmetricCipherKeyPair keypair;
	
	/** Key Agreement. */
	protected BasicAgreement agreement;
	
	/**
	 *  Generates the shared symmetric key given the remote public key.
	 *  
	 * 	@param remotekey The remote public key.
	 *  @return Shared symmetric key;
	 */
	public byte[] generateSymKey(byte[] remotekey)
	{
		AsymmetricKeyParameter decodedkey = null;
		try
		{
			decodedkey = PublicKeyFactory.createKey(remotekey);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
		byte[] ret = agreement.calculateAgreement(decodedkey).toByteArray();
		SHA256Digest digest = new SHA256Digest();
		digest.update(ret, 0, ret.length);
		ret = new byte[digest.getDigestSize()];
		digest.doFinal(ret, 0);
		return ret;
	}
	
	/**
	 *  Returns the generated public key.
	 *  
	 *  @return Generated public key.
	 */
	public byte[] getPublicKey()
	{
		try
		{
			return SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(keypair.getPublic()).getEncoded();
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
}
