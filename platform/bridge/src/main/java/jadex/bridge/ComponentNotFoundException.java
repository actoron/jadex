package jadex.bridge;

/**
 *  Thrown when a component was not found.
 */
public class ComponentNotFoundException	extends RuntimeException
{
	//-------- attributes --------
	
	/** The component identifier. */
	protected IComponentIdentifier cid;
	
	//-------- constructors --------
	
	/**
	 *  Simple constructor for deserialization.
	 */
	public ComponentNotFoundException(String message)
	{
		super(message);
	}
	
	/**
	 *	Create an component termination exception.  
	 */
	public ComponentNotFoundException(IComponentIdentifier cid)
	{
		super(cid.getName());
		this.cid = cid;
	}

	/**
	 *	Create an component termination exception.  
	 */
	public ComponentNotFoundException(IComponentIdentifier cid, String message)
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
	
	public void printStackTrace()
	{
		Thread.dumpStack();
		super.printStackTrace();
	}
}
