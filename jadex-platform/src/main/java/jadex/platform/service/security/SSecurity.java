package jadex.platform.service.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.util.Date;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.x509.X509V1CertificateGenerator;


/**
 * 
 */
public class SSecurity
{
	static
	{
		BouncyCastleProvider bc = new BouncyCastleProvider();
		java.security.Security.addProvider(bc);
	}
	
	/**
	 *  Get keystore from a given file.
	 */
	public static KeyStore getKeystore(String storepath, String storepass, String keypass, String alias)
	{
		try
		{
			KeyStore ks = KeyStore.getInstance("JKS");
			FileInputStream fis = null;
			boolean loaded = false;
			try
			{
				File f = new File(storepath);
				if(f.exists())
				{
					fis = new FileInputStream(storepath);
					ks.load(fis, storepass.toCharArray());
					loaded = true;
				}
			}
			catch(Exception e)
			{
			}
			finally
			{
				if(fis!=null)
					fis.close();
				if(!loaded)
					initKeystore(ks, storepath, storepass, keypass, alias);
			}
			return ks;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	/**
	 *  Init keystore with a self-signed certificate.
	 */
	public static void initKeystore(KeyStore ks, String storepath, String storepass, String keypass, String alias)
	{
		try
    	{
	    	ks.load(null, null); // Must be called. 
	    	
//	    	RSAKeyPairGenerator r = new RSAKeyPairGenerator();
//	    	r.init(new KeyGenerationParameters(new SecureRandom(), 1024));
//	    	AsymmetricCipherKeyPair keys = r.generateKeyPair();
	    	
	    	KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");  
	 	    gen.initialize(1024);  
	 	    
//	 	    System.out.println("Generating key pair, this may take a short while.");
	 	    KeyPair keys = gen.generateKeyPair();
//	 	    System.out.println("Key generation finished.");
	 	    
		    Certificate c = generateCertificate("CN=CKS Self Signed Cert", keys, 1000, "MD5WithRSA");
		    
		    ks.setKeyEntry(alias, keys.getPrivate(), keypass.toCharArray(),  
		    	new java.security.cert.Certificate[]{c});  
		    
		    FileOutputStream output = null;
		    try
		    {
		    	output = new FileOutputStream(storepath);
		    	ks.store(output, storepass.toCharArray());
		    }
		    finally
		    {
		    	if(output!=null)
		    		output.close();
		    }
    	}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
//	/**
//	 *  Generate a certificate.
//	 *  @param dn the X.509 Distinguished Name, eg "CN=Test, L=London, C=GB"
//	 *  @param pair
//	 *  @param days
//	 *  @param algorithm
//	 *  @throws GeneralSecurityException
//	 *  @throws IOException
//	 */
//	public static Certificate generateCertificate(String dn, KeyPair pair, int days, String algorithm) throws GeneralSecurityException, IOException
//	{
//		PrivateKey privkey = pair.getPrivate();
//		X509CertInfo info = new X509CertInfo();
//		Date from = new Date();
//		Date to = new Date(from.getTime() + days * 86400000l);
//		CertificateValidity interval = new CertificateValidity(from, to);
//		BigInteger sn = new BigInteger(64, new SecureRandom());
//		X500Name owner = new X500Name(dn);
// 
//		info.set(X509CertInfo.VALIDITY, interval);
//		info.set(X509CertInfo.SERIAL_NUMBER, new CertificateSerialNumber(sn));
//		info.set(X509CertInfo.SUBJECT, new CertificateSubjectName(owner));
//		info.set(X509CertInfo.ISSUER, new CertificateIssuerName(owner));
//		info.set(X509CertInfo.KEY, new CertificateX509Key(pair.getPublic()));
//		info.set(X509CertInfo.VERSION, new CertificateVersion(CertificateVersion.V3));
//		AlgorithmId algo = new AlgorithmId(AlgorithmId.md5WithRSAEncryption_oid);
//		info.set(X509CertInfo.ALGORITHM_ID, new CertificateAlgorithmId(algo));
// 
//		// Sign the cert to identify the algorithm that's used.
//		X509CertImpl cert = new X509CertImpl(info);
//		cert.sign(privkey, algorithm);
// 
//		// Update the algorith, and resign.
//		algo = (AlgorithmId)cert.get(X509CertImpl.SIG_ALG);
//		info.set(CertificateAlgorithmId.NAME + "." + CertificateAlgorithmId.ALGORITHM, algo);
//		cert = new X509CertImpl(info);
//		cert.sign(privkey, algorithm);
//		return cert;
//	}   
	
	
	/** 
	 * Create a self-signed X.509 Certificate
	 * @param dn the X.509 Distinguished Name, eg "CN=Test, L=London, C=GB"
	 * @param pair the KeyPair
	 * @param days how many days from now the Certificate is valid for
	 * @param algorithm the signing algorithm, eg "SHA1withRSA"
	 */ 
	public static Certificate generateCertificate(String dn, KeyPair pair, int days, String algorithm) throws GeneralSecurityException, IOException
	{
		X509V1CertificateGenerator gen = new X509V1CertificateGenerator();
		X500Principal dnn = new X500Principal(dn); //"CN=Test CA Certificate"

		Date from = new Date();
		Date to = new Date(from.getTime() + days * 86400000l);
		BigInteger sn = new BigInteger(64, new SecureRandom());
		
		gen.setSerialNumber(sn);
		gen.setIssuerDN(dnn);
		gen.setNotBefore(from);
		gen.setNotAfter(to);
		gen.setSubjectDN(dnn);                       // note: same as issuer
		gen.setPublicKey(pair.getPublic());
		gen.setSignatureAlgorithm(algorithm);

		Certificate cert = gen.generate(pair.getPrivate());
		
		return cert;
	}   
	
//	/**
//	 * 
//	 */
//	public static void generateKeyPair(String name, KeyStore ks)
//	{
//		try
//		{
//			java.security.Security.addProvider(new BouncyCastleProvider());
//		    KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "BC");
//		    generator.initialize(1024, new FixedRand());
//	 
//		    KeyPair pair = generator.generateKeyPair();
//		    Key pubKey = pair.getPublic();
//		    Key privKey = pair.getPrivate();
//		    
//		    if(ks!=null)
//		    {
//		    	ks.setEntry(name, new KeyStore.PrivateKeyEntry(pair.getPrivate(), chain),
//		    			new KeyStore.PasswordProtection(privateKeyEntryPassword.toCharArray())
//		    			);
//
//		    }
//		}
//		catch(Exception e)
//		{
//			throw new RuntimeException(e);
//		}
//	}
	
//	/**
//	 * 
//	 */
//	private static class FixedRand extends SecureRandom 
//	{
//        MessageDigest sha;
//        byte[] state;
// 
//        FixedRand() 
//        {
//            try
//            {
//                this.sha = MessageDigest.getInstance("SHA-1");
//                this.state = sha.digest();
//            }
//            catch(NoSuchAlgorithmException e)
//            {
//                throw new RuntimeException("can't find SHA-1!");
//            }
//        }
// 
//        public void nextBytes(byte[] bytes)
//        {
//            int    off = 0;
//            sha.update(state);
// 
//            while (off < bytes.length)
//            {               
//                state = sha.digest();
//                if (bytes.length - off > state.length)
//                {
//                    System.arraycopy(state, 0, bytes, off, state.length);
//                }
//                else
//                {
//                    System.arraycopy(state, 0, bytes, off, bytes.length - off);
//                }
// 
//                off += state.length;
//                sha.update(state);
//            }
//        }
//    }

	/**
     * 
     */
    public static byte[] signContent(PrivateKey key, Signature engine, byte[] content) 
    	throws InvalidKeyException, SignatureException 
    {
    	engine.initSign(key);
    	engine.update(content); // clone ?
    	byte[] sig = engine.sign();// clone() ?
    	return sig;
    }
	
	/**
	 *  
     */
    public static boolean verifyContent(PublicKey key, Signature engine, byte[] content, byte[] sig) 
    	throws InvalidKeyException, SignatureException 
    {
    	engine.initVerify(key);
    	engine.update(content); // clone() ?
    	return engine.verify(sig); // clone() ?
    }

	
	/**
	 *  Main for testing.
	 */
	public static void main(String[] args)
	{
		KeyStore ks = getKeystore("c:\\temp\\keystore", "hans", "hans", "alias");
		System.out.println("ks: "+ks);
	}
}
