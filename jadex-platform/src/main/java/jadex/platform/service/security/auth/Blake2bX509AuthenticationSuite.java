package jadex.platform.service.security.auth;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.crypto.agreement.DHStandardGroups;
import org.bouncycastle.crypto.digests.Blake2bDigest;
import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.util.Pack;
import org.bouncycastle.util.io.pem.PemObject;

import jadex.commons.SUtil;
import jadex.commons.security.SSecurity;

/**
 *  Symmetric authentication based on Blake2b MACs.
 */
public class Blake2bX509AuthenticationSuite implements IAuthenticationSuite
{
	/** Authentication Suite ID. */
	protected static final int AUTH_SUITE_ID = 93482103;
	
	/** Size of the MAC. */
	protected static final int MAC_SIZE = 64;
	
	/** Size of the derived key. */
	protected static final int DERIVED_KEY_SIZE = 64;
	
	/** Size of the salt. */
	protected static final int SALT_SIZE = 32;
	
	/** Schnorr group used for zero-knowledge password proof. */
	protected static final DHParameters SCHNORR_GROUP = DHStandardGroups.rfc3526_4096;
	
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
			
			byte[] dk = ssecret.deriveKey(DERIVED_KEY_SIZE, salt);
			
			if (ssecret instanceof PasswordSecret)
			{
				// Using Schnorr signature
				
				byte[] randsecbytes = new byte[64];
				SSecurity.getSecureRandom().nextBytes(randsecbytes);
				BigInteger randsec = new BigInteger(1, randsecbytes);
				BigInteger randpub = SCHNORR_GROUP.getG().modPow(randsec, SCHNORR_GROUP.getP());
				
				byte[] challengebytes = new byte[64];
				Blake2bDigest dig = new Blake2bDigest(512);
				byte[] tmp = randpub.toByteArray();
				dig.update(tmp, 0, tmp.length);
				dig.update(msghash, 0, msghash.length);
				dig.doFinal(challengebytes, 0);
				BigInteger challenge = new BigInteger(1, challengebytes);
				
				BigInteger dkbi = new BigInteger(1, dk);
				
				BigInteger s = dkbi.multiply(challenge).add(randsec).mod(SCHNORR_GROUP.getP());
				
				byte[] kdfparams = ((PasswordSecret) ssecret).getKdfParams();
				
				tmp = SUtil.mergeData(s.toByteArray(), randpub.toByteArray(), salt, kdfparams);
				
				ret = new byte[tmp.length + 4];
				Pack.intToLittleEndian(AUTH_SUITE_ID, ret, 0);
				System.arraycopy(tmp, 0, ret, 4, tmp.length);
			}
			else
			{
				// Generate MAC used for authentication.
				Blake2bDigest blake2b = new Blake2bDigest(dk);
				ret = new byte[SALT_SIZE + MAC_SIZE + 4];
				Pack.intToLittleEndian(AUTH_SUITE_ID, ret, 0);
				System.arraycopy(salt, 0, ret, 4, salt.length);
				blake2b.update(msghash, 0, msghash.length);
				blake2b.doFinal(ret, salt.length + 4);
			}
		}
		else if (secret instanceof AbstractX509PemSecret)
		{
			AbstractX509PemSecret aps = (AbstractX509PemSecret) secret;
			if (!aps.canSign())
				throw new IllegalArgumentException("Secret cannot be used to sign: " + aps);
			
			byte[] sig = SSecurity.signWithPEM(msghash, aps.openCertificate(), aps.openPrivateKey());
			ret = new byte[sig.length + SALT_SIZE + 4];
			Pack.intToLittleEndian(AUTH_SUITE_ID, ret, 0);
			System.arraycopy(salt, 0, ret, 4, salt.length);
			System.arraycopy(sig, 0, ret, 4 + salt.length, sig.length);
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
			
			if (Pack.littleEndianToInt(authtoken, 0) != AUTH_SUITE_ID)
				return false;
			
			if (secret instanceof SharedSecret)
			{
				if (secret instanceof PasswordSecret)
				{
					List<byte[]> authlist = SUtil.splitData(authtoken, 4, -1);
					if (authlist.size() != 4)
						return false;
					
					BigInteger s = new BigInteger(authlist.get(0));
					BigInteger randpub = new BigInteger(authlist.get(1));
					byte[] salt = authlist.get(2);
					byte[] kdfparams = authlist.get(3);
					
					byte[] dk = ((PasswordSecret) ssecret).deriveKey(DERIVED_KEY_SIZE, salt, kdfparams);
					
					BigInteger sec = new BigInteger(1, dk);
					BigInteger pub = SCHNORR_GROUP.getG().modPow(sec, SCHNORR_GROUP.getP());
					
					byte[] msghash = getMessageHash(msg, salt);
					
					byte[] challengebytes = new byte[64];
					Blake2bDigest dig = new Blake2bDigest(512);
					byte[] tmp = randpub.toByteArray();
					dig.update(tmp, 0, tmp.length);
					dig.update(msghash, 0, msghash.length);
					dig.doFinal(challengebytes, 0);
					BigInteger challenge = new BigInteger(1, challengebytes);
					
					BigInteger res0 = SCHNORR_GROUP.getG().modPow(s, SCHNORR_GROUP.getP());
					BigInteger res1 = pub.modPow(challenge, SCHNORR_GROUP.getP()).multiply(randpub).mod(SCHNORR_GROUP.getP());
					
//					System.out.println("Schnorr res0:" + res0);
//					System.out.println("Schnorr res1:" + res1);
					
					ret = res0.equals(res1);
				}
				else
				{
					if (authtoken.length != SALT_SIZE + MAC_SIZE + 4)
						return false;
					
					// Decode token.
					byte[] salt = new byte[SALT_SIZE];
					System.arraycopy(authtoken, 4, salt, 0, salt.length);
					
					byte[] msghash = getMessageHash(msg, salt);
					
					byte[] mac = new byte[MAC_SIZE];
					System.arraycopy(authtoken, SALT_SIZE + 4, mac, 0, mac.length);
					
					// Decrypt the random key.
					byte[] dk = ssecret.deriveKey(DERIVED_KEY_SIZE, salt);
					
					// Generate MAC
					Blake2bDigest blake2b = new Blake2bDigest(dk);
					byte[] gmac = new byte[MAC_SIZE];
					blake2b.update(msghash, 0, msghash.length);
					blake2b.doFinal(gmac, 0);
					ret = Arrays.equals(gmac, mac);
				}
			}
			else if (secret instanceof AbstractX509PemSecret)
			{
				// Decode token.
				byte[] salt = new byte[SALT_SIZE];
				System.arraycopy(authtoken, 4, salt, 0, salt.length);
				
				byte[] msghash = getMessageHash(msg, salt);
				
				AbstractX509PemSecret aps = (AbstractX509PemSecret) secret;
				byte[] sig = new byte[authtoken.length - 4 - salt.length];
				System.arraycopy(authtoken, 4 + salt.length, sig, 0, sig.length);
				
				SSecurity.verifyWithPEM(msghash, sig, aps.openTrustAnchorCert());
			}
			else
			{
				Logger.getLogger("authentication").warning("Unknown secret type: " + secret);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
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
		byte[] m = "Message".getBytes(SUtil.UTF8);
		
		byte[] secbytes = new byte[32];
		SSecurity.getSecureRandom().nextBytes(secbytes);
		BigInteger sec = new BigInteger(secbytes);
		BigInteger pub = SCHNORR_GROUP.getG().modPow(sec, SCHNORR_GROUP.getP());
		
		byte[] randsecbytes = new byte[32];
		SSecurity.getSecureRandom().nextBytes(randsecbytes);
		BigInteger randsec = new BigInteger(randsecbytes);
		BigInteger randpub = SCHNORR_GROUP.getG().modPow(randsec, SCHNORR_GROUP.getP());
		
		byte[] challengebytes = new byte[64];
		Blake2bDigest dig = new Blake2bDigest(512);
		byte[] tmp = randpub.toByteArray();
		dig.update(tmp, 0, tmp.length);
		dig.update(m, 0, m.length);
		dig.doFinal(challengebytes, 0);
		BigInteger challenge = new BigInteger(challengebytes);
		
		BigInteger s = sec.multiply(challenge).add(randsec).mod(SCHNORR_GROUP.getP());
		
		// s,randpub
		
		
		BigInteger res1 = SCHNORR_GROUP.getG().modPow(s, SCHNORR_GROUP.getP());
		BigInteger res2 = pub.modPow(challenge, SCHNORR_GROUP.getP()).multiply(randpub).mod(SCHNORR_GROUP.getP());
		
		System.out.println(res1);
		System.out.println(res2);
		
	}
	
	public static void mainx(String[] args) throws Exception
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
