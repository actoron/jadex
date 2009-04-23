package jadex.adapter.base.envsupport.observer.perspective;

import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;
import jadex.adapter.base.envsupport.observer.gui.ObserverCenter;

import java.awt.Component;
import java.util.Set;

/**
 * A perspective responsible for displaying information gathered using a view.
 */
public interface IPerspective
{
	/**
	 * Returns the name of the perspective
	 * @return name of the perspective
	 */
	public String getName();
	
	/**
	 * Sets the name of the perspective
	 * @param name name of the perspective
	 */
	public void setName(String name);
	
	/** Returns the currently selected object.
	 * 
	 *  @return currently selected object
	 */
	public Object getSelectedObject();
	
	/**
	 * Sets the selected object.
	 * 
	 *  @param obj selected object
	 */
	public void setSelectedObject(Object obj);
	
	/**
	 * Sets the ObserverCenter.
	 * @param obscenter the ObserverCenter
	 */
	public void setObserverCenter(ObserverCenter obscenter);
	
	/**
	 * Adds a new visual object.
	 * @param id identifier of the object
	 * @param visual the visual object
	 */
	public void addVisual(Object id, Object visual);
	
	/**
	 * Removes a new visual object.
	 * @param id identifier of the object
	 */
	public void removeVisual(Object id);
	
	/**
	 * Gets the view component of the perspective.
	 * @return the view component
	 */
	public Component getView();
	
	/**
	 * Refreshes the perspective.
	 */
	public void refresh();
}
