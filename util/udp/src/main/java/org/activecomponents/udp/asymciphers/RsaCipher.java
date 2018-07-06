/**
 * 
 */
package org.activecomponents.udp.asymciphers;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.Arrays;

import org.activecomponents.udp.DaemonThreadExecutor;
import org.activecomponents.udp.IThreadExecutor;
import org.activecomponents.udp.SUdpUtil;
import org.spongycastle.crypto.AsymmetricBlockCipher;
import org.spongycastle.crypto.AsymmetricCipherKeyPair;
import org.spongycastle.crypto.InvalidCipherTextException;
import org.spongycastle.crypto.Signer;
import org.spongycastle.crypto.digests.SHA512Digest;
import org.spongycastle.crypto.encodings.OAEPEncoding;
import org.spongycastle.crypto.engines.AESLightEngine;
import org.spongycastle.crypto.engines.RSAEngine;
import org.spongycastle.crypto.modes.CBCBlockCipher;
import org.spongycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.spongycastle.crypto.params.AsymmetricKeyParameter;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.crypto.params.ParametersWithIV;
import org.spongycastle.crypto.params.RSAKeyParameters;
import org.spongycastle.crypto.signers.PSSSigner;

/**
 * @author jander
 *
 */
public class RsaCipher implements IAsymCipher
{
	/** Keypair */
	protected AsymmetricCipherKeyPair keypair;
	
	/** Public key of the communication partner. */
	protected AsymmetricKeyParameter foreignpublic;
	
	/** PSS engine. */
	protected Signer pssrsa;
	
	/** OAEP engine. */
	protected AsymmetricBlockCipher oaeprsa;
	
	/**
	 * 
	 */
	public RsaCipher(RsaKeyProvider kp)
	{
		this(kp.getKeyPair());
	}
	
	/**
	 * 
	 */
	public RsaCipher(AsymmetricCipherKeyPair keypair)
	{
		this.keypair = keypair;
		AsymmetricBlockCipher rawrsa = new RSAEngine();
		oaeprsa = new OAEPEncoding(rawrsa);
		SHA512Digest digest = new SHA512Digest();
		pssrsa = new PSSSigner(rawrsa, digest, digest.getByteLength());
	}
	
	public static void main(String[] args)
	{
		RsaKeyProvider kp = new RsaKeyProvider();
		IThreadExecutor texec = new DaemonThreadExecutor();
		kp.start(texec);
		RsaCipher rsa = new RsaCipher(kp);
		
		String str1 = "Hello";
		String str2 = "World";
		Charset utf8 = null;
		try
		{
			utf8 = Charset.forName("UTF-8");
		}
		catch (Exception e)
		{
		}
		byte[] ct1 = rsa.encrypt(str1.getBytes(utf8), false);
		System.out.println(ct1.length);
		byte[] ct2 = rsa.encrypt(str2.getBytes(utf8), false);
		byte[] key = new byte[32];
		
		for (int i = 0; i < 10; ++i)
		{
			System.out.println(i);
			SUdpUtil.getSecRandom().nextBytes(key);
			byte[] signedmsg = rsa.sign(key);
			if (SUdpUtil.getSecRandom().nextDouble() < 0.5)
			{
				// corrupt
				byte[] corbyte = new byte[1];
				SUdpUtil.getSecRandom().nextBytes(corbyte);
//				signedmsg[(int)(signedmsg.length * rand.nextDouble())] = corbyte[0];
				signedmsg[0] = 96;
				System.out.println("Corrupted");
			}
			byte[] msg = rsa.verify(signedmsg, false);
			System.out.println("EQUALS: " + Arrays.equals(msg, key));
		}
		byte[] enckey = rsa.encrypt(key, false);
		byte[] deckey = rsa.decrypt(enckey);
		
		String dec2 = new String(rsa.decrypt(ct2), utf8);
		String dec1 = new String(rsa.decrypt(ct1), utf8);
		
		System.out.println(dec1 + " " + dec2);
		System.out.println(Arrays.equals(key, deckey));
		try
		{
			while (true)
			{
				Thread.sleep(5000);
				if (SUdpUtil.getSecRandom().nextDouble() < 0.5)
					kp.getKeyPair();
			}
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 *  Sets the public key of the communication partner.
	 * 
	 * 	@param foreignpublic The public key.
	 * 	@return False, if the key was invalid.
	 */
	public boolean setForeign(byte[] foreignpublic)
	{
		this.foreignpublic = null;
		if (foreignpublic == null)
		{
			return true;
		}
		
		this.foreignpublic = byteArrayToKey(foreignpublic);
		if (this.foreignpublic == null)
		{
			return false;
		}
		
		return true;
	}
	
	/**
	 *  Encrypts a message using a public key.
	 *  
	 * 	@param plain The plain text.
	 *  @param foreign True, if the foreign public key should be used,
	 *  			   false, if the innate one should be used.
	 * @return The cipher text.
	 */
	public byte[] encrypt(byte[] plain, boolean foreign)
	{
//		System.out.println("IN: " + Arrays.toString(plain));
		PaddedBufferedBlockCipher symciph = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESLightEngine()));
		byte[] symkey = new byte[32];
		byte[] symiv = new byte[16];
		SUdpUtil.getSecRandom().nextBytes(symkey);
		SUdpUtil.getSecRandom().nextBytes(symiv);
		ParametersWithIV symparams = new ParametersWithIV(new KeyParameter(symkey), symiv);
		symciph.init(true, symparams);
		byte[] symout = new byte[symciph.getOutputSize(plain.length)];
		try
		{
			symciph.doFinal(symout, symciph.processBytes(plain, 0, plain.length, symout, 0));
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
			return null;
		}
		
		oaeprsa.init(true, foreign? foreignpublic : keypair.getPublic());
		
		byte[] encsymkey;
		try
		{
			encsymkey = oaeprsa.processBlock(symkey, 0, symkey.length);
		}
		catch (InvalidCipherTextException e)
		{
			throw new RuntimeException(e);
		}
		
		byte[] ret = new byte[2 + encsymkey.length + symiv.length + symout.length];
		int offset = 0;
		SUdpUtil.shortIntoByteArray(ret, offset, (short) encsymkey.length);
		offset += 2;
		System.arraycopy(encsymkey, 0, ret, offset, encsymkey.length);
		offset += encsymkey.length;
		System.arraycopy(symiv, 0, ret, offset, symiv.length);
		offset += symiv.length;
		System.arraycopy(symout, 0, ret, offset, symout.length);
		
		return ret;
	}
	
