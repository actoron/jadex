package jadex.adapter.base.envsupport.environment.view;

import java.util.List;

import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.ISpaceObject;

/**
 * View used by an observer to display part of the environment
 */
public interface IView
{
	// View Types //
	public static final String SIMPLE_VIEW_2D = "Simple 2D View";
	
	/**
	 * 
	 */
	public void setSpace(IEnvironmentSpace space);
	
	/**
	 * Returns the type of the view.
	 * @return type of the view
	 */
	public String getType();
	
	/**
	 * Returns a list of objects in this view
	 * @return list of objects
	 */
	public Object[] getObjects();
	
	/**
	 *  Updates the view.
	 *  
	 *  @param space the space of the view
	 */
	public void update(IEnvironmentSpace space);
}
