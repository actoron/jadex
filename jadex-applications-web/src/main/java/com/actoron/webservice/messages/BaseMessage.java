package com.actoron.webservice.messages;

/**
 * 
 */
public class BaseMessage
{
	/** The callid. */
	protected String callid;
	
	/**
	 *  Create a new message.
	 */
	public BaseMessage()
	{
	}
	
	/**
	 *  Create a new message.
	 */
	public BaseMessage(String callid)
	{
		this.callid = callid;
	}

	/** 
	 *  Get the callid.
	 *  @return Tthe callid
	 */
	public String getCallid()
	{
		return callid;
	}

	/**
	 *  Set the callid.
	 *  @param callid The callid to set
	 */
	public void setCallid(String callid)
	{
		this.callid = callid;
	}
}
