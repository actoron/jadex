package jadex.rules.profiler;

/**
 *  A profiler stores execution information, such as
 *  time and memory consumption. The application has
 *  to call the start()/stop()   for information
 *  on specific items being collected.
 */
public interface IProfiler
{
	//-------- constants --------
	
	/** The dummy root type (not used for ordering). */
	public static final String	TYPE_ROOT	= "root";
	
	/** The rule type. */
	public static final String	TYPE_RULE	= "rule";
	
	/** The object-type type. */
	public static final String	TYPE_OBJECT	= "object";
	
	/** The object event type (see objectevent_xxx for possible items). */
	public static final String	TYPE_OBJECTEVENT	= "objectevent";
	
	/** The rete-node type. */
	public static final String	TYPE_NODE	= "node";
	
	/** The rete-node event type (see nodeevent_xxx for possible items). */
	public static final String	TYPE_NODEEVENT	= "nodeevent";
	
	/** The object added event item. */
	public static final String	OBJECTEVENT_ADDED	= "StateObjectAdded";

	/** The object removed event item. */
	public static final String	OBJECTEVENT_REMOVED	= "StateObjectRemoved";

	/** The object modified event item. */
	public static final String	OBJECTEVENT_MODIFIED	= "StateObjectModified";

	/** The node object added event item. */
	public static final String	NODEEVENT_OBJECTADDED	= "NodeObjectAdded";

	/** The node object removed event item. */
	public static final String	NODEEVENT_OBJECTREMOVED	= "NodeObjectRemoved";

	/** The node object modified event item. */
	public static final String	NODEEVENT_OBJECTMODIFIED	= "NodeObjectModified";

	/** The node tuple added event item. */
	public static final String	NODEEVENT_TUPLEADDED	= "NodeTupleAdded";

	/** The node tuple removed event item. */
	public static final String	NODEEVENT_TUPLEREMOVED	= "NodeTupleRemoved";

	/** The node tuple modified event item. */
	public static final String	NODEEVENT_TUPLEMODIFIED	= "NodeTupleModified";

	//-------- methods --------
	
	/**
	 *  Start profiling an item. Nesting (i.e. calling start() several times before
	 *  calling corresponding stops) is allowed.
	 *  @param type A constant representing the event or activity type being profiled (e.g. object added).
	 *  @param item The element corresponding to the activity (e.g. the object type).
	 */
	public void	start(String type, Object item);

	/**
	 *  Stop profiling the current item.
	 *  Calls to stop() have match the last unstopped call to start(), with respect to the supplied types and items.
	 *  @param type A constant representing the event or activity type being profiled (e.g. object added).
	 *  @param item The element corresponding to the activity (e.g. the object type).
	 */
	public void	stop(String type, Object item);
	
	/**
	 *  Get the current profiling infos from the given start index.
	 *  @param start	The start index (use 0 for all profiling infos).
	 */
	public ProfilingInfo[] getProfilingInfos(int start);
}
