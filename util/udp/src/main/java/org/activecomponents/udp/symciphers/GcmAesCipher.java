/**
 * 
 */
package org.activecomponents.udp.symciphers;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

import org.spongycastle.crypto.engines.AESFastEngine;
import org.spongycastle.crypto.modes.GCMBlockCipher;
import org.spongycastle.crypto.params.AEADParameters;
import org.spongycastle.crypto.params.KeyParameter;

//import com.actoron.udp.SUdpUtil;

/**
 *  Class representing AES in GCM mode.
 *
 */
public class GcmAesCipher implements ISymCipher
{
	/** GCM-mode cipher. */
	protected GCMBlockCipher gcm;
	
	/** The nonce used. */
	protected Nonce nonce;
	
	/** The key. */
	protected byte[] key;
	
	/**
	 *  Creates the cipher with the given key.
	 */
	public GcmAesCipher(byte[] key, Nonce nonce)
	{
		this.key = key;
		AESFastEngine aes = new AESFastEngine();
		gcm = new GCMBlockCipher(aes);
		this.nonce = nonce;
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
		AEADParameters params = new AEADParameters(new KeyParameter(key), 128, noncearray, new byte[0]);
		gcm.init(true, params);
		if (len < 0)
			len = plain.length - offset;
//		int outnum = gcm.getOutputSize(plain.length);
		int outnum = gcm.getOutputSize(len);
		
		byte[] ret = new byte[outnum + noncearray.length];
		// Bouncy offset handling seems to be bugged in encryption mode, using an array as workaround...
//		byte[] enc = new byte[outnum];
//		outnum = gcm.processBytes(plain, offset, len, enc, 0);
		outnum = gcm.processBytes(plain, offset, len, ret, noncearray.length);
		try
		{
//			gcm.doFinal(enc, outnum);
			gcm.doFinal(ret, outnum + noncearray.length);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		
		System.arraycopy(noncearray, 0, ret, 0, noncearray.length);
//		System.arraycopy(enc, 0, ret, noncearray.length, enc.length);
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
		byte[] noncearray = new byte[Nonce.SIZE];
		if (cipher.length <= noncearray.length)
		{
			throw new IllegalArgumentException("Bad cipher text.");
		}
		System.arraycopy(cipher, offset, noncearray, 0, noncearray.length);
		AEADParameters params = new AEADParameters(new KeyParameter(key), 128, noncearray, new byte[0]);
		gcm.init(false, params);
		if (len < 0)
			len = cipher.length - noncearray.length - offset;
		else
			len -= noncearray.length;
		int outnum = gcm.getOutputSize(len);
		byte[] ret = new byte[outnum];
//		outnum = gcm.processBytes(cipher, noncearray.length, cipher.length - noncearray.length, ret, 0);
		outnum = gcm.processBytes(cipher, noncearray.length + offset, len, ret, 0);
		try
		{
			gcm.doFinal(ret, outnum);
		}
		catch (Exception e)
		{
			throw new IllegalArgumentException("Bad cipher text.");
		}
		return ret;
	}
	
	public static void main(String[] args)
	{
		byte[] key = new byte[32];
		(new SecureRandom()).nextBytes(key);
		GcmAesCipher sc = new GcmAesCipher(key, new Nonce(new SecureRandom()));
		byte[] pt = "Hello".getBytes(StandardCharsets.UTF_8);
		System.out.println(Arrays.toString(pt));
		//byte[] ct = sc.encrypt(pt, 1, 3);
		byte[] ct = sc.encrypt(pt);
		byte[] expct = new byte[ct.length + 15];
		System.arraycopy(ct, 0, expct, 1, ct.length);
		System.out.println("" + pt.length + " " +ct.length + " " + expct.length);
		//System.out.println(Arrays.toString(sc.decrypt(expct, 1, expct.length - 15)));
		System.out.println(Arrays.toString(sc.decrypt(ct)));
		byte[] big = new byte[100*1024*1024];
		Random rnd = new Random();
		rnd.nextBytes(big);
//		EcbAesCipher ecb = new EcbAesCipher(key);
		long ts = System.currentTimeMillis();
//		ecb.encrypt(big);
		sc.encrypt(big);
		ts = System.currentTimeMillis() - ts;
		System.out.println(ts);
		System.out.println(100/(ts/1000.0));
	}
}
