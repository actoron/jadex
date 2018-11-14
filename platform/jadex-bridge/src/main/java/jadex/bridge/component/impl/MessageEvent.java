package jadex.bridge.component.impl;

import jadex.bridge.component.IMsgHeader;
import jadex.bridge.service.types.security.IMsgSecurityInfos;

/**
 *  Represents the event of a sent or received message for monitoring of communication.
 */
public class MessageEvent
{
	public static enum Type
	{
		SENT, RECEIVED;
	}
	
	//-------- attributes --------
	
	/** The event type. */
	protected Type	type;
	
	/** The security infos (only for received). */
	protected IMsgSecurityInfos	secinfos;
	
	/** The message header. */
	protected IMsgHeader	header;
	
	/** The message body. */
	protected Object	body;
	
	//-------- constructors --------
	
	/**
	 *  Bean constructor.
	 */
	public MessageEvent()
	{
	}
	
	/**
	 *  Instance constructor.
	 */
	public MessageEvent(Type type, IMsgSecurityInfos secinfos, IMsgHeader header, Object body)
	{
		this.type	= type;
		this.secinfos	= secinfos;
		this.header	= header;
		this.body	= body;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the type.
	 */
	public Type getType()
	{
		return type;
	}
	
	/**
	 *  Set the type.
	 */
	public void setType(Type type)
	{
		this.type = type;
	}
	
	/**
	 *  Get the security infos.
	 */
	public IMsgSecurityInfos getSecinfos()
	{
		return secinfos;
	}
	
	/**
	 *  Set the security infos.
	 */
	public void setSecinfos(IMsgSecurityInfos secinfos)
	{
		this.secinfos = secinfos;
	}
	
	/**
	 *  Get the header.
	 */
	public IMsgHeader getHeader()
	{
		return header;
	}
	
	/**
	 *  Set the header.
	 */
	public void setHeader(IMsgHeader header)
	{
		this.header = header;
	}
	
	/**
	 *  Get the body.
	 */
	public Object getBody()
	{
		return body;
	}
	
	/**
	 *  Set the body.
	 */
	public void setBody(Object body)
	{
		this.body = body;
	}
}
