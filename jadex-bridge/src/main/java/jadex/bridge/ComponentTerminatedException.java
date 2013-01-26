package jadex.bridge;


/**
 *  Thrown when operations are invoked after an component has been terminated.
 */
public class ComponentTerminatedException	extends RuntimeException
{
	//-------- attributes --------
	
	/** The component identifier. */
	protected IComponentIdentifier cid;
	
	//-------- constructors --------
	
	/**
	 *  Simple constructor for deserialization.
	 */
	public ComponentTerminatedException(String message)
	{
		super(message);
	}
	
	/**
	 *	Create an component termination exception.  
	 */
	public ComponentTerminatedException(IComponentIdentifier cid)
	{
		super(cid.getName());
		this.cid = cid;
	}

	/**
	 *	Create an component termination exception.  
	 */
	public ComponentTerminatedException(IComponentIdentifier cid, String message)
	{
		super(cid.getName()+": "+message);
		this.cid = cid;
	}

	//-------- methods --------
	
	/**
	 *  Get the component identifier.
	 *  @return The component identifier.
	 */
	public IComponentIdentifier getComponentIdentifier()
	{
		return cid;
	}
	
	/**
	 *  Get the component identifier.
	 */
	public void setComponentIdentifier(IComponentIdentifier cid)
	{
		this.cid	= cid;
	}
}
