package jadex.commons.security;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.sec.ECPrivateKey;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.ECNamedCurveTable;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509ExtensionUtils;
import org.bouncycastle.cert.bc.BcX509v3CertificateBuilder;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.generators.DSAKeyPairGenerator;
import org.bouncycastle.crypto.generators.DSAParametersGenerator;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.DSAKeyGenerationParameters;
import org.bouncycastle.crypto.params.DSAParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECNamedDomainParameters;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import org.bouncycastle.crypto.prng.EntropySource;
import org.bouncycastle.crypto.prng.EntropySourceProvider;
import org.bouncycastle.crypto.prng.SP800SecureRandomBuilder;
import org.bouncycastle.crypto.util.PrivateKeyInfoFactory;
import org.bouncycastle.crypto.util.SubjectPublicKeyInfoFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.operator.DefaultAlgorithmNameFinder;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.bc.BcDigestCalculatorProvider;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder;
import org.bouncycastle.pqc.crypto.sphincs.SPHINCS256KeyGenerationParameters;
import org.bouncycastle.pqc.crypto.sphincs.SPHINCS256KeyPairGenerator;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.security.random.SecureThreadedRandom;


/**
 *  Class with static helper methods for security functions.
 */
public class SSecurity
{
	/**
	 *  Flag if the paranoid/hedged-mode PRNG should be used (much slower, but guarded against single-point failures).
	 */
	public static boolean PARANOID_PRNG = false;
	
	/** Common secure random number source. */
	protected static volatile SecureRandom SECURE_RANDOM;
	
