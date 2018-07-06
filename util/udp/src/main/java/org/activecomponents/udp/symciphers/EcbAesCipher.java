package org.activecomponents.udp.symciphers;

import org.spongycastle.crypto.BlockCipher;
import org.spongycastle.crypto.engines.AESFastEngine;
import org.spongycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.spongycastle.crypto.params.KeyParameter;

/**
 *  Implementation of AES in ECB mode. Insecure in most cases, for testing purposes only.
 *
 */
public class EcbAesCipher implements ISymCipher
{
	/** ECB-mode padded cipher. */
	protected PaddedBufferedBlockCipher encecb;
	
	/** ECB-mode padded cipher. */
	protected PaddedBufferedBlockCipher dececb;
	
	/** The key. */
	protected byte[] key;
	
	/**
	 *  Creates the cipher with the given key.
	 */
	public EcbAesCipher(byte[] key, Nonce nonce)
	{
		BlockCipher aes = new AESFastEngine();
//		BlockCipher aes = new AesWrapper();
		encecb = new PaddedBufferedBlockCipher(aes);
		this.key = key;
		encecb.init(true, new KeyParameter(key));
		aes = new AESFastEngine();
		dececb = new PaddedBufferedBlockCipher(aes);
		dececb.init(false, new KeyParameter(key));
	}
	
	/**
	 *  Encrypts a message.
	 *  
	 *  @param plain Plain text.
	 *  @return Cipher text.
	 */
	public synchronized byte[] encrypt(byte[] plain)
	{
		return encrypt(plain, 0, -1);
	}
	
	/**
	 *  Encrypts a message.
	 *  
	 *  @param plain Plain text.
	 *  @param offset Offset where the plain text starts.
	 *  @param len Length of the plain text, -1 for rest of the array.
	 *  @return Cipher text.
	 */
	public synchronized byte[] encrypt(byte[] plain, int offset, int len)
	{
		if (len < 0)
			len = plain.length - offset;
		int outnum = encecb.getOutputSize(len);
		
		byte[] ret = new byte[outnum];
		outnum = encecb.processBytes(plain, offset, len, ret, 0);
		try
		{
			encecb.doFinal(ret, outnum);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		return ret;
	}
	
	/**
	 *  Decrypts a message.
	 *  
	 * 	@param cipher Cipher text.
	 * 	@return Plain text.
	 */
	public synchronized byte[] decrypt(byte[] cipher)
	{
		return decrypt(cipher, 0, -1);
	}
	
	/**
	 *  Decrypts a message.
	 *  
	 * 	@param cipher Cipher text.
	 *  @param offset Offset where the cipher text starts.
	 *  @param len Length of the cipher text, -1 for rest of the array.
	 * 	@return Plain text.
	 */
	public synchronized byte[] decrypt(byte[] cipher, int offset, int len)
	{
		if (len < 0)
			len = cipher.length - offset;
		int outnum = dececb.getOutputSize(len);
		byte[] ret = new byte[outnum];
		outnum = dececb.processBytes(cipher, offset, len, ret, 0);
		try
		{
			dececb.doFinal(ret, outnum);
		}
		catch (Exception e)
		{
			throw new IllegalArgumentException("Bad cipher text.");
		}
		return ret;
	}
}
