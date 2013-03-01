package jadex.platform.service.message.transport.udpmtp.sending;

import jadex.platform.service.message.transport.udpmtp.STunables;

import java.util.concurrent.atomic.AtomicInteger;

/**
 *  The flow control.
 *
 */
public class FlowControl
{
	/** The current send quota. */
	protected AtomicInteger sendquota;
	
	/** The current maximum send quota. */
	protected int maxsendquota;
	
	protected volatile boolean startmode = true;
	
	/**
	 *  Creates the flow control.
	 */
	public FlowControl(AtomicInteger sendquota)
	{
		this.sendquota = sendquota;
		this.maxsendquota = STunables.MIN_SENDABLE_BYTES;
	}
	
	
	
	/**
	 *  Gets the maxsendquota.
	 *
	 *  @return The maxsendquota.
	 */
	public synchronized int getMaxsendQuota()
	{
		return maxsendquota;
	}

	

	/**
	 *  Gets the sendquota.
	 *
	 *  @return The sendquota.
	 */
	public AtomicInteger getSendQuota()
	{
		return sendquota;
	}



	public synchronized void ack()
	{
		if (startmode)
		{
			
			if (maxsendquota < (STunables.MAX_SENDABLE_BYTES))
			{
				maxsendquota += STunables.MIN_SENDABLE_BYTES;
				sendquota.addAndGet(STunables.MIN_SENDABLE_BYTES);
			}
		}
		else
		{
			if (maxsendquota < (STunables.MAX_SENDABLE_BYTES))
			{
				maxsendquota += 1;
				sendquota.addAndGet(1);
			}
		}
//			System.out.println(maxsendquota);
	}
	
	public synchronized void resend()
	{
		if (startmode)
		{
			if (maxsendquota - STunables.MIN_SENDABLE_BYTES > STunables.MIN_SENDABLE_BYTES)
			{
				maxsendquota -= (STunables.MIN_SENDABLE_BYTES);
				sendquota.addAndGet(-(STunables.MIN_SENDABLE_BYTES));
			}
			startmode = false;
		}
		else
		{
			if ((maxsendquota - STunables.MIN_SENDABLE_BYTES) > 0)// STunables.MIN_SENDABLE_BYTES)
			{
				maxsendquota -= 16; //STunables.MIN_SENDABLE_BYTES / 128;
				sendquota.addAndGet(-(16));
			}
//				System.out.println("RS: " + maxsendquota);
		}
//			System.out.println(maxsendquota);
	}
}
