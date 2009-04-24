package jadex.adapter.base.envsupport.environment;


import jadex.commons.IPropertyObject;

import java.util.Map;


/**
 * An action in the environment
 */
public interface ISpaceAction extends IPropertyObject
{
	/**
	 *  Performs a space action.
	 *  @param parameters parameters for the action
	 *  @param space the environment space
	 *  @return action return value
	 */
	public Object perform(Map parameters, IEnvironmentSpace space);
}
