package jadex.commons.security;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.DSAKey;
import java.security.interfaces.RSAKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.crypto.interfaces.DHKey;
import javax.security.auth.x500.X500Principal;

import org.spongycastle.crypto.Mac;
import org.spongycastle.crypto.digests.SHA512Digest;
import org.spongycastle.crypto.engines.AESFastEngine;
import org.spongycastle.crypto.macs.HMac;
import org.spongycastle.crypto.prng.EntropySource;
import org.spongycastle.crypto.prng.EntropySourceProvider;
import org.spongycastle.crypto.prng.SP800SecureRandomBuilder;
import org.spongycastle.jce.provider.BouncyCastleProvider;
import org.spongycastle.x509.X509V1CertificateGenerator;

import jadex.commons.Base64;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.security.random.SecureThreadedRandom;


/**
 *  Class with static helper methods for security functions.
 */
public class SSecurity
{
	static
	{
		BouncyCastleProvider bc = new BouncyCastleProvider();
		if (!(SReflect.isAndroid() && SUtil.androidUtils().getAndroidVersion() > 19 && SUtil.androidUtils().getAndroidVersion() < 23)) {
			// see https://code.google.com/p/android/issues/detail?id=68562
			java.security.Security.addProvider(bc);
		}
	}
	
	/** Common secure random number source. */
	protected static volatile SecureRandom RANDOM;
	
	protected static volatile SecureRandom HIGHLY_SECURE_SEED_RANDOM;
	
	protected static volatile SecureRandom SEED_RANDOM;
	
	/**
	 *  Gets access to the common secure PRNG.
	 *  @return Common secure PRNG.
	 */
	public static final SecureRandom getSecureRandom()
	{
		if(RANDOM == null)
		{
			synchronized(SSecurity.class)
			{
				if(RANDOM == null)
				{
					RANDOM = new SecureThreadedRandom();
				}
			}
		}
		return RANDOM;
	}
	
	/**
	 *  Generates a fast secure PRNG. The setup attempts to prepare a PRNG that is fast and secure.
	 *  @return Secure PRNG.
	 */
	public static final SecureRandom generateSecureRandom()
	{
		return new SecureThreadedRandom();
	}
	
	/**
	 *  Generates a secure PRNG. The setup attempts to prepare a PRNG that avoids relying
	 *  on a single approach.
	 *  @return Secure PRNG.
	 */
	public static final SecureRandom generateHighlySecureRandom()
	{
		SecureRandom ret = null;
		
		EntropySourceProvider esp = new EntropySourceProvider()
		{
			public EntropySource get(int bitsRequired)
			{
				// Convert to bytes.
				int numbytes = (int) Math.ceil(bitsRequired / 8.0);
				final byte[] fseed = new byte[numbytes];
				getHighlySecureSeedRandom().nextBytes(fseed);;
				
				EntropySource ret = new EntropySource()
				{
					public boolean isPredictionResistant()
					{
						return true;
					}
					
					public byte[] getEntropy()
					{
						return fseed;
					}
					
					public int entropySize()
					{
						return fseed.length * 8;
					}
				};
				return ret;
			}
		};
		
		// Combine PRNGs / paranoid
		List<SecureRandom> prngs = new ArrayList<SecureRandom>();
		
		SP800SecureRandomBuilder builder = new SP800SecureRandomBuilder(esp);
		AESFastEngine eng = new AESFastEngine();
		prngs.add(builder.buildCTR(eng, 256, esp.get(128).getEntropy(), false));
		System.out.println(prngs.get(prngs.size() - 1));
		
		Mac m = new HMac(new SHA512Digest());
		prngs.add(builder.buildHMAC(m, esp.get(512).getEntropy(), false));
		System.out.println(prngs.get(prngs.size() - 1));
		
		prngs.add(generateSecureRandom());
		System.out.println(prngs.get(prngs.size() - 1));
		
		prngs.add(new SecureRandom());
		System.out.println(prngs.get(prngs.size() - 1));
		
		final SecureRandom[] randsources = prngs.toArray(new SecureRandom[prngs.size()]);
		ret = new SecureRandom()
		{
			/** ID */
			private static final long serialVersionUID = -3198322750446562871L;
			
			public synchronized void nextBytes(byte[] bytes)
			{
				randsources[0].nextBytes(bytes);
				if (randsources.length > 1)
				{
					byte[] addbytes = new byte[bytes.length];
					for (int i = 1; i < randsources.length; ++i)
					{
						randsources[i].nextBytes(addbytes);
						xor(bytes, addbytes);
					}
				}
			}
		};
		return ret;
	}
	
