/**
 * 
 */
package org.activecomponents.udp.symciphers;

import org.spongycastle.crypto.engines.AESFastEngine;
import org.spongycastle.crypto.modes.EAXBlockCipher;
import org.spongycastle.crypto.params.AEADParameters;
import org.spongycastle.crypto.params.KeyParameter;

/**
 *  Class representing AES in EAX mode.
 *
 */
public class EaxAesCipher implements ISymCipher
{
	/** EAX-mode cipher. */
	protected EAXBlockCipher enceax;
	
	/** EAX-mode cipher. */
	protected EAXBlockCipher deceax;
	
	/** The nonce used. */
	protected Nonce nonce;
	
	/** The key. */
	protected byte[] key;
	
	/**
	 *  Creates the cipher with the given key.
	 */
	public EaxAesCipher(byte[] key, Nonce nonce)
	{
//		AESEngine aes = new AESEngine();
		AESFastEngine aes = new AESFastEngine();
		enceax = new EAXBlockCipher(aes);
		this.key = key;
		this.nonce = nonce;
		
		aes = new AESFastEngine();
		deceax = new EAXBlockCipher(aes);
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
		nonce.inc();
		byte[] noncearray = nonce.getAsBytes();
		AEADParameters params = new AEADParameters(new KeyParameter(key), 64, noncearray, new byte[0]);
		enceax.init(true, params);
		if (len < 0)
			len = plain.length - offset;
		int outnum = enceax.getOutputSize(len);
		
		byte[] ret = new byte[outnum + noncearray.length];
		// Bouncy offset handling seems to be bugged in encryption mode, using an array as workaround...
		byte[] enc = new byte[outnum];
		outnum = enceax.processBytes(plain, offset, len, enc, 0);
		try
		{
			enceax.doFinal(enc, outnum);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		
		System.arraycopy(noncearray, 0, ret, 0, noncearray.length);
		System.arraycopy(enc, 0, ret, noncearray.length, enc.length);
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
		byte[] noncearray = new byte[8];//new byte[Nonce.SIZE];
		if (cipher.length <= noncearray.length)
		{
			throw new IllegalArgumentException("Bad cipher text.");
		}
		System.arraycopy(cipher, offset, noncearray, 0, noncearray.length);
		AEADParameters params = new AEADParameters(new KeyParameter(key), 64, noncearray, new byte[0]);
		deceax.init(false, params);
		if (len < 0)
			len = cipher.length - noncearray.length - offset;
		else
			len -= noncearray.length;
		int outnum = deceax.getOutputSize(len);
		byte[] ret = new byte[outnum];
		outnum = deceax.processBytes(cipher, noncearray.length + offset, len, ret, 0);
		try
		{
			deceax.doFinal(ret, outnum);
		}
		catch (Exception e)
		{
			throw new IllegalArgumentException("Bad cipher text.");
		}
		return ret;
	}
	
//	public static void main(String[] args)
//	{
//		byte[] key = new byte[32];
//		random.nextBytes(key);
//		SymCipher sc = new SymCipher(key, random);
//		byte[] pt = "Hello".getBytes(StandardCharsets.UTF_8);
//		System.out.println(Arrays.toString(pt));
//		byte[] ct = sc.encrypt(pt, 1, 3);
//		byte[] expct = new byte[ct.length + 15];
//		System.arraycopy(ct, 0, expct, 1, ct.length);
//		System.out.println("" + pt.length + " " +ct.length + " " + expct.length);
//		System.out.println(Arrays.toString(sc.decrypt(expct, 1, expct.length - 15)));
//	}
}
