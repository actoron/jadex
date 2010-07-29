package jadex.standalone.service;

import jadex.bridge.IComponentIdentifier;
import jadex.service.IServiceIdentifier;

/**
 * 
 */
public class ProxyInfo
{
	//-------- attributes --------
	
	/** The rms. */
	protected IComponentIdentifier rms;
	
	/** The service identifier. */
	protected IServiceIdentifier sid; 
	
	/** The service type. */
	protected Class service;

	//-------- constructors --------
	
	/**
	 *  Create a new proxy info.
	 */
	public ProxyInfo()
	{
	}

	/**
	 *  Create a new proxy info.
	 */
	public ProxyInfo(IComponentIdentifier rms, IServiceIdentifier sid, Class service)
	{
		this.rms = rms;
		this.sid = sid;
		this.service = service;
	}

	//-------- methods --------
	
	/**
	 *  Get the rms.
	 *  @return the rms.
	 */
	public IComponentIdentifier getRms()
	{
		return rms;
	}

	/**
	 *  Set the rms.
	 *  @param rms The rms to set.
	 */
	public void setRms(IComponentIdentifier rms)
	{
		this.rms = rms;
	}

	/**
	 *  Get the sid.
	 *  @return the sid.
	 */
	public IServiceIdentifier getServiceIdentifier()
	{
		return sid;
	}

	/**
	 *  Set the sid.
	 *  @param sid The sid to set.
	 */
	public void setServiceIdentifier(IServiceIdentifier sid)
	{
		this.sid = sid;
	}

	/**
	 *  Get the service.
	 *  @return the service.
	 */
	public Class getService()
	{
		return service;
	}

	/**
	 *  Set the service.
	 *  @param service The service to set.
	 */
	public void setService(Class service)
	{
		this.service = service;
	}	
	
}
