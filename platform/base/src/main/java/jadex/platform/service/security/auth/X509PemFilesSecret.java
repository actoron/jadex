package jadex.platform.service.security.auth;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import jadex.commons.SUtil;

/**
 *  Secret based on PEM-encoded X.509 certificate files and key.
 *
 */
public class X509PemFilesSecret extends AbstractX509PemSecret
{
	/** Prefix used to encode secret type as strings. */
	public static final String PREFIX = "pemfiles";
	
	/** The trust anchor file/ca cert. */
//	protected String cacert;
	
	/** The local certificate. */
	protected String cert;
	
	/** The local certificate key. */
	protected String key;
	
	public X509PemFilesSecret(String encodedstring)
	{
		String basestring = encodedstring.substring(PREFIX.length() + 1);
		String[] toks = basestring.split(":");
//		if (toks.length != 1 && toks.length != 3)
//			throw new IllegalArgumentException("Could not decode pem file string: " +encodedstring);
		if (toks.length != 1 && toks.length != 2)
			throw new IllegalArgumentException("Could not decode pem file string: " +encodedstring);
		
//		cacert = toks[0];
		cert = toks[0];
		if (toks.length > 1)
		{
//			cert = toks[1];
//			key = toks[2];
			key = toks[1];
		}
	}
	
	/**
	 *  Creates the secret.
	 *  
	 *  @param cacert Path to the trust anchor certificate.
	 *  @param cert Path to the local certificate.
	 *  @param key Path to the local certificate key.
	 */
	public X509PemFilesSecret(String cert, String key)
	{
//		this.cacert = cacert;
		this.cert = cert;
		this.key = key;
	}
	
	/**
	 *  Tests if the secret can be used for signing or, alternatively, verification only.
	 *  @return True, if the secret can be used for signing.
	 */
	public boolean canSign()
	{
		return cert != null && key != null;
	}
	
	/**
	 *  Opens a stream to the CA/trust anchor certificate.
	 *  
	 *  @return Stream of the CA certificate.
	 */
//	public InputStream openTrustAnchorCert()
//	{
//		try
//		{
//			return new FileInputStream(cacert);
//		}
//		catch (FileNotFoundException e)
//		{
//			throw SUtil.convertToRuntimeException(e);
//		}
//	}
	
	/**
	 *  Opens the local certificate.
	 * 
	 *  @return The local certificate.
	 */
	public InputStream openCertificate()
	{
		try
		{
			return new FileInputStream(cert);
		}
		catch (FileNotFoundException e)
		{
			throw SUtil.convertToRuntimeException(e);
		}
	}
	
	/**
	 *  Opens the private key used for signing.
	 *  
	 *  @return The private key.
	 */
	public InputStream openPrivateKey()
	{
		try
		{
			return new FileInputStream(key);
		}
		catch (FileNotFoundException e)
		{
			throw SUtil.convertToRuntimeException(e);
		}
	}
	
	/**
	 *  Hashcode.
	 */
	public int hashCode()
	{
		int ret = cert != null ? cert.hashCode() : 0;
		if (key != null)
			ret += 31 * key.hashCode();
//		ret = 31 * ret + cacert != null ? cacert.hashCode() : 0;
		return ret;
	}
	
	/**
	 *  Equals.
	 */
	public boolean equals(Object obj)
	{
		if (obj instanceof X509PemStringsSecret)
		{
			X509PemStringsSecret other = (X509PemStringsSecret) obj;
//			return SUtil.equals(cacert, other.cacert) &&
			return SUtil.equals(cert, other.cert) &&
				   SUtil.equals(key, other.key);
		}
		return false;
	}
	
	/**
	 *  Converts to encoded string.
	 */
	public String toString()
	{
//		String ret = PREFIX + ":" + cacert;
		String ret = PREFIX + ":" + cert;
		if (canSign())
		{
//			ret += ":" + cert;
			ret += ":" + key;
		}
		return ret;
	}
}
