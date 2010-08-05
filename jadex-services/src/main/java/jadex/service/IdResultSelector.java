package jadex.service;


/**
 *  Select service by id.
 */
public class IdResultSelector extends BasicResultSelector
{
	//-------- attributes --------
	
	/** The id. */
	protected Object sid;
	
	//-------- constructors --------
	
	/**
	 *  Create a id result listener.
	 */
	public IdResultSelector()
	{
	}
	
	/**
	 *  Create a id result listener.
	 */
	public IdResultSelector(Object sid)
	{
		this(sid, true);
	}
	
	/**
	 *  Create a id result listener.
	 */
	public IdResultSelector(Object sid, boolean oneresult)
	{
		this(sid, oneresult, true);
	}
	
	/**
	 *  Create a id result listener.
	 */
	public IdResultSelector(Object sid, boolean oneresult, boolean onlylocal)
	{
		super(new ServiceIdFilter(sid), oneresult, onlylocal);
	}
	
	//-------- methods --------
	
	/**
	 *  Get the service identifier.
	 *  @return the service identifier.
	 */
	public Object getServiceIdentifier()
	{
		return sid;
	}

	/**
	 *  Set the service identifier.
	 *  @param sid The service identifier to set.
	 */
	public void setServiceIdentifier(Object sid)
	{
		this.sid = sid;
	}
}
