package com.actoron.webservice.messages;

/**
 * 
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
