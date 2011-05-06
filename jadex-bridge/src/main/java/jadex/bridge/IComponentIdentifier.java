package jadex.bridge;

/**
 *  Interface for component identifiers.
 */
public interface IComponentIdentifier
{
	public static final IComponentIdentifier[] EMPTY_COMPONENTIDENTIFIERS = new IComponentIdentifier[0]; 
	
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
