package jadex.bridge.component.impl;

import jadex.bridge.component.IMsgHeader;

/**
 *  Represents the event of a sent or received message for monitoring of communication.
 */
// TODO in progress
public class MessageEvent
{
	//-------- attributes --------
	
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
}
