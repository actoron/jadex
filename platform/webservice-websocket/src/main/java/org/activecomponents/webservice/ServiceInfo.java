package org.activecomponents.webservice;

import java.util.HashSet;
import java.util.Set;

import jadex.bridge.service.IServiceIdentifier;

/**
 *  Service info.
 */
public class ServiceInfo
{
	/** The service identifier. */
	protected IServiceIdentifier sid;
	
	/** The method names. */
	protected Set<String> methodnames = new HashSet<String>();
	
	/**
	 *  Create the service proxy.
	 */
	public ServiceInfo()
	{
	}
	
	/**
	 *  Create the service proxy.
	 *  @param sid The service identifier.
	 *  @param methodnames The method names.
	 */
	public ServiceInfo(IServiceIdentifier sid, Set<String> methodnames)
	{
		this.sid = sid;
		this.methodnames = methodnames;
	}

	/**
	 *  Get the methodNames.
	 *  @return The methodNames
	 */
	public Set<String> getMethodNames()
	{
		return methodnames;
	}

	/**
	 *  Set the methodNames.
	 *  @param methodnames The methodNames to set
	 */
	public void setMethodNames(Set<String> methodnames)
	{
		this.methodnames = methodnames;
	}

	/**
	 *  Get the serviceIdentifier.
	 *  @return The serviceIdentifier
	 */
	public IServiceIdentifier getServiceIdentifier()
	{
		return sid;
	}

	/**
	 *  Set the serviceIdentifier.
	 *  @param sid The serviceIdentifier to set
	 */
	public void setServiceIdentifier(IServiceIdentifier sid)
	{
		this.sid = sid;
	}
	
	/**
	 *  Get the serviceIdentifier.
	 *  @return The serviceIdentifier
	 */
	public String getServiceIdentifierString()
	{
		return ""+sid;
	}

	/**
	 *  Set the serviceIdentifier.
	 *  @param sid The serviceIdentifier to set
	 */
	public void setServiceIdentifierString(String sid)
	{
	}
}
