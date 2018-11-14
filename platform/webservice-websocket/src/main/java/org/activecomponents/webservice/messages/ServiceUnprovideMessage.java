package org.activecomponents.webservice.messages;

import jadex.bridge.service.IServiceIdentifier;

/**
 *  Message for unproviding a service.
 */
public class ServiceUnprovideMessage extends BaseMessage
{
	/** The service id of the service to invoke. */
	protected IServiceIdentifier sid;
	
	/**
	 *  Create a new command.
	 */ 
	public ServiceUnprovideMessage()
	{
	}

	/**
	 *  Create a new command.
	 *  @param serviceId The serviceid;
	 */
	public ServiceUnprovideMessage(String callid, IServiceIdentifier serviceid)
	{
		super(callid);
		this.sid = serviceid;
	}

	/** 
	 *  Get the serviceId.
	 *  @return Tthe serviceId
	 */
	public IServiceIdentifier getServiceId()
	{
		return sid;
	}

	/**
	 *  Set the serviceId.
	 *  @param serviceId The serviceId to set
	 */
	public void setServiceId(IServiceIdentifier serviceid)
	{
		this.sid = serviceid;
	}
}

