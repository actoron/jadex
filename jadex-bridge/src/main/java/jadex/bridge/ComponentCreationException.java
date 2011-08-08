package jadex.bridge;

/**
 *  Thrown when component creation failed.
 */
public class ComponentCreationException	extends RuntimeException
{
	//-------- constants --------
	
	/** . */
	public final static String REASON_COMPONENT_EXISTS = "component_exists";

	/** . */
	public final static String REASON_WRONG_ID = "wrong_id";

	/** . */
	public final static String REASON_MODEL_ERROR = "model_error";
	
	/** . */
	public final static String REASON_NO_COMPONENT_FACTORY = "no_component_factory";
	
	//-------- attributes --------
	
	/** The reason. */
	protected String reason;
	
	//-------- constructors --------
	
	/**
	 *	Create an component termination exception.  
	 */
	public ComponentCreationException(String message, String reason)
	{
		super(message);
		this.reason = reason;
	}

	//-------- methods --------
	
	/**
	 *  Get the reason.
	 *  @return The reason.
	 */
	public String getReason()
	{
		return reason;
	}
}
