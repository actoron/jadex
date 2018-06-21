package jadex.extension.envsupport.observer.perspective;

import java.awt.Component;

import jadex.commons.meta.ITypedPropertyObject;
import jadex.extension.envsupport.observer.gui.IObserverCenter;

/**
 * A perspective responsible for displaying information gathered using a view.
 */
public interface IPerspective extends ITypedPropertyObject
{
	/**
	 *  Returns the name of the perspective
	 *  @return name of the perspective
	 */
	public String getName();
	
	/**
	 *  Sets the name of the perspective
	 *  @param name name of the perspective
	 */
	public void setName(String name);
	
	/** 
	 *  Returns the currently selected object.
	 *  @return currently selected object
	 */
	public Object getSelectedObject();
	
	/**
	 *  Sets the selected object.
	 *  @param obj selected object
	 */
	public void setSelectedObject(Object obj);
	
	/**
	 *  Sets the ObserverCenter.
	 *  @param obscenter the ObserverCenter
	 */
	public void setObserverCenter(IObserverCenter obscenter);
	
	/**
	 *  Get the ObserverCenter.
	 *  @return The observer center.
	 */
	public IObserverCenter getObserverCenter();
	
	/**
	 *  Adds a new visual object.
	 *  @param id identifier of the object
	 *  @param visual the visual object
	 */
	public void addVisual(Object id, Object visual);
	
	/**
	 *  Removes a new visual object.
	 *  @param id identifier of the object
	 */
	public void removeVisual(Object id);
	
	/**
	 *  Gets the view component of the perspective.
	 *  @return the view component
	 */
	public Component getView();
	
	/**
	 *  Refreshes the perspective.
	 */
	public void refresh();
	
	/**
	 * Gets whether to try to use OpenGL.
	 * @return true, if attempt should be made to use OpenGL
	 */
	public boolean getOpenGl();
	
	/**
	 * Resets position of the perspective.
	 */
	public void resetZoomAndPosition();
	
	/**
	 * Resets position and flushes render info
	 */
	public void reset();
	
	/**
	 *  Sets whether to try to use OpenGL.
	 *  @param opengl true, if attempt should be made to use OpenGL
	 */
	public boolean setOpenGl(boolean opengl);
}
