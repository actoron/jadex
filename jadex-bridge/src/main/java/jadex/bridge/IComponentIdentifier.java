package jadex.bridge;

/**
 *  Interface for component identifiers.
 */
public interface IComponentIdentifier
{
	//-------- constants --------
	
	/** The currently executing component (if any). */
	public static final ThreadLocal<IComponentIdentifier>	LOCAL	= new ThreadLocal<IComponentIdentifier>();
	
	/** The caller of a currently executed service call (if any). */
//	public static final ThreadLocal<IComponentIdentifier>	CALLER	= new ThreadLocal<IComponentIdentifier>();
	
	/** Return value for empty arrays. */
	public static final IComponentIdentifier[] EMPTY_COMPONENTIDENTIFIERS = new IComponentIdentifier[0];
	
	//-------- methods --------
	
	/**
	 *  Get the component name.
	 *  @return The name of an component.
	 */
	public String getName();
	
	/**
	 *  Get the local component name.
	 *  @return The local name of an component.
	 */
	public String getLocalName();
	
	/**
	 *  Get the platform name.
	 *  @return The platform name.
	 */
	public String getPlatformName();
	
	/**
	 *  Get the platform name without the suffix for name uniqueness.
	 *  @return The platform name without suffix.
	 */
	public String getPlatformPrefix();
	
	/**
	 *  Get the addresses.
	 *  @return The addresses.
	 */
	public String[] getAddresses();
	
	/**
	 *  Get the parent identifier.
	 *  @return The parent identifier (if any).
	 */
	public IComponentIdentifier getParent();
	
	/**
	 *  Get the root identifier.
	 *  @return The root identifier.
	 */
	public IComponentIdentifier getRoot();
}
