package jadex.bridge.fipa;


/**
 *  Predicate indicating a completed action.
 */
public class Done
{
	//-------- attributes --------
	
	/** The completed action. */
	protected IComponentAction	action;
	
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
	public Done(IComponentAction action)
	{
		this.action	= action;
	}

	//-------- accessors --------

	/**
	 *  Get the action.
	 */
	public IComponentAction getAction()
	{
		return action;
	}

	/**
	 *  Set the action.
	 */
	public void setAction(IComponentAction action)
	{
		this.action = action;
	}
}
