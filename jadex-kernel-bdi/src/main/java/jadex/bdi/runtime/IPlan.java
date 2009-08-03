package jadex.bdi.runtime;


/**
 *  A currently instantiated plan of the agent (=intention).
 */
public interface IPlan	extends IParameterElement
{
	// Todo: add some methods (e.g. isAlive)?

	/**
	 *  Get the waitqueue.
	 *  @return The waitqueue.
	 */
	public IWaitqueue getWaitqueue();

	/**
	 *  Get the body.
	 *  @return The body.
	 */
	public Object getBody();
	
	/**
	 *  Get the reason (i.e. initial event).
	 *  @return The reason.
	 */
	public Object getReason();
	
	//-------- listeners --------
	
	/**
	 *  Add a goal listener.
	 *  @param listener The goal listener.
	 */
	public void addPlanListener(IPlanListener listener);
	
	/**
	 *  Remove a goal listener.
	 *  @param listener The goal listener.
	 */
	public void removePlanListener(IPlanListener listener);
}