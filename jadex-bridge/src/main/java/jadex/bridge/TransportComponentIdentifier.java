package jadex.bridge;

/**
 *  Component identifier with transport addresses.
 */
public class TransportComponentIdentifier extends ComponentIdentifier implements ITransportComponentIdentifier
{
	//-------- addresses --------
	
	/** Attribute for slot addresses. */
	protected String[]	addresses;

	//-------- constructors --------

	/**
	 *  Create a new component identifier.
	 *  Bean constructor
	 */
	public TransportComponentIdentifier()
	{
	}

	/**
	 *  Create a new component identifier with a given global name.
	 *  @param name A global name (e.g. "cms@lars").
	 */
	public TransportComponentIdentifier(String name)
	{
		super(name);
	}

	/**
	 *  Create a new component identifier with a global name and given addresses.
	 *  @param name A global name (e.g. "cms@lars").
	 *  @param addresses A list of transport addresses.
	 */
	public TransportComponentIdentifier(String name, String[] addresses)
	{
		super(name);
		this.addresses	= addresses;
	}
	
	/**
	 *  Create component identifier.
	 *  @param name The local name.
	 *  @param parent The parent.
	 *  @param addresses The addresses.
	 */
	public TransportComponentIdentifier(String name, ITransportComponentIdentifier parent)
	{
		this(name, parent, parent.getAddresses());
	}
	
	/**
	 *  Create component identifier.
	 *  @param name The local name.
	 *  @param parent The parent.
	 *  @param addresses The addresses.
	 */
	public TransportComponentIdentifier(String name, ITransportComponentIdentifier parent, String[] addresses)
	{
		this(name+"@"+parent.getName().replace('@', '.'), addresses);
	}
	
	/**
	 *  Get the addresses of this ComponentIdentifier.
	 * @return addresses
	 */
	public String[] getAddresses()
	{
		return addresses;
	}

	/**
	 *  Set the addresses of this ComponentIdentifier.
	 * @param addresses the value to be set
	 */
	public void setAddresses(String[] addresses)
	{
		this.addresses	= addresses;
	}
	
	/**
	 *  Get the parent identifier.
	 *  @return The parent identifier (if any).
	 */
	public IComponentIdentifier getParent()
	{
		IComponentIdentifier ret = null;
		int	at = name.indexOf("@");
		int idx = name.indexOf(".", at);
		if(idx!=-1)
		{
			String paname = name.substring(at+1, idx);
			String pfname = name.substring(idx+1);
			ret = new TransportComponentIdentifier(paname+"@"+pfname, getAddresses());
		}
		else if(at!=-1)
		{
			String paname = name.substring(at+1);
			ret = new TransportComponentIdentifier(paname, getAddresses());
		}
		// else at root.
		return ret;
	}
	
	/**
	 *  Get the root identifier.
	 *  @return The root identifier.
	 */
	public IComponentIdentifier getRoot()
	{
		return new TransportComponentIdentifier(getPlatformName(), getAddresses());
	}
	
	/**
	 * Clone this component identifier.
	 * Does a deep copy.
	 */
	public Object clone()
	{
		TransportComponentIdentifier clone = new TransportComponentIdentifier(getName(), getAddresses());
		return clone;
	}
}
