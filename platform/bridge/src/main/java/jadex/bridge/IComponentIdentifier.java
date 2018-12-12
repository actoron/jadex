package jadex.bridge;


/**
 *  Interface for component identifiers.
 */
public interface IComponentIdentifier
{
	//-------- constants --------
	
	/** The constant to fetch the component id out of the results of a component. */
	public static final String RESULTCID = "__internal__component__identifier";
	
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
	 *  Get the parent identifier.
	 *  @return The parent identifier (if any).
	 */
	public IComponentIdentifier getParent();
	
	/**
	 *  Get the root identifier.
	 *  @return The root identifier.
	 */
	public IComponentIdentifier getRoot();
	
	/**
	 *  Get the dot name.
	 *  @return The dot name.
	 */
	public String getDotName();
	
//	/**
//	 *  Get the application name. Equals the local component name in case it is a child of the platform.
//	 *  broadcast@awa.plat1 -> awa
//	 *  @return The application name.
//	 */
//	public String getApplicationName();
}
