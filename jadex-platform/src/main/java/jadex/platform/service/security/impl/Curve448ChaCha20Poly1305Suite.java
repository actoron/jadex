package jadex.platform.service.security.impl;

import java.util.Arrays;
import java.util.HashMap;

import org.bouncycastle.util.Pack;

import jadex.bridge.BasicComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.commons.SUtil;
import jadex.commons.security.SSecurity;
import jadex.platform.service.security.SecurityAgent;
import jadex.platform.service.security.auth.AbstractAuthenticationSecret;
import jadex.platform.service.security.auth.KeySecret;
import jadex.platform.service.security.handshake.BasicSecurityMessage;
import jadex.platform.service.security.handshake.InitialHandshakeFinalMessage;

/**
 *  Crypto suite based on Curve448 and ChaCha20-Poly1305 AEAD.
 *
 */
public class Curve448ChaCha20Poly1305Suite extends AbstractChaCha20Poly1305Suite
{
	/** Constant of 5 for Curve448. */
	protected static final byte[] CURVE448_CONST_5 = new byte[56];
	static
	{
		CURVE448_CONST_5[0] = 5;
	}
	
	/**
	 *  Creates the suite.
	 */
	public Curve448ChaCha20Poly1305Suite()
	{
	}
	
	/**
	 *  Gets the encoded public key.
	 * 
	 *  @param ephemeralkey The ephemeral key.
	 */
	protected byte[] getPubKey()
	{
		byte[] pubkey = new byte[56];
		Curve448.eval(pubkey, 0, (byte[]) ephemeralkey, CURVE448_CONST_5);
		
		return pubkey;
	}
	
	/**
	 *  Creates the ephemeral key.
	 *  
	 *  @return The ephemeral key.
	 */
	protected Object createEphemeralKey()
	{
		byte[] ret = new byte[56];
		SSecurity.getSecureRandom().nextBytes(ret);
		return ret;
	}
	
	/**
	 *  Generates the shared public key.
	 *  
	 *  @param remotepubkey The remote public key.
	 *  @return Shared key.
	 */
	protected byte[] generateSharedKey()
	{
		byte[] genkey = new byte[56];
		if (!Curve448.eval(genkey, 0, (byte[]) ephemeralkey, remotepublickey))
			throw new SecurityException("Curve448 Handshake failed");
		return genkey;
	}
	
	/**
	 *  Destroy information.
	 */
	public void destroy()
	{
		if (ephemeralkey != null)
			SSecurity.getSecureRandom().nextBytes((byte[]) ephemeralkey);
		super.destroy();
	}
	