	/** Random number source for seeding CSPRNGS. */
	protected static volatile SecureRandom SECURE_SEED_RANDOM;
	
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
					if (PARANOID_PRNG)
						SECURE_RANDOM = generateParanoidSecureRandom();
					else
						SECURE_RANDOM = generateSecureRandom();
				}
			}
		}
		return SECURE_RANDOM;
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
					final SecureRandom basicseed = new SecureRandom()
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
					
					if (PARANOID_PRNG)
					{
						SECURE_SEED_RANDOM = new SecureRandom()
						{
							public synchronized void nextBytes(byte[] bytes)
							{
								byte[] addent = SecureRandom.getSeed(bytes.length);
								basicseed.nextBytes(bytes);
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
					else
					{
						SECURE_SEED_RANDOM = basicseed;
					}
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
			X509CertificateHolder cert = readCertificateFromPEM(new String(certdata, SUtil.UTF8));
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			GZIPOutputStream gos = new GZIPOutputStream(baos);
			gos.write(certdata);
			gos.close();
			certdata = baos.toByteArray();
			
			String pemkeystr = new String(SUtil.readStream(pemkey), SUtil.UTF8);
			pemkey.close();
			JcaPEMKeyConverter jpkc = new JcaPEMKeyConverter();
			PrivateKey privatekey = jpkc.getPrivateKey(readPrivateKeyFromPEM(pemkeystr));
			
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
			X509CertificateHolder trustedcrtholder = readCertificateFromPEM(new String(SUtil.readStream(trustedpemcert), SUtil.UTF8));
			trustedpemcert.close();
			
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
			
			PEMParser pemparser = new PEMParser(new InputStreamReader(new ByteArrayInputStream(certdata), SUtil.UTF8));
			List<X509CertificateHolder> certchain = new ArrayList<X509CertificateHolder>();
			Object object = pemparser.readPemObject();
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
	
	/**
	 *  Generates a self-signed certificate that allows signing / authentication.
	 *  
	 *  @param subjectdn The CA subject identifier.
	 *  @param scheme Signature scheme to use, e.g. RSA, DSA, ECDSA.
	 *  @param hashalg Hash algorithm to use.
	 *  @param strength Strength of the key.
	 *  @param daysvalid Number of days valid.
	 *  @return The certificate.
	 */
	public static final Tuple2<String, String> createSelfSignedCertificate(String subjectdn, String scheme, String hashalg, int strength, int daysvalid)
	{
		X500Name subject = new X500Name(subjectdn);
		
		Extension bcext = new Extension(Extension.basicConstraints, true, asn1ToBytes(new BasicConstraints(false)));
		Extension kuext = new Extension(Extension.keyUsage, true, asn1ToBytes(new KeyUsage(KeyUsage.digitalSignature)));
		
		return createCertificateBySpecification(null, null, subject, scheme, hashalg, strength, daysvalid, bcext, kuext);
	}
	
	/**
	 *  Generates a certificate that allows signing / authentication.
	 * 
	 *  @param issuercert Certificate of the parent CA.
	 *  @param issuerkey Key of the parent CA.
	 *  @param subjectdn The CA subject identifier.
	 *  @param scheme Signature scheme to use, e.g. RSA, DSA, ECDSA.
	 *  @param hashalg Hash algorithm to use.
	 *  @param strength Strength of the key.
	 *  @param daysvalid Number of days valid.
	 *  @return The certificate.
	 */
	public static final Tuple2<String, String> createCertificate(String issuercert, String issuerkey, String subjectdn, String scheme, String hashalg, int strength, int daysvalid)
	{
		X500Name subject = new X500Name(subjectdn);
		
		Extension bcext = new Extension(Extension.basicConstraints, true, asn1ToBytes(new BasicConstraints(false)));
		Extension kuext = new Extension(Extension.keyUsage, true, asn1ToBytes(new KeyUsage(KeyUsage.digitalSignature)));
		
		return createCertificateBySpecification(issuercert, issuerkey, subject, scheme, hashalg, strength, daysvalid, bcext, kuext);
	}
	
	/**
	 *  Generates a certificate for an intermediate CA.
	 *  
	 *  @param issuercert Certificate of the parent CA.
	 *  @param issuerkey Key of the parent CA.
	 *  @param subjectdn The CA subject identifier.
	 *  @param pathlen Allowed path length for the intermediate CA (0 = no intermediate CA certificate children).
	 *  @param scheme Signature scheme to use, e.g. RSA, DSA, ECDSA.
	 *  @param hashalg Hash algorithm to use.
	 *  @param strength Strength of the key.
	 *  @param daysvalid Number of days valid.
	 *  @return The certificate.
	 */
	public static final Tuple2<String, String> createIntermediateCaCertificate(String issuercert, String issuerkey, String subjectdn, int pathlen, String scheme, String hashalg, int strength, int daysvalid)
	{
		X500Name subject = new X500Name(subjectdn);
		
		Extension bcext = new Extension(Extension.basicConstraints, true, asn1ToBytes(new BasicConstraints(pathlen)));
		Extension kuext = new Extension(Extension.keyUsage, true, asn1ToBytes(new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyCertSign)));
		
		return createCertificateBySpecification(issuercert, issuerkey, subject, scheme, hashalg, strength, daysvalid, bcext, kuext);
	}
	
	/**
	 *  Generates a certificate for a root CA.
	 *  
	 *  @param subjectdn The CA subject identifier.
	 *  @param scheme Signature scheme to use, e.g. RSA, DSA, ECDSA.
	 *  @param hashalg Hash algorithm to use.
	 *  @param strength Strength of the key.
	 *  @param daysvalid Number of days valid.
	 *  @return The certificate.
	 */
	public static final Tuple2<String, String> createRootCaCertificate(String subjectdn, String scheme, String hashalg, int strength, int daysvalid)
	{
		X500Name subject = new X500Name(subjectdn);
		
		Extension bcext = new Extension(Extension.basicConstraints, true, asn1ToBytes(new BasicConstraints(true)));
		Extension kuext = new Extension(Extension.keyUsage, true, asn1ToBytes(new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyCertSign)));
		
		return createCertificateBySpecification(null, null, subject, scheme, hashalg, strength, daysvalid, bcext, kuext);
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
	protected static final SecureRandom generateParanoidSecureRandom()
	{
		SecureRandom ret = null;
		
		EntropySourceProvider esp = new EntropySourceProvider()
		{
			public EntropySource get(int bitsRequired)
			{
				// Convert to bytes.
				int numbytes = (int) Math.ceil(bitsRequired / 8.0);
				final byte[] fseed = new byte[numbytes];
				getSeedRandom().nextBytes(fseed);;
				
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
	 *  Creates a certificate using the given specification.
	 *  
	 *  @param issuercert Certificate of the issuer (CA).
	 *  @param issuerkey Key of the issuer (CA).
	 *  @param subject Subject of the certificate.
	 *  @param scheme Signature scheme to use, e.g. RSA, DSA, ECDSA.
	 *  @param hashalg Hash algorithm to use.
	 *  @param strength Strength of the key.
	 *  @param daysvalid Number of days valid.
	 *  @param extensions Certificate extensions.
	 *  @return Generated Certificate and private key as PEM-encoded strings.
	 */
	protected static final Tuple2<String, String> createCertificateBySpecification(String issuercert, String issuerkey, X500Name subject, String scheme, String hashalg, int strength, int daysvalid, Extension... extensions)
	{
		try
		{
			X500Name issuer = null;
			X509CertificateHolder loadedissuercert = null;
			
			if (issuercert == null)
			{
				issuer = subject;
			}
			else
			{
				loadedissuercert = SSecurity.readCertificateFromPEM(issuercert);
				issuer = loadedissuercert.getSubject();
			}
			
			byte[] serialbytes = new byte[20];
			SSecurity.getSecureRandom().nextBytes(serialbytes);
			BigInteger serial = new BigInteger(1, serialbytes);
			
			AsymmetricCipherKeyPair pair = createKeyPair(scheme, strength);
			
			String algospec = hashalg + "WITH" + scheme;
			
			long notafterts = System.currentTimeMillis() + daysvalid*24L*3600L*1000L;
			Date notafter = new Date(notafterts);
			
			BcX509v3CertificateBuilder builder = null;
			PrivateKeyInfo pki = null;
			ContentSigner signer = null;
		
			SubjectPublicKeyInfo spki = SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(pair.getPublic());
			DefaultDigestAlgorithmIdentifierFinder algfinder = new DefaultDigestAlgorithmIdentifierFinder();
			BcDigestCalculatorProvider dcp = new BcDigestCalculatorProvider();
			X509ExtensionUtils utils = new X509ExtensionUtils(dcp.get(algfinder.find(hashalg)));
			SubjectKeyIdentifier ski = utils.createSubjectKeyIdentifier(spki);
			
			AuthorityKeyIdentifier aki = null;
			if (loadedissuercert != null)
			{
				aki = utils.createAuthorityKeyIdentifier(loadedissuercert.getSubjectPublicKeyInfo());
			}
			else
			{
				aki = utils.createAuthorityKeyIdentifier(spki);
			}
			
			pki = PrivateKeyInfoFactory.createPrivateKeyInfo(pair.getPrivate());
			
			PrivateKey parentpk = null;
			if (issuerkey != null)
			{
				PrivateKeyInfo parentpki = SSecurity.readPrivateKeyFromPEM(issuerkey);
				parentpk = (new JcaPEMKeyConverter()).getPrivateKey(parentpki);
			}
			else
			{
				parentpk = (new JcaPEMKeyConverter()).getPrivateKey(pki);
			}
			
			JcaContentSignerBuilder signerbuilder = new JcaContentSignerBuilder(algospec);
			signer = signerbuilder.build(parentpk);
			
			builder = new BcX509v3CertificateBuilder(issuer, serial, new Date(), notafter, subject, pair.getPublic());
			
			if (extensions != null)
			{
				for (Extension ext : extensions)
				{
					builder.addExtension(ext);
				}
			}
			builder.addExtension(Extension.subjectKeyIdentifier, false, ski);
			builder.addExtension(Extension.authorityKeyIdentifier, false, aki);
			
			X509CertificateHolder cert = builder.build(signer);
			
			ByteArrayOutputStream boscert = new ByteArrayOutputStream();
			ByteArrayOutputStream boskey = new ByteArrayOutputStream();
			
			JcaPEMWriter pemwriter = new JcaPEMWriter(new OutputStreamWriter(boscert));
			pemwriter.writeObject(cert);
			pemwriter.flush();
			if (issuercert != null)
				boscert.write(issuercert.getBytes(SUtil.UTF8));
			pemwriter.close();
			
			pemwriter = new JcaPEMWriter(new OutputStreamWriter(boskey));
			pemwriter.writeObject(pki);
			pemwriter.close();
			
			return new Tuple2<String, String>(new String(boscert.toByteArray(), SUtil.UTF8),
											  new String(boskey.toByteArray(), SUtil.UTF8));
		}
		catch (Exception e)
		{
			throw SUtil.throwUnchecked(e);
		}
	}
	
	/**
	 *  Generate a key pair.
	 * 
	 *  @param alg Algorithm to use, e.g. RSA, DSA, ECDSA.
	 *  @param strength Strength of the key pair.
	 *  @return The generated key pair.
	 */
	protected static final AsymmetricCipherKeyPair createKeyPair(String alg, int strength)
	{
		AsymmetricCipherKeyPair pair = null;
		
		if ("RSA".equals(alg) || "RSAANDMGF1".equals(alg))
		{
			RSAKeyGenerationParameters kgparams = new RSAKeyGenerationParameters(new BigInteger("65537"), SSecurity.getSecureRandom(), 4096, 100);
			RSAKeyPairGenerator kpg = new RSAKeyPairGenerator();
			kpg.init(kgparams);
			pair = kpg.generateKeyPair();
		}
		else if ("DSA".equals(alg))
		{
			DSAParametersGenerator pgen = new DSAParametersGenerator();
			pgen.init(strength, 20, SSecurity.getSecureRandom());
			DSAParameters dsaparams = pgen.generateParameters();
			DSAKeyGenerationParameters kgparams = new DSAKeyGenerationParameters(SSecurity.getSecureRandom(), dsaparams);
			DSAKeyPairGenerator kpg = new DSAKeyPairGenerator();
			kpg.init(kgparams);
			pair = kpg.generateKeyPair();
		}
		else if ("ECDSA".equals(alg))
		{
			String curvname = null;
			if (strength > 384)
				curvname = "secp521r1";
			else if (strength > 256)
				curvname = "secp384r1";
			else
				curvname = "secp256r1";
			
			X9ECParameters x9 = CustomNamedCurves.getByName(curvname);
			ASN1ObjectIdentifier oid = ECNamedCurveTable.getOID(curvname);
			ECNamedDomainParameters dparams = new ECNamedDomainParameters(oid, x9.getCurve(), x9.getG(), x9.getN(), x9.getH(), x9.getSeed());
			ECKeyGenerationParameters kgparams = new ECKeyGenerationParameters(dparams, SSecurity.getSecureRandom());
			ECKeyPairGenerator kpg = new ECKeyPairGenerator();
			kpg.init(kgparams);
			pair = kpg.generateKeyPair();
		}
		else if ("SPHINCS256".equals(alg))
		{
			SPHINCS256KeyGenerationParameters kgparams = new SPHINCS256KeyGenerationParameters(SSecurity.getSecureRandom(), new SHA256Digest());
			SPHINCS256KeyPairGenerator kpg = new SPHINCS256KeyPairGenerator();
			kpg.init(kgparams);
			pair = kpg.generateKeyPair();
		}
		
		if (pair == null)
			throw new IllegalArgumentException("Could not generate key pair: Signature scheme " + alg + " not found.");
		
		return pair;
	}
	
	/**
	 *  Read a certificate from a PEM-encoded string.
	 *  
	 *  @param pem The PEM-encoded string.
	 *  @return The certificate.
	 */
	protected static final X509CertificateHolder readCertificateFromPEM(String pem)
	{
		try
		{
			PEMParser pemparser = new PEMParser(new StringReader(pem));
			PemObject pemcertobj = pemparser.readPemObject();
			pemparser.close();
			return new X509CertificateHolder(pemcertobj.getContent());
		}
		catch (Exception e)
		{
			throw SUtil.throwUnchecked(e);
		}
	}
	
	/**
	 *  Reads a private key from a PEM string.
	 *  
	 *  @param pem The PEM-encoded string.
	 *  @return The private key.
	 */
	protected static final PrivateKeyInfo readPrivateKeyFromPEM(String pem)
	{
		PrivateKeyInfo ret = null;
		
		// Two-prong approach due to bug with ECDSA keys.
		PemReader pemreader = new PemReader(new StringReader(pem));
		Object pemobject = null;
		do
		{
			try
			{
				pemobject = pemreader.readPemObject();
				ECPrivateKey ecpk = ECPrivateKey.getInstance(((PemObject) pemobject).getContent());
				AlgorithmIdentifier algid = new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, ecpk.getParameters());
                ret = new PrivateKeyInfo(algid, ecpk);
                pemreader.close();
                pemreader = null;
			}
			catch (Exception e)
			{
			}
		}
		while (ret == null && pemobject != null);
		
		if (ret != null)
		{
			PEMParser pemparser = new PEMParser(new StringReader(pem));
			
			do
			{
				try
				{
					pemobject = pemparser.readObject();
				}
				catch (Exception e)
				{
				}
			}
			while (!(pemobject instanceof PEMKeyPair) && pemobject != null);
			
			try
			{
				pemparser.close();
			}
			catch (Exception e)
			{
			}
			
			if (pemobject instanceof PEMKeyPair)
				ret = ((PEMKeyPair) pemobject).getPrivateKeyInfo();
		}
		
		if (ret == null)
			throw new RuntimeException("Could not read private key: " + pem);
		
		return ret;
	}
	
	/**
	 *  Shorthand for converting ANS1Objects to bytes.
	 *  
	 *  @param obj The object.
	 *  @return Encoded bytes.
	 */
	protected static final byte[] asn1ToBytes(ASN1Object obj)
	{
		try
		{
			return obj.toASN1Primitive().getEncoded(ASN1Encoding.DER);
		}
		catch (IOException e)
		{
			throw SUtil.throwUnchecked(e);
		}
	}
	
	/**
	 *  Main for testing.
	 */
	public static void main(String[] args)
	{
	}
}
