package jadex.platform.service.security;

import jadex.bridge.service.types.security.MechanismInfo;
import jadex.commons.future.IFuture;

import java.security.cert.Certificate;

/**
 * 
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
	 *  Get the secser.
	 *  @return The secser.
	 */
	public SecurityService getSecurityService()
	{
		return secser;
	}

	/**
	 *  Aquire a certificate.
	 */
	public abstract IFuture<Certificate> acquireCertificate(final String name);

	/**
	 *  Get the mechanism info for the gui.
	 */
	public abstract MechanismInfo getMechanismInfo();
	
	/**
	 *  Set a mechanism parameter value.
	 */
	public abstract void setParameterValue(String name, Object value);

}
