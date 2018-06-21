package jadex.bdibpmn.handler;

import jadex.bpmn.runtime.handler.ICancelable;
import jadex.commons.future.IFuture;

/**
 *  Dummy cancelable that just stores the type of the event.
 */
public class DummyCancelable implements ICancelable
{
	protected String type;
	
	/**
	 *  Create a new cancelable. 
	 */
	public DummyCancelable(String type)
	{
		this.type = type;
	}

	/**
	 *  Cancel the execution.
	 */
	public IFuture<Void> cancel()
	{
		return IFuture.DONE;
	}

	/**
	 *  Get the type.
	 *  return The type.
	 */
	public String getType()
	{
		return type;
	}
}
