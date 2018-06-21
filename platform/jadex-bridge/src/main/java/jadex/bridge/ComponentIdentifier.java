package jadex.bridge;

/**
 *  Component identifier with transport addresses.
 */
// Not called transport identifier to avoid incompatibilities with older releases
public class ComponentIdentifier extends BasicComponentIdentifier implements ITransportComponentIdentifier
{
	//-------- addresses --------
	
	/** Attribute for slot addresses. */
	protected String[]	addresses;

	//-------- constructors --------

	/**
	 *  Create a new component identifier.
	 *  Bean constructor
	 */
	public ComponentIdentifier()
	{
	}

	/**
	 *  Create a new component identifier with a given global name.
	 *  @param name A global name (e.g. "cms@lars").
	 */
	public ComponentIdentifier(String name)
	{
		super(name);
	}

	/**
	 *  Create a new component identifier with a global name and given addresses.
	 *  @param name A global name (e.g. "cms@lars").
	 *  @param addresses A list of transport addresses.
	 */
	public ComponentIdentifier(String name, String[] addresses)
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
	public ComponentIdentifier(String name, ITransportComponentIdentifier parent)
	{
		this(name, parent, parent.getAddresses());
	}
	
	/**
	 *  Create component identifier.
	 *  @param name The local name.
	 *  @param parent The parent.
	 *  @param addresses The addresses.
	 */
	public ComponentIdentifier(String name, ITransportComponentIdentifier parent, String[] addresses)
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
			ret = new ComponentIdentifier(paname+"@"+pfname, getAddresses());
		}
		else if(at!=-1)
		{
			String paname = name.substring(at+1);
			ret = new ComponentIdentifier(paname, getAddresses());
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
		return new ComponentIdentifier(getPlatformName(), getAddresses());
	}
	
	/**
	 * Clone this component identifier.
	 * Does a deep copy.
	 */
	public Object clone()
	{
		ComponentIdentifier clone = new ComponentIdentifier(getName(), getAddresses());
		return clone;
	}
	
	/**
	 *  Convenience method.
	 *  
	 *  Static helper method to convert the identifier to a transport identifier.
	 */
//	public static IFuture<ITransportComponentIdentifier> getTransportIdentifier(final IExternalAccess exta)
//	{
//		final Future<ITransportComponentIdentifier> ret = new Future<ITransportComponentIdentifier>();
//		SServiceProvider.getService(exta, ITransportAddressService.class, RequiredServiceInfo.SCOPE_PLATFORM)
//			.addResultListener(new ExceptionDelegationResultListener<ITransportAddressService, ITransportComponentIdentifier>(ret)
//		{
//			public void customResultAvailable(ITransportAddressService tas)
//			{
//				tas.getTransportComponentIdentifier(exta.getComponentIdentifier()).addResultListener(new DelegationResultListener<ITransportComponentIdentifier>(ret));
//			}
//		});
//		return ret;
//	}
}
