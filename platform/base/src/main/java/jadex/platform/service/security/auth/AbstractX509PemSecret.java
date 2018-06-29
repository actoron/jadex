package jadex.platform.service.security.auth;

import java.io.InputStream;

/**
 *  Secret based on PEM-encoded X.509 certificates.
 *
 */
public abstract class AbstractX509PemSecret extends AbstractAuthenticationSecret
{
	/**
	 *  Opens a stream to the CA/trust anchor certificate.
	 *  
	 *  @return Stream of the CA certificate.
	 */
	public abstract InputStream openTrustAnchorCert();
	
	/**
	 *  Opens the local certificate.
	 * 
	 *  @return The local certificate.
	 */
	public abstract InputStream openCertificate();
	
	/**
	 *  Opens the private key used for signing.
	 *  
	 *  @return The private key.
	 */
	public abstract InputStream openPrivateKey();
}
