package jadex.adapter.base.envsupport.dataview;

import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;

import java.util.Map;

/**
 * View used by an observer to display part of the environment
 */
public interface IDataView
{
	// View Types //
	public static final String SIMPLE_VIEW_2D = "Simple 2D View";
	
	/**
	 *  Initialize the view.
	 */
	// todo: other form of initialization?
	public void init(IEnvironmentSpace space, Map properties);
	
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
