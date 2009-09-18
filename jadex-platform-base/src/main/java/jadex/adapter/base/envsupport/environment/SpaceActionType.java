package jadex.adapter.base.envsupport.environment;

import java.util.List;

/**
 * 
 */
public class SpaceActionType
{
	/** The space action instance. */
	protected ISpaceAction action;
	
	/** The parameter specifications. */
	protected List parameters;
	
	/**
	 * 
	 */
	public SpaceActionType(ISpaceAction action, List parameters)
	{
		this.action = action;
		this.parameters = parameters;
	}

	/**
	 *  Get the action.
	 *  @return The action.
	 */
	public ISpaceAction getAction()
	{
		return this.action;
	}

	/**
	 *  Get the parameters.
	 *  @return The parameters.
	 */
	public List getParameters()
	{
		return this.parameters;
	}
	
}
