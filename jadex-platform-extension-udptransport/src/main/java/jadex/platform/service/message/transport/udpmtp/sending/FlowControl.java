package jadex.platform.service.message.transport.udpmtp.sending;

import jadex.platform.service.message.transport.udpmtp.STunables;

/**
 *  The flow control.
 *
 */
public class FlowControl
{
	/** Squared minimum send-able size */
	protected static final int MIN_SENDABLE_BYTES_2 = STunables.MIN_SENDABLE_BYTES * STunables.MIN_SENDABLE_BYTES;
	
	/** The current send quota. */
	protected int sendquota;
	
	/** The current maximum send quota. */
	protected int maxsendquota;
	
	/** The threshold. */
	protected int threshold;
	
	protected volatile boolean startmode = true;
	
	/**
	 *  Creates the flow control.
	 */
	public FlowControl()
	{
		this.sendquota = STunables.MIN_SENDABLE_BYTES;
		this.maxsendquota = STunables.MIN_SENDABLE_BYTES;
//		this.sendquota = 524288;
//		this.maxsendquota = 524288;
		this.threshold = STunables.MIN_SENDABLE_BYTES * 2;
	}
	
	
	
	/**
	 *  Gets the maximum send quota.
	 *
	 *  @return The maximum send quota.
	 */
	public synchronized int getMaxSendQuota()
	{
		return maxsendquota;
	}

	

	/**
	 *  Gets the send quota.
	 *
	 *  @return The send quota.
	 */
	public synchronized int getSendQuota()
	{
		return sendquota;
	}

	/**
	 *  Adds an amount to the send quota and returns the new value.
	 *  
	 *  @param add The value to add.
	 *  @return The new send quota.
	 */
	public synchronized int addAndGetQuota(int add)
	{
		sendquota += add;
		return sendquota;
	}
	
	/**
	 *  Subtracts an amount to the send quota and returns the new value.
	 *  
	 *  @param sub The value to subtract.
	 *  @return The new send quota.
	 */
	public synchronized int subtractAndGetQuota(int sub)
	{
		sendquota -= sub;
		return sendquota;
	}

	public synchronized void ack(int size)
	{
		sendquota += size;
		//missedsize -= size;
		if (maxsendquota < threshold)
		{
			
			if (maxsendquota < STunables.MAX_SENDABLE_BYTES)
			{
				maxsendquota += STunables.MIN_SENDABLE_BYTES;
				sendquota += STunables.MIN_SENDABLE_BYTES;
//				System.out.println("SQ SP RS INC: " + sendquota + " " +maxsendquota);
			}
		}
		else
		{
			if (maxsendquota < STunables.MAX_SENDABLE_BYTES)
			{
//				int old = maxsendquota;
				int inc = MIN_SENDABLE_BYTES_2 / maxsendquota;
				maxsendquota += inc;
				sendquota += inc;
//				System.out.println("SQ RS INC: " + sendquota + " " +maxsendquota);
			}
		}
	}
	
	protected int missedsize = 0;
	
	public synchronized void resend(int size)
	{
		missedsize += size;
//		if (maxsendquota < threshold)
		if (missedsize > STunables.MIN_SENDABLE_BYTES)
		{
			missedsize = 0;
			threshold = Math.max(STunables.MIN_SENDABLE_BYTES * 2, maxsendquota >> 1);
			int diff = maxsendquota;
			maxsendquota = STunables.MIN_SENDABLE_BYTES;
			diff -= maxsendquota;
			sendquota -= diff;
		}
//		else
//		{
//			int dec = MIN_SENDABLE_BYTES_2 / maxsendquota;
//			if ((maxsendquota - dec) > STunables.MIN_SENDABLE_BYTES)
//			{
//				
//				maxsendquota -= dec;
//				sendquota -= dec;
//			}
//		}
	}
}