	public static void main(String[] args)
	{
		String skeya = "9a8f4925d1519f5775cf46b04b5800d4ee9ee8bae8bc5565d498c28dd9c9baf574a9419744897391006382a6f127ab1d9ac2d8c0a598726b";
		byte[] keya = new byte[56];
		for (int i = 0; i < skeya.length(); i = i + 2)
		{
			String bytestr = "0x" + skeya.substring(i, i+2);
			int b = Integer.decode(bytestr);
			keya[i >> 1] = (byte) b;
		}
		byte[] pkeya = new byte[56];
		Curve448.eval(pkeya, 0, keya, CURVE448_CONST_5);
		System.out.println(SUtil.hex(pkeya));
		
		String skeyb = "1c306a7ac2a0e2e0990b294470cba339e6453772b075811d8fad0d1d6927c120bb5ee8972b0d3e21374c9c921b09d1b0366f10b65173992d";
		byte[] keyb = new byte[56];
		for (int i = 0; i < skeyb.length(); i = i + 2)
		{
			String bytestr = "0x" + skeyb.substring(i, i+2);
			int b = Integer.decode(bytestr);
			keyb[i >> 1] = (byte) b;
		}
		byte[] pkeyb = new byte[56];
		Curve448.eval(pkeyb, 0, keyb, CURVE448_CONST_5);
		System.out.println(SUtil.hex(pkeyb));
		
		byte[] result = new byte[56];
		Curve448.eval(result, 0, keyb, pkeya);
		System.out.println(SUtil.hex(result));
		
		String teststr = "Ladies and Gentlemen of the class of '99: If I could offer you only one tip for the future, sunscreen would be it.";
		String keystr = "808182838485868788898a8b8c8d8e8f909192939495969798999a9b9c9d9e9f";
		byte[] bkey = new byte[32];
		for (int i = 0; i < keystr.length(); i = i + 2)
		{
			String bytestr = "0x" + keystr.substring(i, i+2);
			int b = Integer.decode(bytestr);
			bkey[i >> 1] = (byte) b;
		}
		int[] key = new int[bkey.length >>> 2];
		Pack.littleEndianToInt(bkey, 0, key);
		
		byte[] res = null;
		
////		byte[] test = new byte[67108864];
//		byte[] test = new byte[1048576];
//		long ts = System.currentTimeMillis();
////		for (int i = 0; i < 1024; ++i)
////			res = chacha20Poly1305Enc(test, key, 7, 0);
//		res = chacha20Poly1305Enc(test, key, 7, 0);
//		for (int i = 0; i < 1024; ++i)
//			chacha20Poly1305Dec(res, key, 7);
//		System.out.println(System.currentTimeMillis() - ts);
		
		res = chacha20Poly1305Enc(teststr.getBytes(SUtil.UTF8), key, 7, 14);
		System.out.println(SUtil.hex(res));
		System.out.println(new String(chacha20Poly1305Dec(res, key, 7), SUtil.UTF8));
//		res[3] = 27;
		System.out.println(chacha20Poly1305Dec(res, key, 7));
		
		Curve448ChaCha20Poly1305Suite s = new Curve448ChaCha20Poly1305Suite();
		System.out.println("----------------------");
		System.out.println(s.isValid(1));
		System.out.println(s.isValid(0));
		System.out.println(s.isValid(1));
		System.out.println(s.isValid(0));
		System.out.println(s.isValid(Integer.MAX_VALUE));
		System.out.println("----------------------");
		
		s = new Curve448ChaCha20Poly1305Suite();
		s.lowid = Long.MAX_VALUE - 1;
		s.highid = s.lowid;
		System.out.println(s.isValid(Long.MAX_VALUE));
		System.out.println(s.isValid(Long.MAX_VALUE + 1));
		System.out.println(s.isValid(Long.MAX_VALUE + 1));
		System.out.println(s.isValid(Long.MAX_VALUE + 5));
		System.out.println(s.isValid(Long.MAX_VALUE + 4));
		System.out.println(s.isValid(Long.MAX_VALUE + 1));
		
		final BasicSecurityMessage[] msg = new BasicSecurityMessage[1];
		SecurityAgent fakeagent = new SecurityAgent()
		{
			{
				networks = new HashMap<String, AbstractAuthenticationSecret>();
//				networks.put("test", new PasswordSecret("password:123456789012345"));
				byte[] key = new byte[32];
				SSecurity.getSecureRandom().nextBytes(key);
				networks.put("test", new KeySecret(key));
			}
			
			public void sendSecurityHandshakeMessage(IComponentIdentifier receiver, Object message)
			{
				msg[0] = (BasicSecurityMessage) message;
			}
			
			public IComponentIdentifier getComponentIdentifier()
			{
				return new BasicComponentIdentifier("TestComp");
			}
		};
		InitialHandshakeFinalMessage ihr = new InitialHandshakeFinalMessage(fakeagent.getComponentIdentifier(), "1234", "");
		
		Curve448ChaCha20Poly1305Suite s1 = new Curve448ChaCha20Poly1305Suite();
		Curve448ChaCha20Poly1305Suite s2 = new Curve448ChaCha20Poly1305Suite();
		System.out.println("HS Step 1:");
		System.out.println(s1.handleHandshake(fakeagent, ihr));
		System.out.println("HS Step 2:");
		System.out.println(s2.handleHandshake(fakeagent, msg[0]));
		System.out.println("HS Step 3:");
		System.out.println(s1.handleHandshake(fakeagent, msg[0]));
		System.out.println("HS Step 4:");
		System.out.println(s2.handleHandshake(fakeagent, msg[0]));
		System.out.println("HS Step 5:");
		System.out.println(s1.handleHandshake(fakeagent, msg[0]));
		System.out.println("Keys:");
		System.out.println(Arrays.toString(s1.key));
		System.out.println(Arrays.toString(s2.key));
	}
}
