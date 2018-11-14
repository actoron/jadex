package jadex.platform.service.security.auth;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import jadex.commons.SUtil;

/**
 *  Secret based on inline PEM-encoded X.509 certificates and key.
 *
 */
public class X509PemStringsSecret extends AbstractX509PemSecret
{
	/** Prefix used to encode secret type as strings. */
	public static final String PREFIX = "pem";
	
	/** The trust anchor file/ca cert. */
//	protected String cacert;
	
	/** The local certificate. */
	protected String cert;
	
	/** The local certificate key. */
	protected String key;
	
	public X509PemStringsSecret(String encodedstring)
	{
		String basestring = encodedstring.substring(PREFIX.length() + 1);
		String[] toks = basestring.split(":");
		if (toks.length != 1 && toks.length != 2)
			throw new IllegalArgumentException("Could not decode pem file string: " +encodedstring);
		
//		cert = SUtil.unescapeLineBreaks(toks[0]);
		cert = toks[0].replace('|', '\n');
//		cacert = new String(Base64.decodeNoPadding(toks[0].getBytes(SUtil.UTF8)), SUtil.UTF8);
		if (toks.length > 1)
		{
			key = toks[1].replace('|', '\n');
//			key = SUtil.unescapeLineBreaks(toks[1]);
//			key = toks[1];
//			key = new String(Base64.decodeNoPadding(toks[2].getBytes(SUtil.UTF8)), SUtil.UTF8);
		}
	}
	
	/**
	 *  Creates the secret.
	 *  
	 *  @param cacert Path to the trust anchor certificate.
	 *  @param cert Path to the local certificate.
	 *  @param key Path to the local certificate key.
	 */
	public X509PemStringsSecret(String cert, String key)
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
//		if (cacert == null)
//			throw new RuntimeException("CA Certificate not available:" + toString());
//		
//		return new ByteArrayInputStream(cacert.getBytes(SUtil.UTF8));
//	}
	
	/**
	 *  Opens the local certificate.
	 * 
	 *  @return The local certificate.
	 */
	public InputStream openCertificate()
	{
		if (cert == null)
			throw new RuntimeException("Certificate not available:" + toString());
		
		return new ByteArrayInputStream(cert.getBytes(SUtil.UTF8));
	}
	
	/**
	 *  Opens the private key used for signing.
	 *  
	 *  @return The private key.
	 */
	public InputStream openPrivateKey()
	{
		if (key == null)
			throw new RuntimeException("Key not available:" + toString());
		
		return new ByteArrayInputStream(key.getBytes(SUtil.UTF8));
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
//		String ret = PREFIX + ":" + new String(Base64.encodeNoPadding(cacert.getBytes(SUtil.UTF8)), SUtil.UTF8);
//		String ret = PREFIX + ":" + new String(Base64.encodeNoPadding(cert.getBytes(SUtil.UTF8)), SUtil.UTF8);
//		String ret = PREFIX + ":" + SUtil.escapeLineBreaks(cert);
//		String ret = PREFIX + ":" + cert.replace("\r", "").replace("\n", "\\n");
		String ret = PREFIX + ":" + cert.replace('\r', '\n').replace('\n', '|');
		if (canSign())
		{
//			ret += ":" + new String(Base64.encodeNoPadding(cert.getBytes(SUtil.UTF8)), SUtil.UTF8);
//			ret += ":" + new String(Base64.encodeNoPadding(key.getBytes(SUtil.UTF8)), SUtil.UTF8);
//			ret += ":" + SUtil.escapeLineBreaks(key);
//			ret += ":" + key.replace("\r", "").replace("\n", "");
			ret += ":" + key.replace('\r', '\n').replace('\n', '|');
		}
		return ret;
	}
}
