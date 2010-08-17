package jadex.tools.common.plugin;

import jadex.bridge.IComponentIdentifier;
import jadex.service.IServiceProvider;

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
	public IServiceProvider	getServiceProvider();
	
	/**
	 *  Get the component id of the component executing the JCC.
	 *  @return The component id.
	 */
	public IComponentIdentifier	getComponentIdentifier();
	
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
	 *  Set the console height.
	 *  @param height The console height.
	 * /
	public void setConsoleHeight(int height);*/
	
	/**
	 *  Get the console height.
	 *  @return The console height.
	 * /
	public int getConsoleHeight();*/
	
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

	/**
	 *  Display an error dialog.
	 * 
	 *  @param errortitle The title to use for an error dialog (required).
	 *  @param errormessage An optional error message displayed before the exception.
	 *  @param exception The exception (if any).
	 */
	public void displayError(final String errortitle, String errormessage, Exception exception);
}
