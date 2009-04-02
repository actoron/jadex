package jadex.adapter.base.envsupport.environment.view;

import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;

/**
 * View used by an observer to display part of the environment
 */
public interface IView
{
	/**
	 * Returns the name of the view.
	 * @return name of the view
	 */
	public String getName();
	
	/**
	 *  Updates the view.
	 *  
	 *  @param space the space of the view
	 */
	public void update(IEnvironmentSpace space);
}
