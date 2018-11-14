package org.activecomponents.webservice.messages;

/**
 *  Message for pulling a result.
 */
public class PullResultMessage extends BaseMessage
{
	//todo: support terminate with exception
	
	/**
	 *  Create a new message.
	 */ 
	public PullResultMessage()
	{
	}
	
	/**
	 *  Create a new message.
	 */ 
	public PullResultMessage(String callid)
	{
		super(callid);
	}
}
