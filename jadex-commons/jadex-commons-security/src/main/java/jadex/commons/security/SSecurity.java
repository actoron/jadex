package jadex.commons.security;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.crypto.interfaces.DHKey;
import javax.security.auth.x500.X500Principal;

import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.prng.EntropySource;
import org.bouncycastle.crypto.prng.EntropySourceProvider;
import org.bouncycastle.crypto.prng.SP800SecureRandomBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.operator.DefaultAlgorithmNameFinder;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.x509.X509V1CertificateGenerator;

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
	protected static volatile SecureRandom SECURE_RANDOM;
	
	/** Slow but conservative secure random number source for critical tasks, e.g. key generation. */
	protected static volatile SecureRandom HIGHLY_SECURE_RANDOM;
	
	/** Random number source for seeding CSPRNGS. */
	protected static volatile SecureRandom SECURE_SEED_RANDOM;
	
	/** Conservative random number source for seeding CSPRNGS, use sparingly. */
	protected static volatile SecureRandom HIGHLY_SECURE_SEED_RANDOM;
	
	/**
	 *  Gets access to the common secure PRNG.
	 *  @return Common secure PRNG.
	 */
	public static final SecureRandom getSecureRandom()
	{
		if(SECURE_RANDOM == null)
		{
			synchronized(SSecurity.class)
			{
				if(SECURE_RANDOM == null)
				{
					SECURE_RANDOM = new SecureThreadedRandom();
				}
			}
		}
		return SECURE_RANDOM;
	}
	
	/**
	 *  Gets access to the common highly secure PRNG used for key generation etc.
	 *  @return Common secure PRNG.
	 */
	public static final SecureRandom getHighlySecureRandom()
	{
		if(HIGHLY_SECURE_RANDOM == null)
		{
			synchronized(SSecurity.class)
			{
				if(HIGHLY_SECURE_RANDOM == null)
				{
					HIGHLY_SECURE_RANDOM = generateHighlySecureRandom();
				}
			}
		}
		return HIGHLY_SECURE_RANDOM;
	}
	
	/**
	 *  Generates a fast secure PRNG. The setup attempts to prepare a PRNG that is fast and secure.
	 *  @return Secure PRNG.
	 */
	protected static final SecureRandom generateSecureRandom()
	{
		return new SecureThreadedRandom();
	}
	
	/**
	 *  Generates a secure PRNG. The setup attempts to prepare a PRNG that avoids relying
	 *  on a single approach.
	 *  @return Secure PRNG.
	 */
	protected static final SecureRandom generateHighlySecureRandom()
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
		
		EntropySourceProvider nonceprovider = new EntropySourceProvider()
		{
			public EntropySource get(int bitsRequired)
			{
				// Convert to bytes.
				int numbytes = (int) Math.ceil(bitsRequired / 8.0);
				final byte[] fseed = new byte[numbytes];
				getSecureRandom().nextBytes(fseed);;
				
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
		AESEngine eng = new AESEngine();
		prngs.add(builder.buildCTR(eng, 256, nonceprovider.get(256).getEntropy(), false));
//		System.out.println(prngs.get(prngs.size() - 1));
		
		Mac m = new HMac(new SHA512Digest());
		prngs.add(builder.buildHMAC(m, nonceprovider.get(512).getEntropy(), false));
//		System.out.println(prngs.get(prngs.size() - 1));
		
		prngs.add(generateSecureRandom());
//		System.out.println(prngs.get(prngs.size() - 1));
		
		prngs.add(new SecureRandom());
//		System.out.println(prngs.get(prngs.size() - 1));
		
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
							getSeedRandom().nextBytes(bytes);
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
		if (SECURE_SEED_RANDOM == null)
		{
			synchronized (SSecurity.class)
			{
				if (SECURE_SEED_RANDOM == null)
				{
					SECURE_SEED_RANDOM = new SecureRandom()
					{
						private static final long serialVersionUID = -8238246099124227737L;
						
//						protected long bytecounter = 0;

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
							
//							bytecounter += ret.length;
//							System.out.println("Seed consumption: " + bytecounter);
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
		
		
		return SECURE_SEED_RANDOM;
	}
	
	/**
	 *  Sign using a PEM-encoded X.509 certificate/key.
	 * 
	 *  @param msghash The message hash.
	 *  @param pemcert The PEM certificate.
	 *  @param pemkey The PEM key.
	 *  @return Signature.
	 */
	public static final byte[] signWithPEM(byte[] msghash, InputStream pemcert, InputStream pemkey)
	{
		byte[] ret = null;
		try
		{
			byte[] certdata = SUtil.readStream(pemcert);
			pemcert.close();
			PEMParser pemparser = new PEMParser(new InputStreamReader(new ByteArrayInputStream(certdata), SUtil.UTF8));
			X509CertificateHolder cert = new X509CertificateHolder(pemparser.readPemObject().getContent());
			pemparser.close();
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			GZIPOutputStream gos = new GZIPOutputStream(baos);
			gos.write(certdata);
			gos.close();
			certdata = baos.toByteArray();
			
			pemparser = new PEMParser(new InputStreamReader(pemkey, SUtil.UTF8));
			JcaPEMKeyConverter jpkc = new JcaPEMKeyConverter();
			Object o = null;
			do
			{
				o = pemparser.readObject();
			}
			while (!(o instanceof PEMKeyPair) && o != null);
			PrivateKey privatekey = jpkc.getPrivateKey(((PEMKeyPair) o).getPrivateKeyInfo());
			pemparser.close();
			
			DefaultAlgorithmNameFinder algfinder = new DefaultAlgorithmNameFinder();
			JcaContentSignerBuilder signerbuilder = new JcaContentSignerBuilder(algfinder.getAlgorithmName(cert.getSignatureAlgorithm()));
			ContentSigner signer = signerbuilder.build(privatekey);
			signer.getOutputStream().write(msghash);
			signer.getOutputStream().close();
			
			byte[] sig = signer.getSignature();
			
			ret = SUtil.mergeData(certdata, sig);
		}
		catch (Exception e)
		{
			Logger.getLogger("authentication").info("Signature creation failed: " + e.toString());
		}
		finally
		{
			try
			{
				pemcert.close();
			}
			catch (Exception e)
			{
			}
			
			try
			{
				pemkey.close();
			}
			catch (Exception e)
			{
			}
		}
		
		return ret;
	}
	
	/**
	 *  Verify using a PEM-encoded X.509 certificate/key.
	 * 
	 *  @param msghash The message hash.
	 *  @param token The authentication token.
	 *  @param trustedpemcert The PEM certificate trust anchor.
	 *  @return True, if the certificate chain and signature is valid.
	 */
	public static final boolean verifyWithPEM(byte[] msghash, byte[] token, InputStream trustedpemcert)
	{
		try
		{
			Date now = new Date();
			PEMParser pemparser = new PEMParser(new InputStreamReader(trustedpemcert, SUtil.UTF8));
			PemObject object = pemparser.readPemObject();
			X509CertificateHolder trustedcrtholder = new X509CertificateHolder(((PemObject) object).getContent());
			pemparser.close();
			if (!trustedcrtholder.isValidOn(now))
				return false;
			
			List<byte[]> splitdata = SUtil.splitData(token);
			if (splitdata.size() != 2)
				return false;
			
			byte[] certdata = splitdata.get(0);
			ByteArrayInputStream bais = new ByteArrayInputStream(certdata);
			GZIPInputStream gis = new GZIPInputStream(bais);
			certdata = SUtil.readStream(gis);
			byte[] sig = splitdata.get(1);
			
			pemparser = new PEMParser(new InputStreamReader(new ByteArrayInputStream(certdata), SUtil.UTF8));
			List<X509CertificateHolder> certchain = new ArrayList<X509CertificateHolder>();
			object = pemparser.readPemObject();
			while (object != null)
			{
				X509CertificateHolder crtholder = new X509CertificateHolder(((PemObject) object).getContent());
				certchain.add(crtholder);
				object = pemparser.readPemObject();
			}
			pemparser.close();
			pemparser = null;
			
			// Verify certificate chain
			JcaContentVerifierProviderBuilder jcvpb = new JcaContentVerifierProviderBuilder();
			for (int i = 0; i < certchain.size() - 1; ++i)
			{
				X509CertificateHolder signedcert = certchain.get(i);
				X509CertificateHolder signercert = certchain.get(i + 1);
				if (!signedcert.isValidOn(now) || !signercert.isValidOn(now))
					return false;
				
				BasicConstraints bc = BasicConstraints.fromExtensions(signercert.getExtensions());
				if (bc == null || !bc.isCA() || (bc.getPathLenConstraint() != null && bc.getPathLenConstraint().longValue() < i))
					return false;
				
				if (!signedcert.isSignatureValid(jcvpb.build(signercert)))
					return false;
			}
			
			// Verify the last chain link is signed by trust anchor.
			if (!certchain.get(certchain.size() - 1).isSignatureValid(jcvpb.build(trustedcrtholder)))
				return false;
			BasicConstraints bc = BasicConstraints.fromExtensions(trustedcrtholder.getExtensions());
			if (bc == null || !bc.isCA() || (bc.getPathLenConstraint() != null && bc.getPathLenConstraint().longValue() < certchain.size() - 1))
				return false;
			
			// Verify signature
			ContentVerifier cv = jcvpb.build(certchain.get(0)).get(certchain.get(0).getSignatureAlgorithm());
			cv.getOutputStream().write(msghash);
			cv.getOutputStream().close();
			return cv.verify(sig);
		}
		catch (Exception e)
		{
			Logger.getLogger("authentication").info("Verification failed: " + e.toString());
		}
		finally
		{
			try
			{
				trustedpemcert.close();
			}
			catch (Exception e)
			{
			}
		}
		return false;
	}
	
//	/**
//	 *  Get keystore from a given file.
//	 */
//	public static KeyStore getKeystore(String storepath, String storepass, String keypass, String alias)
//	{
//		try
//		{
//			KeyStore ks = KeyStore.getInstance("JKS");
//			FileInputStream fis = null;
//			boolean loaded = false;
//			try
//			{
//				File f = new File(storepath);
//				if(f.exists())
//				{
//					fis = new FileInputStream(storepath);
//					ks.load(fis, storepass.toCharArray());
//					loaded = true;
//				}
//			}
//			catch(Exception e)
//			{
//			}
//			finally
//			{
//				if(fis!=null)
//					fis.close();
//				if(!loaded || (alias!=null && !ks.containsAlias(alias)))
//					initKeystore(ks, storepath, storepass, keypass, alias);
//				
//				addStartSSLCertificate(ks, storepath, storepass);
//			}
//	
//			return ks;
//		}
//		catch(Exception e)
//		{
////			e.printStackTrace();
//			throw new RuntimeException(e);
//		}
//	}
//	
//	/**
//	 *  Get keystore from a given file.
//	 */
//	public static void saveKeystore(KeyStore keystore, String storepath, String storepass)
//	{
//		FileOutputStream fos = null;
//		try
//		{
//			fos = new FileOutputStream(storepath);
//			keystore.store(fos, storepass.toCharArray());
//		}
//		catch(Exception e)
//		{
//			throw new RuntimeException(e);
//		}
//		finally
//		{
//			if(fos!=null)
//			{
//				try
//				{
//					fos.close();
//				}
//				catch(Exception e)
//				{
//				}
//			}
//		}
//	}
//	
//	/**
//	 *  Add the start ssl cert to the Java trust store.
//	 */
//	public static void addStartSSLToTrustStore(String storepass)
//	{
//		String storepath = System.getProperty("java.home") + "/lib/security/cacerts";
//		KeyStore ks = getKeystore(storepath, storepass, null, null);
//		addStartSSLCertificate(ks, storepath, storepass);
//	}
//	
//	/**
//	 *  Add the startssl.com root certificate to the used store.
//	 */
//	public static void addStartSSLCertificate(KeyStore ks, String storepath, String storepass)
//	{
//		try
//		{
////			Enumeration<String> aliases = ks.aliases();
////			for(; aliases.hasMoreElements(); ) 
////			{
////				String alias = (String)aliases.nextElement();
////				System.out.println("alias: "+alias);
////			}
//			
//			if(!ks.containsAlias("startcom.ca")) 
//			{
//				CertificateFactory cf = CertificateFactory.getInstance("X.509");
//				InputStream is = null;
//				
//				try
//				{
//					is = SUtil.getResource("jadex/platform/service/security/ca.crt", null);
//					Certificate cert = cf.generateCertificate(is);
//					ks.setCertificateEntry("startcom.ca", cert);
//				}
//				catch(Exception e)
//				{
//				}
//				finally 
//				{
//					if(is!=null)
//						is.close();
//				}
//				
//				try
//				{
//					is = SUtil.getResource("jadex/platform/service/security/sub.class1.server.ca.crt", null);
//					Certificate cert = cf.generateCertificate(is);
//					ks.setCertificateEntry("startcom.ca.sub", cert);
//				}
//				catch(Exception e)
//				{
//				}
//				finally 
//				{
//					if(is!=null)
//						is.close();
//				}
//				
////				try
////				{
//					saveKeystore(ks, storepath, storepass);
////				}
////				catch(Exception e)
////				{
////					// trust store holds certificates for validation (key store mainly holds (private) keys)
////					System.out.println("Could not save trust store: "+e.getMessage());
////				}
//			}
//		}
//		catch(Exception e)
//		{
//			throw new RuntimeException(e);
//		}
//	}
//	
//	/**
//	 *  Init keystore with a self-signed certificate.
//	 */
//	public static void initKeystore(KeyStore ks, String storepath, String storepass, String keypass, String alias)
//	{
//		try
//    	{
//	    	ks.load(null, null); // Must be called. 
//	    	
////	    	RSAKeyPairGenerator r = new RSAKeyPairGenerator();
////	    	r.init(new KeyGenerationParameters(new SecureRandom(), 1024));
////	    	AsymmetricCipherKeyPair keys = r.generateKeyPair();
//	    	
//	    	KeyPair keys = generateKeyPair("RSA", 1024);
//		    Certificate c = generateCertificate("CN=CKS Self Signed Cert", keys, 1000, "SHA256WithRSA");
//		    
//		    // Creates key entry (i.e. keypair with certificate)
//		    ks.setKeyEntry(alias, keys.getPrivate(), keypass.toCharArray(),  
//		    	new java.security.cert.Certificate[]{c});  
//		    
//		    saveKeystore(ks, storepath, storepass);
//    	}
//		catch(RuntimeException e)
//		{
//			throw e;
//		}
//		catch(Exception e)
//		{
//			throw new RuntimeException(e);
//		}
//	}
//	
////	/**
////	 *  Generate a certificate.
////	 *  @param dn the X.509 Distinguished Name, eg "CN=Test, L=London, C=GB"
////	 *  @param pair
////	 *  @param days
////	 *  @param algorithm
////	 *  @throws GeneralSecurityException
////	 *  @throws IOException
////	 */
////	public static Certificate generateCertificate(String dn, KeyPair pair, int days, String algorithm) throws GeneralSecurityException, IOException
////	{
////		PrivateKey privkey = pair.getPrivate();
////		X509CertInfo info = new X509CertInfo();
////		Date from = new Date();
////		Date to = new Date(from.getTime() + days * 86400000l);
////		CertificateValidity interval = new CertificateValidity(from, to);
////		BigInteger sn = new BigInteger(64, new SecureRandom());
////		X500Name owner = new X500Name(dn);
//// 
////		info.set(X509CertInfo.VALIDITY, interval);
////		info.set(X509CertInfo.SERIAL_NUMBER, new CertificateSerialNumber(sn));
////		info.set(X509CertInfo.SUBJECT, new CertificateSubjectName(owner));
////		info.set(X509CertInfo.ISSUER, new CertificateIssuerName(owner));
////		info.set(X509CertInfo.KEY, new CertificateX509Key(pair.getPublic()));
////		info.set(X509CertInfo.VERSION, new CertificateVersion(CertificateVersion.V3));
////		AlgorithmId algo = new AlgorithmId(AlgorithmId.md5WithRSAEncryption_oid);
////		info.set(X509CertInfo.ALGORITHM_ID, new CertificateAlgorithmId(algo));
//// 
////		// Sign the cert to identify the algorithm that's used.
////		X509CertImpl cert = new X509CertImpl(info);
////		cert.sign(privkey, algorithm);
//// 
////		// Update the algorith, and resign.
////		algo = (AlgorithmId)cert.get(X509CertImpl.SIG_ALG);
////		info.set(CertificateAlgorithmId.NAME + "." + CertificateAlgorithmId.ALGORITHM, algo);
////		cert = new X509CertImpl(info);
////		cert.sign(privkey, algorithm);
////		return cert;
////	}   
//	
//	/** 
//	 * Create a self-signed X.509 Certificate
//	 * @param dn the X.509 Distinguished Name, eg "CN=Test, L=London, C=GB"
//	 * @param pair the KeyPair
//	 * @param days how many days from now the Certificate is valid for
//	 * @param algorithm the signing algorithm, eg "SHA1withRSA"
//	 */ 
//	public static Certificate generateCertificate(KeyPair pair, int days, String algorithm) 
//		throws GeneralSecurityException, IOException
//	{
//		return generateCertificate(null, pair, days, algorithm);
//	}
//	
//	/** 
//	 * Create a self-signed X.509 Certificate
//	 * @param dn the X.509 Distinguished Name, eg "CN=Test, L=London, C=GB"
//	 * @param pair the KeyPair
//	 * @param days how many days from now the Certificate is valid for
//	 * @param algorithm the signing algorithm, eg "SHA1withRSA"
//	 */ 
//	public static Certificate generateCertificate(String dn, KeyPair pair, int days, String algorithm) 
//		throws GeneralSecurityException, IOException
//	{
//		if(dn==null)
//			dn = "CN=CKS Self Signed Cert";
//		if(days<=0)
//			days = 365;
//		
//		X509V1CertificateGenerator gen = new X509V1CertificateGenerator();
//		X500Principal dnn = new X500Principal(dn); //"CN=Test CA Certificate"
//
//		Date from = new Date();
//		Date to = new Date(from.getTime() + days * 86400000l);
//		BigInteger sn = new BigInteger(64, getSecureRandom());
//		
//		gen.setSerialNumber(sn);
//		gen.setIssuerDN(dnn);
//		gen.setNotBefore(from);
//		gen.setNotAfter(to);
//		gen.setSubjectDN(dnn);                       // note: same as issuer
//		gen.setPublicKey(pair.getPublic());
//		gen.setSignatureAlgorithm(algorithm);
//
//		Certificate cert = gen.generate(pair.getPrivate());
//		
//		return cert;
//	}  
//	
//	/** 
//	 * Create a self-signed X.509 Certificate
//	 * @param dn the X.509 Distinguished Name, eg "CN=Test, L=London, C=GB"
//	 * @param pair the KeyPair
//	 * @param days how many days from now the Certificate is valid for
//	 * @param algorithm the signing algorithm, eg "SHA1withRSA"
//	 */ 
//	public static KeyPair generateKeyPair(String algorithm, int keysize) 
//		throws GeneralSecurityException, IOException
//	{
//		KeyPairGenerator gen = KeyPairGenerator.getInstance(algorithm);  
// 	    gen.initialize(keysize);  
// 	    KeyPair keys = gen.generateKeyPair();
//		return keys;
//	}
//	
//	/**
//     * 
//     */
//    public static byte[] signContent(PrivateKey key, Signature engine, byte[] content) 
//    	throws InvalidKeyException, SignatureException 
//    {
//    	engine.initSign(key);
//    	engine.update(content); // clone ?
//    	byte[] sig = engine.sign();// clone() ?
//    	return sig;
//    }
//	
//	/**
//	 *  
//     */
//    public static boolean verifyContent(PublicKey key, Signature engine, byte[] content, byte[] sig) 
//    	throws InvalidKeyException, SignatureException 
//    {
//    	engine.initVerify(key);
//    	engine.update(content); // clone() ?
//    	return engine.verify(sig); // clone() ?
//    }
//    
//    /**
//	 * Get the digest of a message as a formatted String.
//	 */
//	public static String getHexMessageDigest(byte[] data, String type)
//	{
//		try
//		{
//			MessageDigest mdig = MessageDigest.getInstance(type);
//			byte[] fp = mdig.digest(data);
//			return SUtil.hex(fp, ":", 1);
//		}
//		catch(NoSuchAlgorithmException e)
//		{
//			throw new RuntimeException(e);
//		}
//	}
//	
//	/**
//	 *  Get the key length.
//	 */
//	public static int getKeyLength(Key key)
//	{
//		int ret = -1;
//		
//		if(key instanceof RSAKey)
//		{
//			ret = ((RSAKey)key).getModulus().bitLength();
//		}
//		else if(key instanceof DSAKey)
//		{
//			ret = ((DSAKey)key).getParams().getP().bitLength();
//		}
//		else if(key instanceof DHKey)
//		{
//			ret = ((DHKey)key).getParams().getP().bitLength();
//		}
////		else if(key instanceof ECKey)
////		{
////		}
//
//		return ret;
//	}
//	
//	/**
//	 *  Get the textual representation of a certificate.
//	 */
//	public static String getCertificateText(Certificate cert)
//	{
//		String ret = null;
//		
//		try
//		{
//			StringBuffer buf =  new StringBuffer("-----BEGIN CERTIFICATE-----").append(SUtil.LF);
//			buf.append(new String(Base64.toCharArray(cert.getEncoded(), 64)));
//			buf.append(SUtil.LF).append("-----END CERTIFICATE-----");
//			ret = buf.toString();
//		}
//		catch(Exception e)
//		{
//			throw new RuntimeException(e);
//		}
//		
//		return ret;
//	}
//	
//	/**
//	 *  Get the textual representation of a certificate.
//	 */
//	public static Certificate createCertificate(InputStream in)
//	{
//		Certificate ret = null;
//		try 
//		{
//			CertificateFactory fac = CertificateFactory.getInstance("X.509");
//			ret = fac.generateCertificate(in);
//		}
//		catch(Exception ex)
//		{ 
//		}
//		finally
//		{
//			try
//			{
//				in.close();
//			}
//			catch(Exception exc)
//			{
//			}
//		}
//		return ret;
//	}
//	
//	/**
//	 *  Get the textual representation of a certificate.
//	 */
//	public static Certificate createCertificate(String text)
//	{
//		Certificate ret = null;
//		
//		try
//		{
//			CertificateFactory fac = CertificateFactory.getInstance("X.509");
//			ByteArrayInputStream bas = new ByteArrayInputStream(text.getBytes());
//			ret = fac.generateCertificate(bas);
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//		}
//		
//		return ret;
//	}
//	
//	/**
//	 *  Get the alogrithm name of a certificate.
//	 */
//	public static String getAlgorithm(Certificate cert)
//	{
//		String ret = "MD5WithRSA"; // todo: how to find out if not X509
//		if(cert instanceof X509Certificate)
//			ret = ((X509Certificate)cert).getSigAlgName();
//		return ret;
//	}
	
	/**
	 *  Main for testing.
	 */
	public static void main(String[] args)
	{
//		KeyStore ks = getKeystore("c:\\temp\\keystore", "hans", "hans", "alias");
//		System.out.println("ks: "+ks);
	}
}
