package jadex.bridge;


/**
 *  A description of a component, i.e. information
 *  about the execution state, component type, etc.
 */
public interface IComponentDescription
{
	//-------- constants --------
	
//	/** Predefined value "initiated" for slot state. */
//	public static String  STATE_INITIATED  = "initiated";
	/** Predefined value "active" for slot state. */
	public static String  STATE_ACTIVE  = "active";
	/** Predefined value "suspended" for slot state. */
	public static String  STATE_SUSPENDED  = "suspended";
	/** Predefined value "waiting" for slot state. */
	public static String  STATE_WAITING  = "waiting";
//	/** Predefined value "transit" for slot state. */
//	public static String  STATE_TRANSIT  = "transit";
	/** Predefined value "terminating" for slot state. */
	public static String  STATE_TERMINATING  = "terminating";
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
}
