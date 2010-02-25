package jadex.bridge;

/**
 *  Interface for component identifiers.
 */
public interface IComponentIdentifier
{
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
}
