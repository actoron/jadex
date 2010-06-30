package jadex.bdi.runtime;

import jadex.commons.IFuture;

/**
 *  A currently instantiated plan of the agent (=intention).
 */
public interface IEAPlan extends IEAParameterElement
{
	//-------- methods --------
	
	/**
	 *  Get the lifecycle state of the plan (e.g. body or aborted).
	 *  @return The lifecycle state.
	 */
	public IFuture getLifecycleState();

	/**
	 *  Get the waitqueue.
	 *  @return The waitqueue.
	 */
	public IFuture getWaitqueue();

	/**
	 *  Get the body.
	 *  @return The body.
	 */
	public IFuture getBody();
	
	/**
	 *  Get the reason (i.e. initial event).
	 *  @return The reason.
	 */
	public IFuture getReason();
	
	/**
	 *  Abort a running plan. 
	 */
	public IFuture abortPlan();
	
	//-------- listeners --------
	
	/**
	 *  Add a goal listener.
	 *  @param listener The goal listener.
	 */
	public IFuture addPlanListener(IPlanListener listener);
	
	/**
	 *  Remove a goal listener.
	 *  @param listener The goal listener.
	 */
	public IFuture removePlanListener(IPlanListener listener);
}
