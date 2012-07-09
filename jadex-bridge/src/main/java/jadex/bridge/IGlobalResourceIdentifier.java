/**
 * 
 */
package jadex.bridge;

import java.net.URL;

/**
 *  Global resources are identified by their unique resource id.
 *  Additional optional repository information can be used
 *  as download helper.
 */
public interface IGlobalResourceIdentifier
{
	/**
	 *  Get the resource id. E.g. in case of maven the global
	 *  coordinates in the form groupid:artifactid:version
	 *  @return The id. 
	 */
	public String getResourceId();
	
	/**
	 *  todo: make struct to also allow containing
	 *  Get the url.
	 *  @return The resource url.
	 */
	public URL getRepositoryInfo();
	
}
