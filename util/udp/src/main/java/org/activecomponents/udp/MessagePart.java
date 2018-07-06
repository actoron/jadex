/**
 * 
 */
package org.activecomponents.udp;

import java.net.DatagramPacket;

/**
 *  A message part in transmission.
 *
 */
public class MessagePart
{
	/** Message containing this part. */
	protected OutgoingMessage message;
	
	/** Packet ID. */
	protected long id;
	
	/** Part sent time. */
	protected long senttime;
	
	/** Part scheduled resend time. */
	protected long resendtime;
	
	/** The packet. */
	protected DatagramPacket dgp;
	
	/** Flag if the part has been send more than once. */
	protected boolean resend = false;
	
	/**
	 *  Creates a queued message part.
	 */
	public MessagePart(long id, DatagramPacket dgp, OutgoingMessage message)
	{
		this.id = id;
		this.dgp = dgp;
		this.message = message;
	}
	
	/**
	 *  Gets the ID.
	 *  
	 *  @return The ID.
	 */
	public long getId()
	{
		return id;
	}
	
	/**
	 *  Returns the message containing this part.
	 *  @return The message.
	 */
	public OutgoingMessage getMessage()
	{
		return message;
	}
	
	/**
	 *  Gets the senttime.
	 *
	 *  @return The senttime
	 */
	public long getSentTime()
	{
		return senttime;
	}
	
	/**
	 *  Sets the senttime.
	 *
	 *  @param senttime The senttime to set
	 */
	public void setSentTime(long senttime)
	{
		this.senttime = senttime;
	}
	
	/**
	 *  Gets the resendtime.
	 *
	 *  @return The resendtime
	 */
	public long getResendTime()
	{
		return resendtime;
	}
	
	/**
	 *  Sets the resendtime.
	 *
	 *  @param resendtime The resendtime to set
	 */
	public void setResentTime(long resendtime)
	{
		this.resendtime = resendtime;
	}
	
	/**
	 *  Gets the packet.
	 *  @return The packet.
	 */
	public DatagramPacket getPacket()
	{
		return dgp;
	}
	
	/**
	 *  Gets the resend.
	 *
	 *  @return The resend
	 */
	public boolean isResend()
	{
		return resend;
	}
	
	/**
	 *  Sets the resend.
	 *
	 *  @param resend The resend to set
	 */
	public void setResend(boolean resend)
	{
		this.resend = resend;
	}
}
