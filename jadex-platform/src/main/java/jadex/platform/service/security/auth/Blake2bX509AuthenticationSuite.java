package jadex.platform.service.security.auth;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.crypto.digests.Blake2bDigest;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.util.Pack;
import org.bouncycastle.util.io.pem.PemObject;

import jadex.commons.SUtil;
import jadex.commons.security.SSecurity;
import jadex.platform.service.security.impl.AbstractX509PemSecret;

/**
 *  Symmetric authentication based on Blake2b MACs.
 */
public class Blake2bX509AuthenticationSuite implements IAuthenticationSuite
{
	/** Authentication Suite ID. */
	protected static final int AUTH_SUITE_ID = 93482103;
	
	/** Key derivation function to use. */
	protected static final int KDF_ID = 0;
	
	/** Size of the MAC. */
	protected static final int MAC_SIZE = 64;
	
	/** Size of the derived key. */
	protected static final int DERIVED_KEY_SIZE = 64;
	
	/** Size of the salt. */
	protected static final int SALT_SIZE = 32;
	
	/**
	 *  Gets the authentication suite ID.
	 *  
	 *  @return The authentication suite ID.
	 */
	public int getId()
	{
		return AUTH_SUITE_ID;
	}
	
	/**
	 *  Creates an authentication token for a message based on an abstract 
	 *  implementation-dependent "key".
	 *  
	 *  @param msg The message being authenticated.
	 *  @param secret The secret used for authentication.
	 *  @return Authentication token.
	 */
	public byte[] createAuthenticationToken(byte[] msg, AbstractAuthenticationSecret secret)
	{
		byte[] ret = null;
		
		// Generate random salt.
		byte[] salt = new byte[SALT_SIZE];
		SSecurity.getSecureRandom().nextBytes(salt);
		
		// Hash the message.
		byte[] msghash = getMessageHash(msg, salt);
		
		if (secret instanceof SharedSecret)
		{
			SharedSecret ssecret = (SharedSecret) secret;
			
			byte[] dk = ssecret.deriveKey(DERIVED_KEY_SIZE, salt, KDF_ID);
			
			// Generate MAC used for authentication.
			Blake2bDigest blake2b = new Blake2bDigest(dk);
			ret = new byte[SALT_SIZE + MAC_SIZE + 8];
			Pack.intToLittleEndian(AUTH_SUITE_ID, ret, 0);
			Pack.intToLittleEndian(KDF_ID, ret, 4);
			System.arraycopy(salt, 0, ret, 8, salt.length);
			blake2b.update(msghash, 0, msghash.length);
			blake2b.doFinal(ret, salt.length + 8);
		}
		else if (secret instanceof AbstractX509PemSecret)
		{
			AbstractX509PemSecret aps = (AbstractX509PemSecret) secret;
			if (!aps.canSign())
				throw new IllegalArgumentException("Secret cannot be used to sign: " + aps);
			
			byte[] sig = SSecurity.signWithPEM(msghash, aps.openCertificate(), aps.openPrivateKey());
			ret = new byte[sig.length + SALT_SIZE + 8];
			Pack.intToLittleEndian(AUTH_SUITE_ID, ret, 0);
			System.arraycopy(salt, 0, ret, 8, salt.length);
			System.arraycopy(sig, 0, ret, 8 + salt.length, sig.length);
		}
		else
		{
			throw new IllegalArgumentException("Unknown secret type: " + secret);
		}
		
		// Generate authenticator: Salt, MAC.
		return ret;
	}
	