	/**
	 *  XORs two byte arrays.
	 *  
	 *  @param op1result First array and output array.
	 *  @param op2 Second array.
	 *  @return Modified first array.
	 */
	public static final byte[] xor(byte[] op1result, byte[] op2)
	{
		int max = Math.max(op1result.length, op2.length);
		for (int i = 0; i < max; ++i)
		{
			op1result[i] = (byte) (op1result[i] ^ op2[i]);
		}
		return op1result;
	}
	
	/**
	 *  Gets a secure random seed value that received additional strengthening.
	 *  
	 *  @param numbytes number of seed bytes needed.
	 *  @return Secure seed.
	 */
	public static SecureRandom getHighlySecureSeedRandom()
	{
		if (HIGHLY_SECURE_SEED_RANDOM == null)
		{
			synchronized (SSecurity.class)
			{
				if (HIGHLY_SECURE_SEED_RANDOM == null)
				{
					HIGHLY_SECURE_SEED_RANDOM = new SecureRandom()
					{
						public synchronized void nextBytes(byte[] bytes)
						{
							
							byte[] addent = SecureRandom.getSeed(bytes.length);
							getSeedRandom().nextBytes(bytes);;
							
							xor(bytes, addent);
						}
						
						public byte[] generateSeed(int numbytes)
						{
							byte[] ret = new byte[numbytes];
							nextBytes(ret);
							return ret;
						}
					};
				}
			}
		}
		return HIGHLY_SECURE_SEED_RANDOM;
	}
	
	/**
	 *  Gets a secure random seed value from OS or other sources.
	 *  
	 *  @param numbytes number of seed bytes needed.
	 *  @return Secure seed.
	 */
	public static SecureRandom getSeedRandom()
	{
		if (SEED_RANDOM == null)
		{
			synchronized (SSecurity.class)
			{
				if (SEED_RANDOM == null)
				{
					SEED_RANDOM = new SecureRandom()
					{
						private static final long serialVersionUID = -8238246099124227737L;
						
						protected long bytecounter = 0;

						public synchronized void nextBytes(byte[] ret)
						{
							boolean noseed = true;
							File urandom = new File("/dev/urandom");
							InputStream urandomin = null;
							try
							{
								urandomin = new FileInputStream(urandom);
								if (urandom.exists())
								{
									SUtil.readStream(ret, urandomin);
									noseed = false;
									SUtil.close(urandomin);
								}
							}
							catch (Exception e)
							{
								SUtil.close(urandomin);
							}
							
							if (noseed)
							{
								// For Windows, use Windows API to gather seed data
								String osname = System.getProperty("os.name");
								String osversion = System.getProperty("os.version");
								int minmajwinversion = 6;
								if (osname != null &&
									osname.startsWith("Windows") &&
									osversion != null &&
									osversion.contains(".") &&
									Integer.parseInt(osversion.substring(0, osversion.indexOf('.'))) >= minmajwinversion)
								{
									try
									{
										ret = WinCrypt.getRandomFromWindows(ret.length);
										noseed = false;
									}
									catch(Throwable e)
									{
									}
								}
							}
							
							if (noseed)
							{
								ret = SecureRandom.getSeed(ret.length);
							}
							
							bytecounter += ret.length;
							System.out.println("Seed consumption: " + bytecounter);
						}
						
						public byte[] generateSeed(int numbytes)
						{
							byte[] ret = new byte[numbytes];
							nextBytes(ret);
							return ret;
						}
					};
				}
			}
		}
		
		
		return SEED_RANDOM;
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
				if(!loaded || (alias!=null && !ks.containsAlias(alias)))
					initKeystore(ks, storepath, storepass, keypass, alias);
				
				addStartSSLCertificate(ks, storepath, storepass);
			}
	
