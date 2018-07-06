package org.activecomponents.webservice.messages;

/**
 *  A terminate service call message.
 */
public class ServiceTerminateInvocationMessage extends BaseMessage
{
	//todo: support terminate with exception
	
	/**
	 *  Create a new command.
	 */ 
	public ServiceTerminateInvocationMessage()
	{
	}
	
	/**
	 *  Create a new command.
	 */ 
	public ServiceTerminateInvocationMessage(String callid)
	{
		super(callid);
	}
}
