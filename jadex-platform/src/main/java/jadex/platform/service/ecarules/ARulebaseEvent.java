package jadex.platform.service.ecarules;

import jadex.bridge.service.types.ecarules.IRulebaseEvent;
import jadex.commons.future.ICommandFuture;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 */
public abstract class ARulebaseEvent implements IRulebaseEvent
{
	/** The counter. */
	private static AtomicInteger cnt = new AtomicInteger();
	
	/** The id. */
	protected int id;
	
	/**
	 *  Create a new rule event.
	 */
	public ARulebaseEvent()
	{
		this.id = cnt.incrementAndGet();
	}
	
	/**
	 * 
	 */
	public void setFinished(ICommandFuture fut)
	{
		fut.sendCommand(new FinishedEvent(id));
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
	 * 
	 */
	public abstract ARulebaseEvent createCopy();
}
