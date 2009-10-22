package jadex.wfms.service.admin;

import jadex.service.IServiceContainer;

import javax.swing.JComponent;

/**
 *  Control center interface providing useful methods
 *  that may be called from plugins. 
 */
public interface IControlCenter
{
	/**
	 *  Get the service container.
	 *  @return The service container.
	 */
	public IServiceContainer getServiceContainer();

	/**
	 *  Set a text to be displayed in the status bar.
	 *  The text will be removed automatically after
	 *  some delay (or replaced by some other text).
	 */
	public void setStatusText(String text);
	
	/**
	 *  Add a component to the status bar.
	 *  @param id	An id for later reference.
	 *  @param comp	An id for later reference.
	 */
	public void	addStatusComponent(Object id, JComponent comp);

	/**
	 *  Remove a previously added component from the status bar.
	 *  @param id	The id used for adding the component.
	 */
	public void	removeStatusComponent(Object id);
	
	/**
	 *  Show the console.
	 *  @param show True, if console should be shown.
	 */
	public void showConsole(boolean show);
	
	/**
	 *  Test if console is shown.
	 *  @return True, if shown.
	 */
	public boolean isConsoleShown();
	
	/**
	 *  Set the console enable state.
	 *  @param enabled The enabled state.
	 */
	public void setConsoleEnabled(boolean enabled);
	
	/**
	 *  Test if the console is enabled.
	 *  @return True, if enabled.
	 */
	public boolean isConsoleEnabled();
}
