package jadex.bridge.component;

/**
 *   Interface for message headers with meta information / link-level security.
 *
 */
public interface IMsgHeader
{
	/** Message header key for the sender. */
	public static final String SENDER = "sender";
	
	/** Message header key for the receiver. */
	public static final String RECEIVER = "receiver";
	
	/** Message header key for conversation IDs (optional). */
	public static final String CONVERSATION_ID = "convid";
	
	/**
	 *  Gets a property stored in the header.
	 *  
	 *  @param propertyname
	 * @return
	 */
	public Object getProperty(String propertyname);
	
	/**
	 *  Adds a header property to the header.
	 *  
	 *  @param propname The property name.
	 *  @param propval The property value.
	 */
	public void addProperty(String propname, Object propval);
}
