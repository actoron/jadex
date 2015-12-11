package jadex.platform.service.security;

import java.security.cert.Certificate;

import jadex.bridge.service.types.security.MechanismInfo;
import jadex.commons.Properties;
import jadex.commons.future.IFuture;

/**
 *  Abstract super class for certificate acquisition mechanisms.
 */
public abstract class AAcquisitionMechanism
{
	//-------- attributes --------
	
	/** The component. */
	protected SecurityService secser;

	//-------- methods --------

	/**
	 *  Init the mechanism.
	 */
	public void init(SecurityService secser)
	{
		this.secser = secser;
	}
	
	/**
	 *  Get the security service.
	 *  @return The security service.
	 */
	public SecurityService getSecurityService()
	{
		return secser;
	}

	/**
	 *  Acquire a certificate.
	 *  @param name The platform prefix of the target.
	 *  @return The certificate.
	 */
	public abstract IFuture<Certificate> acquireCertificate(final String name);

	/**
	 *  Get the mechanism info for the gui.
	 *  @return The mechanism info.
	 */
	public abstract MechanismInfo getMechanismInfo();
	
	/**
	 *  Set a mechanism parameter value.
	 *  @param name The mechanism name.
	 *  @param value The mechanism value.
	 */
	public abstract void setParameterValue(String name, Object value);
	
	/**
	 *  Get the properties of the mechanism.
	 */
	public abstract Properties getProperties();

	/**
	 *  Set the properties of the mechanism.
	 */
	public abstract void setProperties(Properties props);
}
