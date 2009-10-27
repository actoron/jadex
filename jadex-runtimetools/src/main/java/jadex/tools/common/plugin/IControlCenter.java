package jadex.tools.common.plugin;

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
	
//	/**
//	 *  Get the external access interface.
//	 *  @return the external agent access.
//	 */
//	public IExternalAccess getAgent();
//
//	/**
//	 *  Listen for changes to the list of known agents.
//	 */
//	public void addAgentListListener(IAgentListListener listener);
//
//	/**
//	 *  Listen for incoming messages.
//	 */
//	public void addMessageListener(IMessageListener listener);
//
//	/**
//	 *  Create a new agent on the platform.
//	 *  Any errors will be displayed in a dialog to the user.
//	 */
//	public void createAgent(String type, String name, String configname, Map arguments);
//
//	/**
//	 *  Kill an agent on the platform.
//	 *  Any errors will be displayed in a dialog to the user.
//	 */
//	public void killAgent(IComponentIdentifier name);
//	
//	/**
//	 *  Suspend an agent on the platform.
//	 *  Any errors will be displayed in a dialog to the user.
//	 */
//	public void suspendAgent(IComponentIdentifier name);
//	
//	/**
//	 *  Resume an agent on the platform.
//	 *  Any errors will be displayed in a dialog to the user.
//	 */
//	public void resumeAgent(IComponentIdentifier name);
	
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
