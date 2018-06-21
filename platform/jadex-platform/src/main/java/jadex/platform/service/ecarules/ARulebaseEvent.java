package jadex.platform.service.ecarules;

import java.util.concurrent.atomic.AtomicInteger;

import jadex.bridge.service.types.ecarules.IRulebaseEvent;
import jadex.commons.future.IBackwardCommandFuture;

/**
 * 
 */
public abstract class ARulebaseEvent implements IRulebaseEvent
{
	/** The counter. */
	private static AtomicInteger cnt = new AtomicInteger();
	
	/** The id. */
	protected int id;
	
	/** The call id. */
	protected int callid;
	
	/**
	 *  Create a new rule event.
	 */
	public ARulebaseEvent()
	{
		this.id = cnt.incrementAndGet();
	}
	
	/**
	 *  Create a new rule event.
	 */
	public ARulebaseEvent(int callid)
	{
		this.id = cnt.incrementAndGet();
		this.callid = callid;
	}
	
	/**
	 * 
	 */
	public void setFinished(IBackwardCommandFuture fut)
	{
		fut.sendBackwardCommand(new FinishedEvent(callid, id));
	}

	/**
	 *  Get the id.
	 *  return The id.
	 */
	public int getId()
	{
		return id;
	}

	/**
	 *  Set the id. 
	 *  @param id The id to set.
	 */
	public void setId(int id)
	{
		this.id = id;
	}
	
	/**
	 *  Get the callid.
	 *  @return The callid.
	 */
	public int getCallId()
	{
		return callid;
	}

	/**
	 *  Set the callid.
	 *  @param callid The callid to set.
	 */
	public void setCallId(int callid)
	{
		this.callid = callid;
	}

	/**
	 * 
	 */
	public ARulebaseEvent createCopy()
	{
		return null;
	}
}
