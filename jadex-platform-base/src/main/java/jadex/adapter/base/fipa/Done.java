package jadex.adapter.base.fipa;


/**
 *  Predicate indicating a completed action.
 */
public class Done
{
	//-------- attributes --------
	
	/** The completed action. */
	protected IAgentAction	action;
	
	//-------- constructors --------
	
	/**
	 *  Create a done action.
	 */
	public Done()
	{
	}

	/**
	 *  Create a done action.
	 */
	public Done(IAgentAction action)
	{
		this.action	= action;
	}

	//-------- accessors --------

	/**
	 *  Get the action.
	 */
	public IAgentAction getAction()
	{
		return action;
	}

	/**
	 *  Set the action.
	 */
	public void setAction(IAgentAction action)
	{
		this.action = action;
	}
}