	/**
	 *  Decrypts a message.
	 * 
	 *  @param ciphertext The cipher text.
	 * 	@return The plain text or null if decryption failed.
	 */
	public byte[] decrypt(byte[] ciphertext)
	{
		byte[] encsymkey = new byte[SUdpUtil.shortFromByteArray(ciphertext, 0)];
		int offset = 2;
		System.arraycopy(ciphertext, offset, encsymkey, 0, encsymkey.length);
		offset += encsymkey.length;
		byte[] symiv = new byte[16];
		System.arraycopy(ciphertext, offset, symiv, 0, symiv.length);
		offset += symiv.length;
		byte[] encmsg = new byte[ciphertext.length - offset];
		System.arraycopy(ciphertext, offset, encmsg, 0, encmsg.length);
		
		oaeprsa.init(false, keypair.getPrivate());
		byte[] symkey;
		try
		{
			symkey = oaeprsa.processBlock(encsymkey, 0, encsymkey.length);
		}
		catch (InvalidCipherTextException e)
		{
//			throw new RuntimeException(e);
			return null;
		}
		
		PaddedBufferedBlockCipher symciph = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESLightEngine()));
		ParametersWithIV symparams = new ParametersWithIV(new KeyParameter(symkey), symiv);
		symciph.init(false, symparams);
		byte[] ret = new byte[symciph.getOutputSize(encmsg.length)];
		try
		{
			int total = symciph.processBytes(encmsg, 0, encmsg.length, ret, 0);
			total += symciph.doFinal(ret, total);
			byte[] tmp = ret;
			ret = new byte[total];
			System.arraycopy(tmp, 0, ret, 0, ret.length);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
//		System.out.println("OUT: " + Arrays.toString(ret));
		return ret;
	}
	
	/**
	 *  Signs a message.
	 * 
	 *  @param msg The message.
	 *  @return The message with signature.
	 */
	public byte[] sign(byte[] msg)
	{
		byte[] ret = null;
		pssrsa.init(true, keypair.getPrivate());
		byte[] sig = null;
		try
		{
			pssrsa.update(msg, 0, msg.length);
			sig = pssrsa.generateSignature();
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
		
		short siglen = (short) sig.length;
		ret = new byte[sig.length + msg.length + 2];
		SUdpUtil.shortIntoByteArray(ret, 0, siglen);
		System.arraycopy(sig, 0, ret, 2, sig.length);
		System.arraycopy(msg, 0, ret, sig.length + 2, msg.length);
		
		return ret;
	}
	
	/**
	 *  Verifies a message.
	 * 
	 *  @param msg The message with signature.
	 *  @param foreign True, if the foreign public key should be used,
	 *  			   false, if the innate one should be used.
	 *  @return The message or null if the verification failed.
	 */
	public byte[] verify(byte[] msgwithsig, boolean foreign)
	{
		byte[] ret = null;
		try
		{
			short siglen = SUdpUtil.shortFromByteArray(msgwithsig, 0);
			if (siglen > 4096 || siglen < 64)
			{
//				throw new IllegalArgumentException("Signature length invalid.");
				return null;
			}
			
			byte[] sig = new byte[siglen];
			System.arraycopy(msgwithsig, 2, sig, 0, sig.length);
			byte[] msg = new byte[msgwithsig.length - sig.length - 2];
			System.arraycopy(msgwithsig, siglen + 2, msg, 0, msg.length);
			
			AsymmetricKeyParameter key = foreign? foreignpublic : keypair.getPublic();
			pssrsa.init(false, key);
			pssrsa.update(msg, 0, msg.length);
			
			if (pssrsa.verifySignature(sig))
			{
				ret = msg;
//				System.out.println("Sig verified.");
			}
//			else
//			{
//				System.out.println("Sig failed.");
//			}
		}
		catch (Exception e)
		{
		}
		
		return ret;
	}
	
	/**
	 *  Signs a message and encrypts a message.
	 * 
	 *  @param msg The message.
	 *  @param encryptwithforeign True, if the foreign public key should be used for encryption,
	 *  						  false, if the innate one should be used.
	 *  @return The message with signature, encrypted.
	 */
	public byte[] signAndEncrypt(byte[] msg, boolean encryptwithforeign)
	{
		byte[] signedmsg = sign(msg);
		return encrypt(signedmsg, encryptwithforeign);
	}
	
	/**
	 *  Decrypts a message and verifies the signature.
	 * 
	 *  @param msg The signed and encrypted message.
	 *  @param encryptwithforeign True, if the foreign public key should be used for verification,
	 *  						  false, if the innate one should be used.
	 *  @return The message, null if verification failed.
	 */
	public byte[] decryptAndVerify(byte[] ciphertext, boolean verifywithforeign)
	{
		byte[] ret = null;
		
		try
		{
			byte[] signedmsg = decrypt(ciphertext);
			ret = verify(signedmsg, verifywithforeign);
		}
		catch (Exception e)
		{
		}
		return ret;
	}
	
	/**
	 *  Converts a key to binary form.
	 * 
	 * 	@param key The key.
	 * 	@return The binary form of the key.
	 */
	public byte[] keyToByteArray(AsymmetricKeyParameter key)
	{
		RSAKeyParameters rsakey = (RSAKeyParameters) key;
		byte[] mod = rsakey.getModulus().toByteArray();
		byte[] exp = rsakey.getExponent().toByteArray();
		byte[] ret = new byte[exp.length + mod.length + 3];
		ret[0] = (byte) (key.isPrivate() ? 1 : 0);
		SUdpUtil.shortIntoByteArray(ret, 1, (short) mod.length);
		System.arraycopy(mod, 0, ret, 3, mod.length);
		System.arraycopy(exp, 0, ret, 3 + mod.length, exp.length);
		return ret;
	}
	
	/**
	 *  Converts a key from binary form.
	 * 
	 * 	@param enckey The encoded key.
	 * 	@return The key.
	 */
	public AsymmetricKeyParameter byteArrayToKey(byte[] enckey)
	{
		AsymmetricKeyParameter ret = null;
		try
		{
			boolean isprivate = enckey[0] == 1;
			short modlen = SUdpUtil.shortFromByteArray(enckey, 1);
			if (modlen > 4096 || modlen < 16)
			{
				return null;
			}
			
			byte[] mod = new byte[modlen];
			System.arraycopy(enckey, 3, mod, 0, mod.length);
			byte[] exp = new byte[enckey.length - modlen - 3];
			System.arraycopy(enckey, 3 + modlen, exp, 0, exp.length);
			
			ret = new RSAKeyParameters(isprivate, new BigInteger(mod), new BigInteger(exp));
		}
		catch (Exception e)
		{
		}
		return ret;
	}
}
