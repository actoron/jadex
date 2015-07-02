package jadex.bridge;


/**
 *  Thrown when operations are invoked after an component has been persisted.
 */
public class ComponentPersistedException	extends RuntimeException
{
	//-------- attributes --------
	
	/** The component identifier. */
	protected IComponentIdentifier cid;
	
	//-------- constructors --------
	
	/**
	 *  Simple constructor for deserialization.
	 */
	public ComponentPersistedException(String message)
	{
		super(message);
	}
	
	/**
	 *	Create an component persited exception.  
	 */
	public ComponentPersistedException(IComponentIdentifier cid)
	{
		super(cid.getName());
		this.cid = cid;
	}

	/**
	 *	Create an component persisted exception.  
	 */
	public ComponentPersistedException(IComponentIdentifier cid, String message)
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
	 *  Set the component identifier.
	 */
	public void setComponentIdentifier(IComponentIdentifier cid)
	{
		this.cid	= cid;
	}
	
//	public void printStackTrace()
//	{
//		Thread.dumpStack();
//		super.printStackTrace();
//	}
}
