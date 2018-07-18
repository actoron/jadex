package jadex.platform.service.security.auth;

/**
 *  Authentication token created with the X509 certificate-based process.
 *
 */
public class X509AuthToken extends AuthToken
{
	/** The certificate. */
	protected String certificate;
	
	/**
	 *  Creates the token.
	 */
	public X509AuthToken()
	{
		super();
	}
	
	/**
	 *  Gets the signing certificate.
	 *  
	 *  @return The certificate.
	 */
	public String getCertificate()
	{
		return certificate;
	}
	
	/**
	 *  Sets the signing certificate.
	 *  
	 *  @param certificate The certificate.
	 */
	public void setCertificate(String certificate)
	{
		this.certificate = certificate;
	}
}
