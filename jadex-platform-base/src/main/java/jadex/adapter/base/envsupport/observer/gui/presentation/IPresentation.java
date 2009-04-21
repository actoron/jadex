package jadex.adapter.base.envsupport.observer.gui.presentation;

import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;
import jadex.adapter.base.envsupport.observer.gui.ObserverCenter;

import java.awt.Component;
import java.util.Set;

/**
 * A presentation responsible for displaying information gathered using a view.
 */
public interface IPresentation
{
	/**
	 * Returns the name of the presentation
	 * @return name of the presentation
	 */
	public String getName();
	
	/**
	 * Returns supported theme types as Class objects.
	 * 
	 * @return supported theme types
	 */
	public Set getSupportedThemeTypes();
	
	/**
	 * Sets the current theme.
	 * @param theme the new theme 
	 */
	public void setTheme(Object theme);
	
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
	 * Gets the view of the presentation.
	 * @return the view
	 */
	public Component getView();
	
	/**
	 * Refreshes the presentation.
	 */
	public void refresh();
}
