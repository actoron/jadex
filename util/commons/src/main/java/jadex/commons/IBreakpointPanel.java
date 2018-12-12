package jadex.commons;


/**
 *  Common interface for breakpoint panels.
 */
// Required for mutual independence between runtime-tools and rules-tools.
// Todo: Hack!!! Move somewhere else!?
public interface IBreakpointPanel
{
	//-------- constants --------
	
	/** Event type for changed breakpoint selection (e.g. chosen for display). */
	public static final String	EVENT_TYPE_SELECTED	= "event-type-selected";
	
	//-------- methods --------
	
	/**
	 *  Add a listener to receive breakpoint selected events.
	 *  @param listener	The change listener.
	 */
	public void addBreakpointListener(IChangeListener listener);

	/**
	 *  Remove a listener from receiving breakpoint selected events.
	 *  @param listener	The change listener.
	 */
	public void removeBreakpointListener(IChangeListener listener);

	/**
	 *  Get the currently selected breakpoints.
	 *  Selected breakpoints should be displayed or highlighted (if possible).
	 *  @return	An array of selected breakpoints.
	 */
	public String[] getSelectedBreakpoints();
	
	/**
	 *  Set the currently selected breakpoints.
	 *  Update the selection in the breakpoint list.
	 *  Throws an event.
	 *  @param breakpoints	The currently selected breakpoints.
	 */
	public void setSelectedBreakpoints(String[] breakpoints);

	/**
	 *  Dispose the panel, when the gui is closed.
	 */
	public void dispose();
}
