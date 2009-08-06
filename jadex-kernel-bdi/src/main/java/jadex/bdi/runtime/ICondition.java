package jadex.bdi.runtime;


/**
 *  Common interface for all conditions
 */
public interface ICondition	extends IExpression
{
	//-------- constants --------

	/** Do not trace this condition (the default). * /
	public static final String	TRACE_NEVER	= "never";
	
	/** Always trace this condition (generate event whenever satisfied). * /
	public static final String	TRACE_ALWAYS	= "always";
	
	/** Trace this condition once (until satisfied). * /
	public static final String	TRACE_ONCE	= "once";*/

	//-------- attribute accessors --------
	
	/**
	 *  Get the trace mode.
	 */
//	public String getTraceMode();

	/**
	 *  Set the trace mode without generating events immediately.
	 *  @param trace	The new trace mode.
	 */
//	public void setTraceMode(String trace);

	/**
	 *  Trace this condition once, until it is triggerd.
	 *  If the condition is now true, an event is immediately generated.
	 */
//	public void traceOnce();

	/**
	 *  Trace this condition always, generating an event whenever
	 *  it evaluates to true. If the condition is now true, an event
	 *  is immediately generated.
	 */
//	public void traceAlways();

	/**
	 *  Get the filter to wait for the condition.
	 *  @return The filter.
	 */
//	public IFilter getFilter();
	
	//-------- listeners --------
	
	/**
	 *  Add a condition listener.
	 *  @param listener The condition listener.
	 *  @param async True, if the notification should be done on a separate thread.
	 */
	public void addConditionListener(IConditionListener listener, boolean async);
	
	/**
	 *  Remove a condition listener.
	 *  @param listener The condition listener.
	 */
	public void removeConditionListener(IConditionListener listener);
}