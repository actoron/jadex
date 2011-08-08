package jadex.bridge;

/**
 *  Thrown when component creation failed.
 */
public class ComponentCreationException	extends RuntimeException
{
	//-------- constants --------
	
	/** Constant that indicates that the component already exists. */
	public final static String REASON_COMPONENT_EXISTS = "component_exists";

	/** Constant that indicates that component has a wrong/errorneous id. */
	public final static String REASON_WRONG_ID = "wrong_id";

	/** Constant that indicates that a model has occurred. */
	public final static String REASON_MODEL_ERROR = "model_error";
	
	/** Constant that indicates that no component factory has been found. */
	public final static String REASON_NO_COMPONENT_FACTORY = "no_component_factory";
	
	//-------- attributes --------
	
	/** The reason. */
	protected String reason;
	
	/** The optional info. */
	protected Object info;
	
	//-------- constructors --------
	
	/**
	 *	Create an component termination exception.  
	 */
	public ComponentCreationException(String message, String reason)
	{
		this(message, reason, null);
	}
	
	/**
	 *	Create an component termination exception.  
	 */
	public ComponentCreationException(String message, String reason, Object info)
	{
		super(message);
		this.reason = reason;
		this.info = info;
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
	
	/**
	 *  Set the reason.
	 *  @param reason The reason to set.
	 */
	public void setReason(String reason)
	{
		this.reason = reason;
	}

	/**
	 *  Get the info.
	 *  @return the info.
	 */
	public Object getInfo()
	{
		return info;
	}

	/**
	 *  Set the info.
	 *  @param info The info to set.
	 */
	public void setInfo(Object info)
	{
		this.info = info;
	}
}