	/**
	 *  Creates an authentication token for a message based on an abstract 
	 *  implementation-dependent "key".
	 *  
	 *  @param msg The message being authenticated.
	 *  @param secret The secret used for authentication.
	 *  @param authtoken Authentication token.
	 *  @return True if authenticated, false otherwise.
	 */
	public boolean verifyAuthenticationToken(byte[] msg, AbstractAuthenticationSecret secret, byte[] authtoken)
	{
		boolean ret = false;
		try
		{
			SharedSecret ssecret = (SharedSecret) secret;
			
			if (authtoken.length != SALT_SIZE + MAC_SIZE + 8)
				return false;
			
			if (Pack.littleEndianToInt(authtoken, 0) != AUTH_SUITE_ID)
				return false;
			
			// Decode token.
			byte[] salt = new byte[SALT_SIZE];
			System.arraycopy(authtoken, 8, salt, 0, salt.length);
			
			byte[] msghash = getMessageHash(msg, salt);
			
			if (secret instanceof SharedSecret)
			{
				byte[] mac = new byte[MAC_SIZE];
				System.arraycopy(authtoken, SALT_SIZE + 8, mac, 0, mac.length);
				
				int kdfid = Pack.littleEndianToInt(authtoken, 4);
				
				// Decrypt the random key.
				byte[] dk = ssecret.deriveKey(DERIVED_KEY_SIZE, salt, kdfid);
				
				// Generate MAC
				Blake2bDigest blake2b = new Blake2bDigest(dk);
				byte[] gmac = new byte[MAC_SIZE];
				blake2b.update(msghash, 0, msghash.length);
				blake2b.doFinal(gmac, 0);
				ret = Arrays.equals(gmac, mac);
			}
			else if (secret instanceof AbstractX509PemSecret)
			{
				AbstractX509PemSecret aps = (AbstractX509PemSecret) secret;
				byte[] sig = new byte[authtoken.length - 8 - salt.length];
				System.arraycopy(authtoken, 8 + salt.length, sig, 0, sig.length);
				
				SSecurity.verifyWithPEM(msghash, sig, aps.openTrustAnchorCert());
			}
			else
			{
				Logger.getLogger("authentication").warning("Unknown secret type: " + secret);
			}
		}
		catch (Exception e)
		{
		}
		
		return ret;
	}
	
	/**
	 *  Create message hash.
	 * 
	 *  @param msg The message.
	 *  @return Hashed message.
	 */
	protected static final byte[] getMessageHash(byte[] msg, byte[] salt)
	{
		Blake2bDigest blake2b = new Blake2bDigest(512);
		byte[] msghash = new byte[64];
		blake2b.update(msg, 0, msg.length);
		blake2b.update(salt, 0, salt.length);
		blake2b.doFinal(msghash, 0);
		return msghash;
	}
	
