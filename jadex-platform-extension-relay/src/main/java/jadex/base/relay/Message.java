package jadex.base.relay;

import java.io.InputStream;

import jadex.commons.future.Future;

/**
 *  Struct for a message to be delivered to a platform.
 */
public class Message
{
	//-------- attributes --------
	
	/** The message type. */
	protected int	msgtype;
	
	/** The message content (read from stream). */
	protected InputStream	content;
	
	/** The future to be notified, when sending is completed. */
	protected Future<Void>	fut;
	
	//-------- constructors --------
	
	/**
	 *  Create a new message.
	 */
	public Message(int msgtype, InputStream content)
	{
		this.msgtype	= msgtype;
		this.content	= content;
		this.fut	= new Future<Void>();
	}
	
	//-------- methods --------
	
	/**
	 *  Get the message type.
	 */
	public int	getMessageType()
	{
		return msgtype;
	}
	
	/**
	 *  Get the message content.
	 */
	public InputStream	getContent()
	{
		return content;
	}
	
	/**
	 *  Get the future.
	 */
	public Future<Void>	getFuture()
	{
		return fut;
	}
}
