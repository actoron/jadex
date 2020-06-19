package jadex.commons.security;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.logging.Logger;

import javax.net.ssl.TrustManagerFactory;

import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.sec.ECPrivateKey;
import org.bouncycastle.asn1.x500.AttributeTypeAndValue;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.ECNamedCurveTable;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509ContentVerifierProviderBuilder;
import org.bouncycastle.cert.X509ExtensionUtils;
import org.bouncycastle.cert.bc.BcX509v3CertificateBuilder;
import org.bouncycastle.cert.path.CertPath;
import org.bouncycastle.cert.path.CertPathValidation;
import org.bouncycastle.cert.path.CertPathValidationResult;
import org.bouncycastle.cert.path.validations.BasicConstraintsValidation;
import org.bouncycastle.cert.path.validations.KeyUsageValidation;
import org.bouncycastle.cert.path.validations.ParentCertIssuedValidation;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.generators.DSAKeyPairGenerator;
import org.bouncycastle.crypto.generators.DSAParametersGenerator;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.DSAKeyGenerationParameters;
import org.bouncycastle.crypto.params.DSAParameters;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECNamedDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import org.bouncycastle.crypto.prng.EntropySource;
import org.bouncycastle.crypto.prng.EntropySourceProvider;
import org.bouncycastle.crypto.prng.SP800SecureRandomBuilder;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.crypto.util.PrivateKeyInfoFactory;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.crypto.util.SubjectPublicKeyInfoFactory;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.bc.BcContentSignerBuilder;
import org.bouncycastle.operator.bc.BcContentVerifierProviderBuilder;
import org.bouncycastle.operator.bc.BcDSAContentSignerBuilder;
import org.bouncycastle.operator.bc.BcDSAContentVerifierProviderBuilder;
import org.bouncycastle.operator.bc.BcDigestCalculatorProvider;
import org.bouncycastle.operator.bc.BcECContentSignerBuilder;
import org.bouncycastle.operator.bc.BcECContentVerifierProviderBuilder;
import org.bouncycastle.operator.bc.BcRSAContentSignerBuilder;
import org.bouncycastle.operator.bc.BcRSAContentVerifierProviderBuilder;
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
	/** Default hash used for signatures. */
	protected static final String DEFAULT_SIGNATURE_HASH = "SHA512";
	
	/**
	 *  Flag if the paranoid/hedged-mode PRNG should be used (much slower, but guarded against single-point failures).
	 */
	public static boolean PARANOID_PRNG = false;
	
	/** Common secure random number source. */
	protected static volatile SecureRandom SECURE_RANDOM;
	
	/** Entropy source for seeding CSPRNGS. */
	protected static volatile IEntropySource ENTROPY_SOURCE;
	
	/** Flag if the fallback warning has been issued before. */
	protected static boolean ENTROPY_FALLBACK_WARNING_DONE = false;
	
	/** Enable this to test the seeding fallback, do not change, used by tests only. */
	protected static boolean TEST_ENTROPY_FALLBACK = false;
	
	static
	{
		SUtil.ensureNonblockingSecureRandom();
		getSecureRandom();
		
		if (SReflect.isAndroid())
		{
			// Probe for a weird bug caused by the interaction between
			// Java 9+, Android and Bouncycastle
			try
			{
	            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
	            tmf.init((KeyStore) null);
			}
			catch (Exception e)
			{
				// Bug appears to be there, attempt fix...
				String oldstoretype = System.getProperty("javax.net.ssl.trustStoreType");
				System.setProperty("javax.net.ssl.trustStoreType", "JKS");
				try
				{
		            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		            tmf.init((KeyStore) null);
				}
				catch (Exception e1)
				{
					// The fix did not work, restore initial state...
					System.setProperty("javax.net.ssl.trustStoreType", oldstoretype);
				}
			}
		}
	}
	
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
				
				// Attempt to overwrite UUID secure random.
