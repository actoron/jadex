package jadex.platform.service.pawareness;

import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;

/**
 *  Service for making available pre-defined catalog of platforms + addresses.
 *  Platforms are specified as URLs:
 *  
 *  <transporttype>://<platformname>@<address>:<port>
 *  
 *  Example:
 *  tcp://myplatform@jadexplatform.example.com:5000
 *
 */
@Service(system=true)
public interface IPlatformCatalogService
{
	/**
	 *  Adds a platform to the catalog.
	 *  
	 *  @param platformurl URL of the platform.
	 *  @return Null, when done.
	 */
	public IFuture<Void> addPlatform(String platformurl);
	
	/**
	 *  Removes a platform from the catalog.
	 *  
	 *  @param name Name of the platform.
	 *  @return Null, when done.
	 */
	public IFuture<Void> removePlatform(String name);
}
