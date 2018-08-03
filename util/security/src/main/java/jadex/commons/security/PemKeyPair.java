package jadex.commons.security;

import java.util.List;

import org.bouncycastle.cert.X509CertificateHolder;

import jadex.commons.SUtil;

/**
 *  PEM-encoded key/certificate pair.
 *
 */
public class PemKeyPair
{
	/** The encoded certificate. */
	protected String certificate;
	
	/** The encoded key. */
	protected String key;
	
	public PemKeyPair()
	{
	}
	
	/**
	 *  Gets encoded certificate.
	 *  @return Encoded certificate.
	 */
	public String getCertificate()
	{
		return certificate;
	}
	
	/**
	 *  Sets encoded certificate.
	 *  @param certificate Encoded certificate.
	 */
	public void setCertificate(String certificate)
	{
		List<X509CertificateHolder> chain = SSecurity.readCertificateChainFromPEM(certificate);
		if (chain.size() > 1)
		{
			System.out.println("CHAIN??? ");
			certificate = SSecurity.writeCertificateAsPEM(SSecurity.readCertificateFromPEM(certificate));
		}
		this.certificate = certificate;
	}
	
	/**
	 *  Gets encoded key.
	 *  @return Encoded key.
	 */
	public String getKey()
	{
		return key;
	}
	
	/**
	 *  Sets encoded key.
	 *  @param key Encoded key.
	 */
	public void setKey(String key)
	{
		this.key = key;
	}
	
	/**
	 *  Hashcode.
	 */
	public int hashCode()
	{
		String[] combined = new String[2];
		combined[0] = certificate;
		combined[1] = key;
		return SUtil.arrayHashCode(combined);
	}
	
	/**
	 *  Equals method.
	 */
	public boolean equals(Object obj)
	{
		if (obj instanceof PemKeyPair)
		{
			PemKeyPair other = (PemKeyPair) obj;
			if (SUtil.equals(certificate, other.certificate) && SUtil.equals(key, other.key))
				return true;
					
		}
		return false;
	}
	
	/**
	 *  toString()
	 */
	public String toString()
	{
		if (certificate == null)
			return "Empty PemKeyPair";
		
		return SSecurity.getCommonName(SSecurity.readCertificateFromPEM(certificate).getSubject());
	}
}
