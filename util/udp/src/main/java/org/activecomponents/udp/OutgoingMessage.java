package org.activecomponents.udp;

import java.util.Set;

/**
 *  An outgoing message.
 *
 */
public class OutgoingMessage
{
	/** Message data. */
	protected byte[] data;
	
	/** Set of unconfirmed parts. */
	protected Set<Long> unconfirmedparts;
	
	/** Result callback. */
	protected IUdpCallback<Boolean> callback;
	
	/**
	 *  Creates a new outgoing message.
	 *  @param data Data of the message.
	 *  @param callback Result callback.
	 */
	public OutgoingMessage(byte[] data, IUdpCallback<Boolean> callback)
	{
		this.data = data;
		this.callback = callback;
	}
	
	/** 
	 *  Access the unconfirmed parts.
	 *  @return Unconfirmed parts.
	 */
	public Set<Long> getUnconfirmedParts()
	{
		return unconfirmedparts;
	}
	
	/**
	 *  Sets the unconfirmed parts.
	 *  @param unconfirmedparts Unconfirmed parts.
	 */
	public void setUnconfirmedParts(Set<Long> unconfirmedparts)
	{
		this.unconfirmedparts = unconfirmedparts;
	}
	
	/**
	 *  Returns the data.
	 *  @return The data.
	 */
	public byte[] getData()
	{
		return data;
	}
	
	/**
	 *  Clears the data.
	 */
	public void clearData()
	{
		data = null;
	}
	
	/**
	 *  Returns the callback.
	 *  @return The callback.
	 */
	public IUdpCallback<Boolean> getCallback()
	{
		return callback;
	}
}