	/**
	 *  Main
	 */
	public static void main(String[] args) throws Exception
	{
		String home = System.getProperty("user.home") + File.separator;
		byte[] tsig = SSecurity.signWithPEM("TestMessage".getBytes(SUtil.UTF8), new FileInputStream(home + "rsa.pem"), new FileInputStream(home + "rsa.key"));
		System.out.println("VerifyTest: " + SSecurity.verifyWithPEM("TestMessage".getBytes(SUtil.UTF8), tsig, new FileInputStream(home + "rootCA.pem")));
//		byte[] tsig = signWithPEM("TestMessage".getBytes(SUtil.UTF8), new FileInputStream(home + "test.pem"), new FileInputStream(home + "test.key"));
//		System.out.println("VerifyTest: " + verifyWithPEM("TestMessage".getBytes(SUtil.UTF8), tsig, new FileInputStream(home + "trusted.pem")));
		System.out.println("TSIGLEN " + tsig.length);
		
		FileReader fr = new FileReader(home + "rsa.key");
		@SuppressWarnings("resource")
		PEMParser r = new PEMParser(fr);;
		Object object = r.readObject();;
		r.close();
//		object = r.readObject();
//		ASN1StreamParser p = new ASN1StreamParser(new ByteArrayInputStream(object.getContent()));
//		DERSequenceParser dp = (DERSequenceParser) p.readObject();
		
		System.out.println(object.getClass());
		PEMKeyPair keypair = (PEMKeyPair) object;
		System.out.println("AAA" + keypair.getPublicKeyInfo());
		PrivateKeyInfo pki = keypair.getPrivateKeyInfo();
		System.out.println("PKI: " + pki);
//		AsymmetricKeyParameter akp = PrivateKeyFactory.createKey(pki);
		
		
		r = new PEMParser(new FileReader(home + "test.pem"));
		List<X509CertificateHolder> certchain = new ArrayList<X509CertificateHolder>();
		try
		{
			int i = 0;
			object = r.readPemObject();
			while (object != null)
			{
				X509CertificateHolder crtholder2 = new X509CertificateHolder(((PemObject) object).getContent());
				System.out.println(i++ + ": "+crtholder2.getSubject().toString());
				certchain.add(crtholder2);
				object = r.readPemObject();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
//		for (int i = 0; i < certchain.size() - 1; ++i)
//		{
//			X509CertificateHolder signedcert = certchain.get(i);
//			X509CertificateHolder signercert = certchain.get(i + 1);
//			
//			JcaContentVerifierProviderBuilder jcvpb = new JcaContentVerifierProviderBuilder();
////			ContentVerifierProvider cvp = jcvpb.build(signercert);
////			ContentVerifier cv = cvp.get(signercert.getSignatureAlgorithm());
//			
//			System.out.println("VerifyCert: " + signedcert.isSignatureValid(jcvpb.build(signercert)));
//		}
		
//		r = new PEMParser(new FileReader("/home/jander/test.pem"));
//		object = r.readPemObject();
//		X509CertificateHolder crtholder = new X509CertificateHolder(((PemObject) object).getContent());
//		System.out.println(crtholder.getIssuer());
//		System.out.println(crtholder.getSignatureAlgorithm());
//		System.out.println(crtholder.getSubject());
//		System.out.println(crtholder.getSubjectPublicKeyInfo().parsePublicKey());
//		System.out.println(crtholder.isValidOn(new Date()));
		
//		String testmsg = "testmsg";
		
//		DefaultAlgorithmNameFinder danf = new DefaultAlgorithmNameFinder();
//		JcaPEMKeyConverter jpkc = new JcaPEMKeyConverter();
//		PrivateKey pk = jpkc.getPrivateKey(pki);
//		JcaContentSignerBuilder sb = new JcaContentSignerBuilder(danf.getAlgorithmName(crtholder.getSignatureAlgorithm()));
//		sb.setSecureRandom(SSecurity.getSecureRandom());
//		ContentSigner cs = sb.build(pk);
//		System.out.println("ABC "+cs);
//		cs.getOutputStream().write(testmsg.getBytes(SUtil.UTF8));
//		cs.getOutputStream().close();
//		byte[] sig = cs.getSignature();
		
//		JcaContentVerifierProviderBuilder jcvpb = new JcaContentVerifierProviderBuilder();
//		ContentVerifierProvider cvp = jcvpb.build(crtholder);
//		ContentVerifier cv = cvp.get(crtholder.getSignatureAlgorithm());
//		cv.getOutputStream().write(testmsg.getBytes(SUtil.UTF8));
//		cv.getOutputStream().close();
//		System.out.println("Verify: " + cv.verify(sig));
		
		
		Blake2bX509AuthenticationSuite auth = new Blake2bX509AuthenticationSuite();
		byte[] token = auth.createAuthenticationToken("Test".getBytes(SUtil.UTF8), new PasswordSecret(SUtil.createNetworkPassword("sooperdoopersecruit")));
		System.out.println("toklen: " + token.length);
		System.out.println(auth.verifyAuthenticationToken("Test".getBytes(SUtil.UTF8), new PasswordSecret(SUtil.createNetworkPassword("sooperdoopersecruit")), token));
		System.out.println(auth.verifyAuthenticationToken("Test".getBytes(SUtil.UTF8), new PasswordSecret(SUtil.createNetworkPassword("superdupersecret")), token));
	}
}
