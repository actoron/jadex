package jadex.adapter.base.envsupport.environment;


import java.util.Map;


/**
 * An action in the environment
 */
public interface ISpaceAction
{
	/**
	 *  Performs a space action.
	 *  @param parameters parameters for the action
	 *  @param space the environment space
	 *  @return action return value
	 */
	public Object perform(Map parameters, IEnvironmentSpace space);

	/**
	 * Returns the ID of the action.
	 * 
	 * @return ID of the action
	 */
	public Object getId();
}
