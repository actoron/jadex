package jadex.bridge;


/**
 * 
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
	 *  Get the state of this AMSAgentDescription.
	 * @return state
	 */
	public String getState();

	/**
	 *  Set the state of this AMSAgentDescription.
	 * @param state the value to be set
	 */
//	public void setState(String state);

	/**
	 *  Get the agentidentifier of this AMSAgentDescription.
	 * @return agentidentifier
	 */
	public IComponentIdentifier getName();

	/**
	 *  Set the agentidentifier of this AMSAgentDescription.
	 * @param name the value to be set
	 */
//	public void setName(IFIPAAgentIdentifier name);

	/**
	 *  Get the ownership of this AMSAgentDescription.
	 * @return ownership
	 */
	public String getOwnership();

	/**
	 *  Set the ownership of this AMSAgentDescription.
	 * @param ownership the value to be set
	 */
//	public void setOwnership(String ownership);
}
