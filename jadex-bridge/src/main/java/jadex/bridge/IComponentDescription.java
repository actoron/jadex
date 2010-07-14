package jadex.bridge;


/**
 *  A description of a component, i.e. information
 *  about the execution state, component type, etc.
 */
public interface IComponentDescription
{
	//-------- constants --------
	
	// todo: remove? needed for JADE
	/** Predefined value "initiated" for slot state. */
	public static String  STATE_INITIATED  = "initiated";
	/** Predefined value "transit" for slot state. */
	public static String  STATE_TRANSIT  = "transit";	
	
	/** Predefined value "active" for slot state. */
	public static String  STATE_ACTIVE  = "active";
	/** Predefined value "suspended" for slot state. */
	public static String  STATE_SUSPENDED  = "suspended";
	/** Predefined value "waiting" for slot state. */
	public static String  STATE_WAITING  = "waiting";
//	/** Predefined value "terminating" for slot state. */
//	public static String  STATE_TERMINATING  = "terminating";
	/** Predefined value "terminated" for slot state. */
	public static String  STATE_TERMINATED  = "terminated";
	
	//-------- methods --------

	/**
	 *  Get the execution state of the component.
	 *  @return The state.
	 */
	public String getState();

	/**
	 *  Get the identifier of the component.
	 *  @return The component identifier.
	 */
	public IComponentIdentifier getName();

	/**
	 *  Get the identifier of the parent component (if any).
	 *  @return The parent component identifier.
	 */
	public IComponentIdentifier getParent();

	/**
	 *  Get the ownership string of the component.
	 *  @return The ownership string.
	 */
	public String getOwnership();

	/**
	 *  Get the component type.
	 *  @return The component type name (e.g. 'BDI Agent').
	 */
	public String getType();
	
	/**
	 *  Get the enabled breakpoints (if any).
	 *  @return The enabled breakpoints.
	 */
	public String[]	getBreakpoints();
	
	/**
	 *  Is the component a master.
	 *  When a master component is killed the parent component will be killed two.
	 *  @return True, if master component.
	 */
	public boolean isMaster();

	/**
	 *  Is the component a daemon.
	 *  When platform is in autoshutdown mode and the last non-daemon component is killed
	 *  the platform will also be shutdowned.
	 *  @return True, if daemon component.
	 */
	public boolean isDaemon();
}
