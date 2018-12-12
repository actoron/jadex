package jadex.extension.envsupport.observer.gui;

import java.util.List;

import jadex.commons.future.IFuture;
import jadex.extension.envsupport.dataview.IDataView;
import jadex.extension.envsupport.environment.AbstractEnvironmentSpace;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.observer.perspective.IPerspective;

public interface IObserverCenter
{
	/** Starts an observer center.
	 *  
	 *  @param title title of the observer window
	 *  @param space the space being observed
	 *  @param classloader the application class loader for loading resources (images etc.)
	 *  @param plugins custom plugins used in the observer
	 */
	public void startObserver(final String title, final IEnvironmentSpace space, ClassLoader classloader, boolean killonexit);
	public void loadPlugins(List plugins);
	
	/**
	 *  Dispose the observer center. 
	 */
	public void dispose();
	
	/**
	 * Adds a perspective.
	 * @param name name of the perspective
	 * @param perspective the perspective
	 */
	public IFuture<Void>	addPerspective(final String name, final IPerspective perspective);
	
	/**
	 * Returns the space.
	 * @return the space
	 */
	public AbstractEnvironmentSpace getSpace();
	
	/**
	 * Fires a selected object change event.
	 */
	public void fireSelectedObjectChange();
	
	/**
	 *  Get the class loader.
	 */
	public ClassLoader getClassLoader();
	
	/**
	 * Returns the selected dataview.
	 * 
	 *  @return the selected dataview
	 */
	public IDataView getSelectedDataView();
}
