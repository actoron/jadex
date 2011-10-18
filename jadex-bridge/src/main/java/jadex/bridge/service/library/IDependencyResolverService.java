package jadex.bridge.service.library;

import jadex.bridge.IResourceIdentifier;
import jadex.bridge.ResourceIdentifier;
import jadex.commons.future.IFuture;

import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 *  Service for resolving deployment artifact dependencies.
 */
public interface IDependencyResolverService
{
	/**
	 *  Load dependencies from a resource identifier.
	 *  @param rid	A local or global resource identifier. If both local and global ids are present,
	 *    local takes precedence, e.g. resolving to workspace urls before fetching an older snapshot from a repository.
	 *  @return A map containing the dependencies as mapping (parent RID -> list of children RIDs).
	 */
	public IFuture<Map<IResourceIdentifier, List<IResourceIdentifier>>>	loadDependencies(IResourceIdentifier rid);
	
	/**
	 *  Get the resource identifier for an url.
	 *  @param url The url.
	 *  @return The resource identifier.
	 */
	public IFuture<IResourceIdentifier> getResourceIdentifier(URL url);

}
