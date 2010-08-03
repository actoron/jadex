package jadex.base.service.remote;

import jadex.bridge.IComponentIdentifier;
import jadex.service.IServiceIdentifier;

/**
 *  Info struct that holds all necessary information to generate
 *  a proxy on the local platform. Is necessary because a proxy
 *  cannot be directly created on the remote side and then sent 
 *  per message to the calling side.
 */
public class ProxyInfo
{
	//-------- attributes --------
	
	/** The rms. */
	protected IComponentIdentifier rms;
	
	/** The service identifier. */
	protected IServiceIdentifier sid; 
	
	/** The service type. */
	protected Class type;

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
	public ProxyInfo(IComponentIdentifier rms, IServiceIdentifier sid, Class type)
	{
		this.rms = rms;
		this.sid = sid;
		this.type = type;
	}

	//-------- methods --------
	
	/**
	 *  Get the rms.
	 *  @return the rms.
	 */
	public IComponentIdentifier getRemoteManagementServiceIdentifier()
	{
		return rms;
	}

	/**
	 *  Set the rms.
	 *  @param rms The rms to set.
	 */
	public void setRemoteManagementServiceIdentifier(IComponentIdentifier rms)
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
	 *  Get the type.
	 *  @return the type.
	 */
	public Class getType()
	{
		return type;
	}

	/**
	 *  Set the type.
	 *  @param type The type to set.
	 */
	public void setType(Class type)
	{
		this.type = type;
	}

}
