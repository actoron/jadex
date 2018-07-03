package org.activecomponents.udp;

import java.util.HashSet;
import java.util.Set;

/**
 *  An incoming message.
 *
 */
public class IncomingMessage
{
	/** Message data. */
	protected byte[][] data;
	
	/** Set of missing parts. */
	protected Set<Integer> missingparts;
	
	/** Flag indicating internal messages. */
	protected boolean internal;
	
	/**
	 *  Creates a new internal message.
	 * @param size Number of parts.
	 * @param internal Flag indicating an internal message.
	 */
	public IncomingMessage(int size, boolean internal)
	{
		data = new byte[size][];
		this.internal = internal;
		missingparts = new HashSet<Integer>();
		for (int i = 0; i < size; ++i)
		{
			missingparts.add(i);
		}
	}
	
	/**
	 *  Adds a part to the message.
	 *  @param partnum The part number.
	 *  @param buffer The buffer containing part data.
	 *  @param offset Offset position of the part data.
	 */
	public void addPart(int partnum, byte[] buffer, int offset)
	{
		data[partnum] = new byte[buffer.length - offset];
		System.arraycopy(buffer, offset, data[partnum], 0, data[partnum].length);
		missingparts.remove(partnum);
	}
	
	/**
	 *  Returns true if the message is complete.
	 *  @return True, if complete.
	 */
	public boolean isDone()
	{
		return missingparts.isEmpty();
	}
	
	public boolean isInternal()
	{
		return internal;
	}
	
	/**
	 *  Returns the full message.
	 *  @return The message.
	 */
	public byte[] getMessage()
	{
		int size = 0;
		for (int i = 0; i < data.length; ++i)
		{
			size += data[i].length;
		}
		byte[] ret = new byte[size];
		int offset = 0;
		for (int i = 0; i < data.length; ++i)
		{
			System.arraycopy(data[i], 0, ret, offset, data[i].length);
			offset += data[i].length;
		}
		
		return ret;
	}
}