//				try
//				{
//					Class<?>[] cls = UUID.class.getDeclaredClasses();
//					Class<?> holdercl = null;
//					if (cls != null)
//					{
//						for (Class<?> cl : cls)
//						{
//							if ("java.util.UUID.Holder".equals(cl.getCanonicalName()))
//							{
//								holdercl = cl;
//								break;
//							}
//						}
//					}
//					
//					if (holdercl != null)
//					{
//						Field prngfield = holdercl.getDeclaredField("numberGenerator");
//						VmHacks.get().setAccessible(prngfield, true);
//						Field modifiersfield = Field.class.getDeclaredField("modifiers");
//						VmHacks.get().setAccessible(modifiersfield, true);
//						modifiersfield.setInt(prngfield, prngfield.getModifiers() & ~Modifier.FINAL);
//						prngfield.set(null, SECURE_RANDOM);
//					}
//					
//				}
//				catch (Exception e)
//				{
//				}
			}
		}
		return SECURE_RANDOM;
	}
	
	/**
	 *  Gets a secure entropy source from OS or otherwise.
	 *  
	 *  @return Secure entropy source.
	 */
	public static IEntropySource getEntropySource()
	{
		if (ENTROPY_SOURCE == null)
		{
			synchronized (SSecurity.class)
			{
				if (ENTROPY_SOURCE == null)
				{
					final IEntropySource basicsource = new IEntropySource()
					{
						
						/**
						 *  Gets low-level entropy, preferably from OS.
						 */
						public synchronized void getEntropy(byte[] output)
						{
//							bcount += ret.length;
//							System.out.println("Entropy bytes: " + bcount);
//							boolean urandomworked = false;
							byte[] empty = new byte[output.length];
							byte[] ret = null;
							
							// For UNIX, use UNIX API to gather entropy data
							try
							{
								Class<?> unixentropyapi = Class.forName("jadex.commons.security.UnixEntropyApi");
								Method getEntropy = unixentropyapi.getMethod("getEntropy", int.class);
								ret = (byte[]) getEntropy.invoke(null, output.length);
							}
							catch(Throwable e)
							{
								ret = null;
							}
							
							// Try /dev/urandom
							if (ret == null || Arrays.equals(ret, empty))
								ret = getEntropyFromFile("/dev/urandom", output.length);
							
							// Try /dev/urandom
							if (ret == null || Arrays.equals(ret, empty))
								ret = getEntropyFromFile("/dev/random", output.length);
							
							if (ret == null || Arrays.equals(ret, empty))
							{
								// For Windows, use Windows API to gather entropy data
								try
								{
									Class<?> winentropyapi = Class.forName("jadex.commons.security.WindowsEntropyApi");
									Method getEntropy = winentropyapi.getMethod("getEntropy", int.class);
									ret = (byte[]) getEntropy.invoke(null, output.length);
								}
								catch(Throwable e)
								{
									ret = null;
								}
							}
							
							if (TEST_ENTROPY_FALLBACK)
								ret = null;
							
							// Fallback
							while (ret == null || Arrays.equals(ret, empty))
							{
								if (!ENTROPY_FALLBACK_WARNING_DONE)
								{
									Logger.getLogger("jadex").warning("Unable to find OS entropy source, using fallback...");
									ENTROPY_FALLBACK_WARNING_DONE = true;
								}
								ret = SecureRandom.getSeed(output.length);
							}
							
							System.arraycopy(ret, 0, output, 0, output.length);
							
							if (output == null || Arrays.equals(output, empty))
								throw new SecurityException("Entropy gathering failed.");
						}
						
						/**
						 *  Gets entropy from a file.
						 *  @param path Path of the entropy file.
						 *  @param numbytes Number of bytes to read.
						 *  @return Entropy data or null on failure.
						 */
						protected byte[] getEntropyFromFile(String path, int numbytes)
						{
							File input = new File(path);
							if (!input.exists())
								return null;
							
							InputStream is = null;
							try
							{
								byte[] ret = new byte[numbytes];
								is = new FileInputStream(input);
								int read = 0;
								int off = 0;
								while (read < ret.length)
								{
									read = is.read(ret, off, ret.length - read);
									off += read;
								}
								return ret;
								
							}
							catch (Exception e)
							{
							}
							finally
							{
								SUtil.close(is);
							}
							return null;
						}
					};
					
					ENTROPY_SOURCE = basicsource;
				}
			}
		}
		
		return ENTROPY_SOURCE;
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
			X509CertificateHolder cert = readCertificateFromPEM(new String(certdata, SUtil.UTF8));
			
			String pemkeystr = new String(SUtil.readStream(pemkey), SUtil.UTF8);
			pemkey.close();
			
			String sigalg = getCertSigAlg(cert);
			ContentSigner signer = getSigner(DEFAULT_SIGNATURE_HASH + "WITH" + sigalg, readPrivateKeyFromPEM(pemkeystr));
			signer.getOutputStream().write(msghash);
			signer.getOutputStream().close();
			
			byte[] sig = signer.getSignature();
			
			ret = sig;
		}
		catch (Exception e)
		{
			Logger.getLogger("authentication").info("Signature creation failed: " + e.toString());
		}
		finally
		{
			SUtil.close(pemcert);
		}
		
		return ret;
	}
	
	/**
	 *  Verify using a PEM-encoded X.509 certificate/key.
	 * 
	 *  @param msghash The message hash.
	 *  @param token The authentication token.
	 *  @param signingcert The signing certificate.
	 *  @param trustedpemcert The PEM certificate trust anchor.
	 *  @return True, if the certificate chain and signature is valid.
	 */
	public static final boolean verifyWithPEM(byte[] msghash, byte[] token, String signingcert, LinkedHashSet<X509CertificateHolder> trustchain)
	{
		try
		{
			Date now = new Date();
			
			List<X509CertificateHolder> certchain = readCertificateChainFromPEM(signingcert);
//			for (int i = 0; i < certchain.size() - 1; ++i)
//			{
//				X509CertificateHolder signedcert = certchain.get(i);
//				X509CertificateHolder signercert = certchain.get(i + 1);
//				if (!signedcert.isValidOn(now) || !signercert.isValidOn(now))
//					return false;
//				
//				BasicConstraints bc = BasicConstraints.fromExtensions(signercert.getExtensions());
//				if (bc == null || !bc.isCA() || (bc.getPathLenConstraint() != null && bc.getPathLenConstraint().longValue() < i))
//					return false;
//				
//				if (!signedcert.isSignatureValid(getVerifierProvider(signercert)))
//					return false;
//			}
//			
//			// Verify the last chain link is signed by trust anchor.
//			if (!certchain.get(certchain.size() - 1).isSignatureValid(getVerifierProvider(trustedcrtholder)))
//				return false;
//			BasicConstraints bc = BasicConstraints.fromExtensions(trustedcrtholder.getExtensions());
//			if (bc == null || !bc.isCA() || (bc.getPathLenConstraint() != null && bc.getPathLenConstraint().longValue() < certchain.size() - 1))
//				return false;
			
			// Verify provided certificate chain
			CertPath certpath = new CertPath(certchain.toArray(new X509CertificateHolder[certchain.size()]));
			CertPathValidationResult result = certpath.validate(getChainValidationRules());
			if (!result.isValid())
				return false;
			
			// Verify if at least one chain certificate is in trusted set.
			boolean chaintrust = false;
			for (X509CertificateHolder cert : certchain)
			{
				if (trustchain.contains(cert))
				{
					chaintrust = true;
					break;
				}
			}
			if (!chaintrust)
				return false;
			
			// Verify signature
			ContentVerifier cv = getDefaultVerifier(certchain.get(0));
			cv.getOutputStream().write(msghash);
			cv.getOutputStream().close();
			return cv.verify(token);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Logger.getLogger("authentication").info("Verification failed: " + e.toString());
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
	public static final Tuple2<String, String> createSelfSignedCertificate(String subjectdn, String scheme, String schemeconf, String hashalg, int strength, int daysvalid)
	{
		X500Name subject = new X500Name(subjectdn);
		
		Extension bcext = new Extension(Extension.basicConstraints, true, asn1ToBytes(new BasicConstraints(false)));
		Extension kuext = new Extension(Extension.keyUsage, true, asn1ToBytes(new KeyUsage(KeyUsage.digitalSignature)));
		
		return createCertificateBySpecification(null, null, subject, scheme, schemeconf, hashalg, strength, daysvalid, bcext, kuext);
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
	public static final Tuple2<String, String> createCertificate(String issuercert, String issuerkey, String subjectdn, String scheme, String schemeconf, String hashalg, int strength, int daysvalid)
	{
		X500Name subject = new X500Name(subjectdn);
		
		Extension bcext = new Extension(Extension.basicConstraints, true, asn1ToBytes(new BasicConstraints(false)));
		Extension kuext = new Extension(Extension.keyUsage, true, asn1ToBytes(new KeyUsage(KeyUsage.digitalSignature)));
		
		return createCertificateBySpecification(issuercert, issuerkey, subject, scheme, schemeconf, hashalg, strength, daysvalid, bcext, kuext);
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
	public static final Tuple2<String, String> createIntermediateCaCertificate(String issuercert, String issuerkey, String subjectdn, int pathlen, String scheme, String schemeconf, String hashalg, int strength, int daysvalid)
	{
		X500Name subject = new X500Name(subjectdn);
		
		Extension bcext = null;
		if (pathlen == -1)
			bcext = new Extension(Extension.basicConstraints, true, asn1ToBytes(new BasicConstraints(true)));
		else
			bcext = new Extension(Extension.basicConstraints, true, asn1ToBytes(new BasicConstraints(pathlen)));
		Extension kuext = new Extension(Extension.keyUsage, true, asn1ToBytes(new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyCertSign)));
		
		return createCertificateBySpecification(issuercert, issuerkey, subject, scheme, schemeconf, hashalg, strength, daysvalid, bcext, kuext);
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
	public static final Tuple2<String, String> createRootCaCertificate(String subjectdn, int pathlen, String scheme, String schemeconf, String hashalg, int strength, int daysvalid)
	{
		X500Name subject = new X500Name(subjectdn);
		
		Extension bcext = null;
		if (pathlen == -1)
			bcext = new Extension(Extension.basicConstraints, true, asn1ToBytes(new BasicConstraints(true)));
		else
			bcext = new Extension(Extension.basicConstraints, true, asn1ToBytes(new BasicConstraints(pathlen)));
		Extension kuext = new Extension(Extension.keyUsage, true, asn1ToBytes(new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyCertSign)));
		
		return createCertificateBySpecification(null, null, subject, scheme, schemeconf, hashalg, strength, daysvalid, bcext, kuext);
	}
	
	/** Creates a random CA certificate for testing. */
	public static final PemKeyPair createTestCACert()
	{
		Tuple2<String, String> cacert = createRootCaCertificate("CN=TESTCA", -1, "ECDSA", "NIST P", "SHA256", 256, 1);
		PemKeyPair ret = new PemKeyPair();
		ret.setCertificate(cacert.getFirstEntity());
		ret.setKey(cacert.getSecondEntity());
		return ret;
	}
	
	/** Creates a random certificate for testing. */
	public static final PemKeyPair createTestCert(PemKeyPair ca)
	{
		Tuple2<String, String> cert = createCertificate(ca.getCertificate(), ca.getKey(), "CN=TEST", "ECDSA", "NIST P", "SHA256", 256, 1);
		PemKeyPair ret = new PemKeyPair();
		ret.setCertificate(cert.getFirstEntity());
		ret.setKey(cert.getSecondEntity());
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
		int max = Math.min(op1result.length, op2.length);
		for (int i = 0; i < max; ++i)
		{
			op1result[i] = (byte) (op1result[i] ^ op2[i]);
		}
		return op1result;
	}
	
	/**
	 *  Read a certificate from a PEM-encoded string.
	 *  
	 *  @param pem The PEM-encoded string.
	 *  @return The certificate.
	 */
	public static final X509CertificateHolder readCertificateFromPEM(String pem)
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
	 *  Reads a certificate chain.
	 *  
	 *  @param pem PEM of the chain.
	 *  @return The chain, starting with the leaf.
	 */
	public static final List<X509CertificateHolder> readCertificateChainFromPEM(String pem)
	{
		List<X509CertificateHolder> certchain = new ArrayList<X509CertificateHolder>();
		
		try
		{
			PEMParser pemparser = new PEMParser(new StringReader(pem));
			Object object = pemparser.readPemObject();
			while (object != null)
			{
				X509CertificateHolder crtholder = new X509CertificateHolder(((PemObject) object).getContent());
				certchain.add(crtholder);
				object = pemparser.readPemObject();
			}
			pemparser.close();
		}
		catch (Exception e)
		{
			throw SUtil.throwUnchecked(e);
		}
		
		return certchain;
	}
	
	/**
	 *  Returns the subject ID of a certificate.
	 *  
	 *  @param cert The certificate.
	 *  @return The subject ID.
	 */
//	public static final X500Name getSubjectName(X509CertificateHolder cert)
//	{
//		return cert.getSubject();
//	}
	
	public static String getCommonName(X500Name name)
	{
		String ret = null;
	    RDN[] rdns = name.getRDNs(BCStyle.CN);
	    if (rdns != null && rdns.length > 0)
	    {
	        RDN rdn = rdns[0];
	        if (rdn.isMultiValued())
	        {
	            for (AttributeTypeAndValue m : rdn.getTypesAndValues())
	            {
	                if (m.getType().equals(BCStyle.CN))
	                {
	                    ret = IETFUtils.valueToString(m.getValue());
	                    break;
	                }
	            }
	        }
	        else
	        {
	        	ret = IETFUtils.valueToString(rdn.getFirst().getValue());
	        }
	    }
	    return ret;
	}
	
	/**
	 *  Check whether a certificate belongs to an entity,
	 *  either as common name or as alt name.
	 *  
	 *  @param cert The certificate.
	 *  @param entityname The entity name.
	 *  @return True, if the certificate belongs, false otherwise.
	 */
	public static final boolean checkEntity(X509CertificateHolder cert, String entityname)
	{
		if (cert == null || entityname == null)
			return false;
		
		String cn = getCommonName(cert.getSubject());
		if (cn.equals(entityname))
			return true;
		
		//Extension san = cert.getExtension(Extension.subjectAlternativeName);
		try
		{
			GeneralNames gnames = GeneralNames.fromExtensions(cert.getExtensions(), Extension.subjectAlternativeName);
			if (gnames != null)
			{
				GeneralName[] names = gnames.getNames();
				if (names != null)
				{
					for (GeneralName name : names)
					{
						String strname = IETFUtils.valueToString(name.getName());
						if (entityname.equals(strname))
							return true;
					}
				}
			}
		}
		catch (Exception e)
		{
		}
//		String val = IETFUtils.valueToString(san.getParsedValue());
		return false;
	}
	
	/**
	 *  Writes a certificate as PEM-encoded string.
	 *  
	 *  @param cert The certificate.
	 *  @return Encoded string.
	 */
	public static final String writeCertificateAsPEM(X509CertificateHolder cert)
	{
		try
		{
			ByteArrayOutputStream boscert = new ByteArrayOutputStream();
			JcaPEMWriter pemwriter = new JcaPEMWriter(new OutputStreamWriter(boscert));
			pemwriter.writeObject(cert);
			pemwriter.close();
			
			return new String(boscert.toByteArray(), SUtil.UTF8);
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
	public static final PrivateKeyInfo readPrivateKeyFromPEM(String pem)
	{
		PrivateKeyInfo ret = null;
		
		// Two-prong approach due to bug with ECDSA keys.
		PemReader pemreader = new PemReader(new StringReader(pem));
		Object pemobject = null;
		
		if (pem.contains("-----BEGIN EC PRIVATE KEY-----"))
		{
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
		}
		
		if (ret == null)
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
	 *  Tests if a certificate is a CA certificate.
	 *  
	 *  @param cert The certificate.
	 *  @return True, if CA certificate.
	 */
	public static final boolean isCaCertificate(String cert)
	{
		X509CertificateHolder lcert = readCertificateFromPEM(cert);
		BasicConstraints bc = BasicConstraints.fromExtensions(lcert.getExtensions());
		return bc.isCA();
	}
	
	/**
	 *  Gets the signatures algorithm supported by the key provided by a certificate.
	 *  
	 *  @param cert The certificate.
	 *  @return The signature algorithm.
	 */
	public static final String getCertSigAlg(String cert)
	{
		X509CertificateHolder lcert = readCertificateFromPEM(cert);
		return getCertSigAlg(lcert);
	}
	
	/**
	 *  Gets the signatures algorithm supported by the key provided by a certificate.
	 *  
	 *  @param cert The certificate.
	 *  @return The signature algorithm.
	 */
	public static final String getCertSigAlg(X509CertificateHolder cert)
	{
		SubjectPublicKeyInfo spki = cert.getSubjectPublicKeyInfo();
		return getSigAlg(spki);
	}
	
	/**
	 *  Gets the signatures algorithm supported by the key.
	 *  
	 *  @param spki The subject key info.
	 *  @return The signature algorithm.
	 */
	public static final String getSigAlg(SubjectPublicKeyInfo spki)
	{
		String ret = spki.getAlgorithm().getAlgorithm().getId();

		if (X9ObjectIdentifiers.id_ecPublicKey.getId().equals(ret))
			ret = "ECDSA";
		else if (PKCSObjectIdentifiers.rsaEncryption.getId().equals(ret))
			ret = "RSA";
		else if (X9ObjectIdentifiers.id_dsa.getId().equals(ret))
			ret = "DSA";
		
		return ret;
	}
	
	/**
	 *  Gets the certificate chain validation rules.
	 *  @return The rules.
	 */
	protected static final CertPathValidation[] getChainValidationRules()
	{
		List<CertPathValidation> ret = new ArrayList<>();
		
		ret.add(new BasicConstraintsValidation(true));
		ret.add(new ParentCertIssuedValidation(new X509ContentVerifierProviderBuilder()
		{

			public ContentVerifierProvider build(SubjectPublicKeyInfo validatingkeyinfo) throws OperatorCreationException
			{
				return getVerifierProvider(validatingkeyinfo);
			}

			public ContentVerifierProvider build(X509CertificateHolder validatingkeyinfo) throws OperatorCreationException
			{
				return getVerifierProvider(validatingkeyinfo);
			}
			
		}));
		ret.add(new KeyUsageValidation(true));
		
		return ret.toArray(new CertPathValidation[ret.size()]);
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
				getEntropySource().getEntropy(fseed);;
				
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
		prngs.add(builder.buildCTR(eng, 256, esp.get(256).getEntropy(), false));
//		System.out.println(prngs.get(prngs.size() - 1));
		
		Mac m = new HMac(new SHA512Digest());
		prngs.add(builder.buildHMAC(m, esp.get(512).getEntropy(), false));
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
	 *  @param sigalg Signature scheme / certificate key algorithm to use, e.g. RSA, DSA, ECDSA.
	 *  @param schemeconf Additional scheme configuration, may be null.
	 *  @param digalg Hash algorithm to use for certificate signature.
	 *  @param strength Strength of the key.
	 *  @param daysvalid Number of days valid.
	 *  @param extensions Certificate extensions.
	 *  @return Generated Certificate and private key as PEM-encoded strings.
	 */
	protected static final Tuple2<String, String> createCertificateBySpecification(String issuercert, String issuerkey, X500Name subject, String sigalg, String schemeconf, String digalg, int strength, int daysvalid, Extension... extensions)
	{
		try
		{
			X500Name issuer = null;
			X509CertificateHolder loadedissuercert = null;
			String sigspec = null;
			
			if (issuercert == null)
			{
				issuer = subject;
				sigspec = digalg + "WITH" + sigalg;
			}
			else
			{
				loadedissuercert = SSecurity.readCertificateFromPEM(issuercert);
				issuer = loadedissuercert.getSubject();
				sigspec = digalg + "WITH" + getCertSigAlg(loadedissuercert);
			}
			
			byte[] serialbytes = new byte[20];
			SSecurity.getSecureRandom().nextBytes(serialbytes);
			BigInteger serial = new BigInteger(1, serialbytes);
			
			AsymmetricCipherKeyPair pair = createKeyPair(sigalg, schemeconf, strength);
			
			long notafterts = System.currentTimeMillis() + daysvalid*24L*3600L*1000L;
			Date notafter = new Date(notafterts);
			
			BcX509v3CertificateBuilder builder = null;
			PrivateKeyInfo pki = null;
		
			SubjectPublicKeyInfo spki = SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(pair.getPublic());
			DefaultDigestAlgorithmIdentifierFinder digalgfinder = new DefaultDigestAlgorithmIdentifierFinder();
			BcDigestCalculatorProvider dcp = new BcDigestCalculatorProvider();
			X509ExtensionUtils utils = new X509ExtensionUtils(dcp.get(digalgfinder.find(digalg)));
			SubjectKeyIdentifier ski = utils.createSubjectKeyIdentifier(spki);
			
			SubjectPublicKeyInfo parentspki = null;
			if (loadedissuercert != null)
			{
				parentspki = loadedissuercert.getSubjectPublicKeyInfo();
			}
			else
			{
				parentspki = spki;
			}
			AuthorityKeyIdentifier aki = utils.createAuthorityKeyIdentifier(parentspki);
			
			pki = PrivateKeyInfoFactory.createPrivateKeyInfo(pair.getPrivate());
			
			PrivateKeyInfo parentpki = null;
			if (issuerkey != null)
			{
				parentpki = SSecurity.readPrivateKeyFromPEM(issuerkey);
			}
			else
			{
				parentpki = pki;
			}
			
			ContentSigner signer = getSigner(sigspec, parentpki);
			
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
	protected static final AsymmetricCipherKeyPair createKeyPair(String alg, String algconf, int strength)
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
			if (algconf == null || "BRAINPOOL".equals(algconf.toUpperCase()))
			{
				if (strength > 384)
					curvname = "brainpoolp512r1";
				else if (strength > 256)
					curvname = "brainpoolp384r1";
				else
					curvname = "brainpoolp256r1";
			}
			else if ("NIST K".equals(algconf.toUpperCase()))
			{
				if (strength > 384)
					curvname = "K-571";
				else if (strength > 256)
					curvname = "K-409";
				else
					curvname = "K-283";
			}
			else
			{
				if (strength > 384)
					curvname = "P-521";
				else if (strength > 256)
					curvname = "P-384";
				else
					curvname = "P-256";
			}
			
			X9ECParameters x9 = CustomNamedCurves.getByName(curvname);
			if (x9 == null)
				x9 = ECNamedCurveTable.getByName(curvname);
			ASN1ObjectIdentifier oid = ECNamedCurveTable.getOID(curvname);
			ECNamedDomainParameters dparams = new ECNamedDomainParameters(oid, x9.getCurve(), x9.getG(), x9.getN(), x9.getH(), x9.getSeed());
			ECKeyGenerationParameters kgparams = new ECKeyGenerationParameters(dparams, SSecurity.getSecureRandom());
			ECKeyPairGenerator kpg = new ECKeyPairGenerator();
			kpg.init(kgparams);
			pair = kpg.generateKeyPair();
		}
		
		if (pair == null)
			throw new IllegalArgumentException("Could not generate key pair: Signature scheme " + alg + " not found.");
		
		return pair;
	}
	
	/**
	 *  Gets a signer based on a private key to identify the algorithm.
	 * 
	 *  @param pki The private key.
	 *  @return A content signer.
	 */
	protected static final ContentSigner getSigner(String algospec, PrivateKeyInfo pki)
	{
		try
		{
			String[] algs = algospec.split("WITH");
			String sigalg = algs[1];
			String digalg = algs[0];
			
			AsymmetricKeyParameter privkeyparam = null;
			// Fix Bouncy bug?
			if ("ECDSA".equals(sigalg))
			{
				AlgorithmIdentifier algid = pki.getPrivateKeyAlgorithm();
				Object aparams = algid.getParameters();
				X962Parameters params = null;
				if (aparams instanceof X962Parameters)
					params = (X962Parameters) aparams;
				else if (aparams instanceof X9ECParameters)
					params = new X962Parameters((X9ECParameters) aparams);
				else
					params = new X962Parameters((ASN1ObjectIdentifier) aparams);
	
	            X9ECParameters x9;
	            ECDomainParameters dparams;
	            
	            ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier)params.getParameters();
	
	            x9 = CustomNamedCurves.getByOID(oid);
	            if (x9 == null)
	            {
	                x9 = ECNamedCurveTable.getByOID(oid);
	            }
	            dparams = new ECNamedDomainParameters(oid, x9.getCurve(), x9.getG(), x9.getN(), x9.getH(), x9.getSeed());
	            
	
	            ECPrivateKey ec = ECPrivateKey.getInstance(pki.parsePrivateKey());
	            BigInteger d = ec.getKey();
	
	            privkeyparam = new ECPrivateKeyParameters(d, dparams);
			}
			else
			{
				privkeyparam = PrivateKeyFactory.createKey(pki);
			}
			
			DefaultSignatureAlgorithmIdentifierFinder sigalgfinder = new DefaultSignatureAlgorithmIdentifierFinder();
			DefaultDigestAlgorithmIdentifierFinder digalgfinder = new DefaultDigestAlgorithmIdentifierFinder();
			BcContentSignerBuilder signerbuilder = null;
			if ("ECDSA".equals(sigalg))
				signerbuilder = new BcECContentSignerBuilder(sigalgfinder.find(algospec), digalgfinder.find(digalg));
			else if ("RSA".equals(sigalg))
				signerbuilder = new BcRSAContentSignerBuilder(sigalgfinder.find(algospec), digalgfinder.find(digalg));
			else if ("DSA".equals(sigalg))
				signerbuilder = new BcDSAContentSignerBuilder(sigalgfinder.find(algospec), digalgfinder.find(digalg));
			return signerbuilder.build(privkeyparam);
		}
		catch (Exception e)
		{
			throw SUtil.throwUnchecked(e);
		}
	}
	
	/**
	 *  Gets a verifier based on a certificate to identify the algorithm.
	 * 
	 *  @param cert The certificate.
	 *  @return A content verifier.
	 */
	protected static final ContentVerifier getDefaultVerifier(X509CertificateHolder cert)
	{
		DefaultSignatureAlgorithmIdentifierFinder saf = new DefaultSignatureAlgorithmIdentifierFinder();
		String sig = getCertSigAlg(cert);
		AlgorithmIdentifier algspec = saf.find(DEFAULT_SIGNATURE_HASH + "WITH" + sig);
		
		try
		{
			return getVerifierProvider(cert).get(algspec);
		}
		catch (Exception e)
		{
			throw SUtil.throwUnchecked(e);
		}
	}
	
	/**
	 *  
	 *  Gets a verifier provider based on a certificate to identify the algorithm.
	 *  
	 *  @param keyinfo The certificate or key info.
	 *  @return The content verifier provider.
	 */
	protected static final ContentVerifierProvider getVerifierProvider(Object keyinfo)
	{
		String sigalg = null;
		if (keyinfo instanceof X509CertificateHolder)
			sigalg = getCertSigAlg((X509CertificateHolder) keyinfo);
		else
			sigalg = getSigAlg((SubjectPublicKeyInfo) keyinfo);
			
			
		DefaultDigestAlgorithmIdentifierFinder digalgfinder = new DefaultDigestAlgorithmIdentifierFinder();
		BcContentVerifierProviderBuilder verifierbuilder = null;
		if ("ECDSA".equals(sigalg))
			verifierbuilder = new BcECContentVerifierProviderBuilder(digalgfinder);
		else if ("RSA".equals(sigalg))
			verifierbuilder = new BcRSAContentVerifierProviderBuilder(digalgfinder);
		else if ("DSA".equals(sigalg))
			verifierbuilder = new BcDSAContentVerifierProviderBuilder(digalgfinder);
		
		try
		{
			if (keyinfo instanceof X509CertificateHolder)
				return verifierbuilder.build((X509CertificateHolder) keyinfo);
			else
				return verifierbuilder.build(PublicKeyFactory.createKey(((SubjectPublicKeyInfo) keyinfo)));
		}
		catch (Exception e)
		{
			throw SUtil.throwUnchecked(e);
		}
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
	
//	/**
//	 *  Main for testing.
//	 */
//	public static void main(String[] args)
//	{
//		System.out.println(getSecureRandom().nextInt());
////		SecureRandom sec = new SecureRandom();
//	}
}
