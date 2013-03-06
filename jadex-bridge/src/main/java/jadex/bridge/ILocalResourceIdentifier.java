package jadex.bridge;

import java.net.URL;

/**
 *  Interface for resource identification.
 *  Localized resources are identified by their platform and local resource url.
 */
public interface ILocalResourceIdentifier
{
	/**
	 *  Get the platform identifier belonging to the resource identifier.
	 *  @return The component identifier of the platform. 
	 */
	public IComponentIdentifier	getComponentIdentifier();

	/**
	 *  Get the host identifier.
	 *  @return The host identifier.
	 */
	public String getHostIdentifier();
	
	/**
	 *  Get the url.
	 *  @return The resource url.
	 */
	public URL	getUrl();
}
