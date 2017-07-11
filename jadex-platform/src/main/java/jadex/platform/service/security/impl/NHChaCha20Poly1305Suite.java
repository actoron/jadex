package jadex.platform.service.security.impl;

import java.util.Arrays;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.pqc.crypto.ExchangePair;
import org.bouncycastle.pqc.crypto.newhope.NHAgreement;
import org.bouncycastle.pqc.crypto.newhope.NHExchangePairGenerator;
import org.bouncycastle.pqc.crypto.newhope.NHKeyPairGenerator;
import org.bouncycastle.pqc.crypto.newhope.NHPublicKeyParameters;

import jadex.commons.Tuple2;
import jadex.commons.security.SSecurity;

/**
 *  Crypto suite based on NewHope and ChaCha20-Poly1305 AEAD.
 *
 */
public class NHChaCha20Poly1305Suite extends AbstractChaCha20Poly1305Suite
{
	
	/**
	 *  Creates the suite.
	 */
	public NHChaCha20Poly1305Suite()
	{
	}
	
	/**
	 *  Gets the encoded public key.
	 * 
	 *  @param ephemeralkey The ephemeral key.
	 */
	protected byte[] getPubKey()
	{
		byte[] ret = null;
		if (ephemeralkey instanceof ExchangePair)
		{
			ret = ((NHPublicKeyParameters)((ExchangePair) ephemeralkey).getPublicKey()).getPubData();
		}
		else
		{
			@SuppressWarnings("unchecked")
			Tuple2<byte[], NHAgreement> tup = (Tuple2<byte[], NHAgreement>) ephemeralkey;
			ret = tup.getFirstEntity();
		}
		return ret;
	}
	
	/**
	 *  Creates the ephemeral key.
	 *  
	 *  @return The ephemeral key.
	 */
	protected Object createEphemeralKey()
	{
		Object ret = null;
		if (remotepublickey == null)
		{
			NHKeyPairGenerator kpg = new NHKeyPairGenerator();
			kpg.init(new KeyGenerationParameters(SSecurity.getSecureRandom(), -1));
			
			AsymmetricCipherKeyPair keypair = kpg.generateKeyPair();
			NHAgreement nhagree = new NHAgreement();
			nhagree.init(keypair.getPrivate());
			ret = new Tuple2<byte[], NHAgreement>(((NHPublicKeyParameters) keypair.getPublic()).getPubData(), nhagree);
		}
		else
		{
			NHExchangePairGenerator ekpg2 = new NHExchangePairGenerator(SSecurity.getSecureRandom());
			ret = ekpg2.generateExchange(new NHPublicKeyParameters(remotepublickey));
		}
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
		byte[] ret = null;
		if (ephemeralkey instanceof ExchangePair)
		{
			ret = ((ExchangePair) ephemeralkey).getSharedValue();
		}
		else
		{
			@SuppressWarnings("unchecked")
			NHAgreement nhagree = ((Tuple2<byte[], NHAgreement>) ephemeralkey).getSecondEntity();
			ret = nhagree.calculateAgreement(new NHPublicKeyParameters(remotepublickey));
		}
		return ret;
	}
	
	public static void main(String[] args)
	{
		NHKeyPairGenerator kpg = new NHKeyPairGenerator();
		kpg.init(new KeyGenerationParameters(SSecurity.getSecureRandom(), -1));
		
		AsymmetricCipherKeyPair keypair1 = kpg.generateKeyPair();
		NHAgreement na1 = new NHAgreement();
		na1.init(keypair1.getPrivate());
		
		NHExchangePairGenerator ekpg2 = new NHExchangePairGenerator(SSecurity.getSecureRandom());
		ExchangePair ep2 = ekpg2.generateExchange(keypair1.getPublic());
		byte[] shared2 = ep2.getSharedValue();
		
		byte[] shared1 = na1.calculateAgreement(ep2.getPublicKey());
		
		System.out.println(Arrays.toString(shared1));
		System.out.println(Arrays.toString(shared2));
	}
}
