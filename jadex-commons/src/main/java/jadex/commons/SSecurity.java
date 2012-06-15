package jadex.commons;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.util.Date;

import sun.security.x509.AlgorithmId;
import sun.security.x509.CertificateAlgorithmId;
import sun.security.x509.CertificateIssuerName;
import sun.security.x509.CertificateSerialNumber;
import sun.security.x509.CertificateSubjectName;
import sun.security.x509.CertificateValidity;
import sun.security.x509.CertificateVersion;
import sun.security.x509.CertificateX509Key;
import sun.security.x509.X500Name;
import sun.security.x509.X509CertImpl;
import sun.security.x509.X509CertInfo;

/**
 * 
 */
public class SSecurity
{
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
	    	
	    	KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");  
	 	    keyPairGenerator.initialize(1024);  
	 	    
//	 	    System.out.println("Generating key pair, this may take a short while.");
	 	    KeyPair keys = keyPairGenerator.generateKeyPair();
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
	
	/**
	 *  Generate a certificate.
	 *  @param dn the X.509 Distinguished Name, eg "CN=Test, L=London, C=GB"
	 *  @param pair
	 *  @param days
	 *  @param algorithm
	 *  @return
	 *  @throws GeneralSecurityException
	 *  @throws IOException
	 */
	public static Certificate generateCertificate(String dn, KeyPair pair, int days, String algorithm) throws GeneralSecurityException, IOException
	{
		PrivateKey privkey = pair.getPrivate();
		X509CertInfo info = new X509CertInfo();
		Date from = new Date();
		Date to = new Date(from.getTime() + days * 86400000l);
		CertificateValidity interval = new CertificateValidity(from, to);
		BigInteger sn = new BigInteger(64, new SecureRandom());
		X500Name owner = new X500Name(dn);
 
		info.set(X509CertInfo.VALIDITY, interval);
		info.set(X509CertInfo.SERIAL_NUMBER, new CertificateSerialNumber(sn));
		info.set(X509CertInfo.SUBJECT, new CertificateSubjectName(owner));
		info.set(X509CertInfo.ISSUER, new CertificateIssuerName(owner));
		info.set(X509CertInfo.KEY, new CertificateX509Key(pair.getPublic()));
		info.set(X509CertInfo.VERSION, new CertificateVersion(CertificateVersion.V3));
		AlgorithmId algo = new AlgorithmId(AlgorithmId.md5WithRSAEncryption_oid);
		info.set(X509CertInfo.ALGORITHM_ID, new CertificateAlgorithmId(algo));
 
		// Sign the cert to identify the algorithm that's used.
		X509CertImpl cert = new X509CertImpl(info);
		cert.sign(privkey, algorithm);
 
		// Update the algorith, and resign.
		algo = (AlgorithmId)cert.get(X509CertImpl.SIG_ALG);
		info.set(CertificateAlgorithmId.NAME + "." + CertificateAlgorithmId.ALGORITHM, algo);
		cert = new X509CertImpl(info);
		cert.sign(privkey, algorithm);
		return cert;
	}   
	
	/**
	 *  Main for testing.
	 */
	public static void main(String[] args)
	{
		KeyStore ks = getKeystore("c:\\temp\\keystore", "hans", "hans", "alias");
	}
}