			return ks;
		}
		catch(Exception e)
		{
//			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	/**
	 *  Get keystore from a given file.
	 */
	public static void saveKeystore(KeyStore keystore, String storepath, String storepass)
	{
		FileOutputStream fos = null;
		try
		{
			fos = new FileOutputStream(storepath);
			keystore.store(fos, storepass.toCharArray());
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
		finally
		{
			if(fos!=null)
			{
				try
				{
					fos.close();
				}
				catch(Exception e)
				{
				}
			}
		}
	}
	
	/**
	 *  Add the start ssl cert to the Java trust store.
	 */
	public static void addStartSSLToTrustStore(String storepass)
	{
		String storepath = System.getProperty("java.home") + "/lib/security/cacerts";
		KeyStore ks = getKeystore(storepath, storepass, null, null);
		addStartSSLCertificate(ks, storepath, storepass);
	}
	
	/**
	 *  Add the startssl.com root certificate to the used store.
	 */
	public static void addStartSSLCertificate(KeyStore ks, String storepath, String storepass)
	{
		try
		{
//			Enumeration<String> aliases = ks.aliases();
//			for(; aliases.hasMoreElements(); ) 
//			{
//				String alias = (String)aliases.nextElement();
//				System.out.println("alias: "+alias);
//			}
			
			if(!ks.containsAlias("startcom.ca")) 
			{
				CertificateFactory cf = CertificateFactory.getInstance("X.509");
				InputStream is = null;
				
				try
				{
					is = SUtil.getResource("jadex/platform/service/security/ca.crt", null);
					Certificate cert = cf.generateCertificate(is);
					ks.setCertificateEntry("startcom.ca", cert);
				}
				catch(Exception e)
				{
				}
				finally 
				{
					if(is!=null)
						is.close();
				}
				
				try
				{
					is = SUtil.getResource("jadex/platform/service/security/sub.class1.server.ca.crt", null);
					Certificate cert = cf.generateCertificate(is);
					ks.setCertificateEntry("startcom.ca.sub", cert);
				}
				catch(Exception e)
				{
				}
				finally 
				{
					if(is!=null)
						is.close();
				}
				
//				try
//				{
					saveKeystore(ks, storepath, storepass);
//				}
//				catch(Exception e)
//				{
//					// trust store holds certificates for validation (key store mainly holds (private) keys)
//					System.out.println("Could not save trust store: "+e.getMessage());
//				}
			}
		}
		catch(Exception e)
		{
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
	    	
	    	KeyPair keys = generateKeyPair("RSA", 1024);
		    Certificate c = generateCertificate("CN=CKS Self Signed Cert", keys, 1000, "SHA256WithRSA");
		    
		    // Creates key entry (i.e. keypair with certificate)
		    ks.setKeyEntry(alias, keys.getPrivate(), keypass.toCharArray(),  
		    	new java.security.cert.Certificate[]{c});  
		    
		    saveKeystore(ks, storepath, storepass);
    	}
		catch(RuntimeException e)
		{
			throw e;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
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
	public static Certificate generateCertificate(KeyPair pair, int days, String algorithm) 
		throws GeneralSecurityException, IOException
	{
		return generateCertificate(null, pair, days, algorithm);
	}
	
	/** 
	 * Create a self-signed X.509 Certificate
	 * @param dn the X.509 Distinguished Name, eg "CN=Test, L=London, C=GB"
	 * @param pair the KeyPair
	 * @param days how many days from now the Certificate is valid for
	 * @param algorithm the signing algorithm, eg "SHA1withRSA"
	 */ 
	public static Certificate generateCertificate(String dn, KeyPair pair, int days, String algorithm) 
		throws GeneralSecurityException, IOException
	{
		if(dn==null)
			dn = "CN=CKS Self Signed Cert";
		if(days<=0)
			days = 365;
		
		X509V1CertificateGenerator gen = new X509V1CertificateGenerator();
		X500Principal dnn = new X500Principal(dn); //"CN=Test CA Certificate"

		Date from = new Date();
		Date to = new Date(from.getTime() + days * 86400000l);
		BigInteger sn = new BigInteger(64, getSecureRandom());
		
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
	
	/** 
	 * Create a self-signed X.509 Certificate
	 * @param dn the X.509 Distinguished Name, eg "CN=Test, L=London, C=GB"
	 * @param pair the KeyPair
	 * @param days how many days from now the Certificate is valid for
	 * @param algorithm the signing algorithm, eg "SHA1withRSA"
	 */ 
	public static KeyPair generateKeyPair(String algorithm, int keysize) 
		throws GeneralSecurityException, IOException
	{
		KeyPairGenerator gen = KeyPairGenerator.getInstance(algorithm);  
 	    gen.initialize(keysize);  
 	    KeyPair keys = gen.generateKeyPair();
		return keys;
	}
	
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
	 * Get the digest of a message as a formatted String.
	 */
	public static String getHexMessageDigest(byte[] data, String type)
	{
		try
		{
			MessageDigest mdig = MessageDigest.getInstance(type);
			byte[] fp = mdig.digest(data);
			return SUtil.hex(fp, ":", 1);
		}
		catch(NoSuchAlgorithmException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	/**
	 *  Get the key length.
	 */
	public static int getKeyLength(Key key)
	{
		int ret = -1;
		
		if(key instanceof RSAKey)
		{
			ret = ((RSAKey)key).getModulus().bitLength();
		}
		else if(key instanceof DSAKey)
		{
			ret = ((DSAKey)key).getParams().getP().bitLength();
		}
		else if(key instanceof DHKey)
		{
			ret = ((DHKey)key).getParams().getP().bitLength();
		}
//		else if(key instanceof ECKey)
//		{
//		}

		return ret;
	}
	
	/**
	 *  Get the textual representation of a certificate.
	 */
	public static String getCertificateText(Certificate cert)
	{
		String ret = null;
		
		try
		{
			StringBuffer buf =  new StringBuffer("-----BEGIN CERTIFICATE-----").append(SUtil.LF);
			buf.append(new String(Base64.toCharArray(cert.getEncoded(), 64)));
			buf.append(SUtil.LF).append("-----END CERTIFICATE-----");
			ret = buf.toString();
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
		
		return ret;
	}
	
	/**
	 *  Get the textual representation of a certificate.
	 */
	public static Certificate createCertificate(InputStream in)
	{
		Certificate ret = null;
		try 
		{
			CertificateFactory fac = CertificateFactory.getInstance("X.509");
			ret = fac.generateCertificate(in);
		}
		catch(Exception ex)
		{ 
		}
		finally
		{
			try
			{
				in.close();
			}
			catch(Exception exc)
			{
			}
		}
		return ret;
	}
	
	/**
	 *  Get the textual representation of a certificate.
	 */
	public static Certificate createCertificate(String text)
	{
		Certificate ret = null;
		
		try
		{
			CertificateFactory fac = CertificateFactory.getInstance("X.509");
			ByteArrayInputStream bas = new ByteArrayInputStream(text.getBytes());
			ret = fac.generateCertificate(bas);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return ret;
	}
	
	/**
	 *  Get the alogrithm name of a certificate.
	 */
	public static String getAlgorithm(Certificate cert)
	{
		String ret = "MD5WithRSA"; // todo: how to find out if not X509
		if(cert instanceof X509Certificate)
			ret = ((X509Certificate)cert).getSigAlgName();
		return ret;
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
