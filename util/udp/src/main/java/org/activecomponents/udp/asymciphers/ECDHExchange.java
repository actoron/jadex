package org.activecomponents.udp.asymciphers;

import java.util.Arrays;

import org.activecomponents.udp.SUdpUtil;
import org.spongycastle.crypto.agreement.ECDHBasicAgreement;
import org.spongycastle.crypto.generators.ECKeyPairGenerator;
import org.spongycastle.crypto.params.ECDomainParameters;
import org.spongycastle.crypto.params.ECKeyGenerationParameters;
import org.spongycastle.math.ec.ECPoint;
import org.spongycastle.math.ec.custom.djb.Curve25519;
import org.spongycastle.util.encoders.Hex;

public class ECDHExchange extends AbstractKeyExchange
{
	/** Domain Parameter G */
	protected static final String G = "042AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAD245A20AE19A1B8A086B4E01EDD2C7748D14C923D4D7E6D7C61B229E9C5A27ECED3D9";
	
	/**
	 *  Creates a Curve25519-based Exchange Mechanism.
	 */
	public ECDHExchange()
	{
		Curve25519 curve = new Curve25519();
		ECPoint g = curve.decodePoint(Hex.decode(G));
		ECDomainParameters dp = new ECDomainParameters(curve, g, curve.getOrder());
		ECKeyGenerationParameters keyparams = new ECKeyGenerationParameters(dp, SUdpUtil.getSecRandom());
		ECKeyPairGenerator generator=new ECKeyPairGenerator();
		generator.init(keyparams);
		keypair = generator.generateKeyPair();
		agreement = new ECDHBasicAgreement();
		agreement.init(keypair.getPrivate());
	}
	
	/**
	 * 
	 */
	public static void main(String[] args)
	{
		ECDHExchange ex1 = new ECDHExchange();
		ECDHExchange ex2 = new ECDHExchange();
		byte[] pk1 = ex1.getPublicKey();
		byte[] pk2 = ex2.getPublicKey();
		byte[] genkey1 = ex1.generateSymKey(pk2);
		byte[] genkey2 = ex2.generateSymKey(pk1);
		
		System.out.println(genkey1.length);
		System.out.println(genkey2.length);
		System.out.println(Arrays.equals(genkey1, genkey2));
	}
}
