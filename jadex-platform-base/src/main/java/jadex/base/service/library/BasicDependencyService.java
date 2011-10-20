package jadex.base.service.library;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.ResourceIdentifier;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.types.library.IDependencyService;
import jadex.commons.Tuple2;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 *  The basic dependency service for creating (local) rids.
 */
@Service
public class BasicDependencyService implements IDependencyService
{
	//-------- attributes --------
	
	/** The service providing component. */
	@ServiceComponent
	protected IInternalAccess	component;
	
	/** The logger. */
	protected Logger	logger;
	
	/** The component identifier to use for creating local resource IDs.
	 *  The assumption is that URLs are only valid on the local platform. */
	protected IComponentIdentifier	cid;
	
	//-------- constructors --------
	
	/**
	 *  Bean constructor for service creation.
	 */
	public BasicDependencyService()
	{		
	}

	//-------- methods --------

	/**
	 *  Start the service.
	 */
	@ServiceStart
	public IFuture<Void> startService()
	{
		this.cid	= component.getComponentIdentifier().getRoot();
		this.logger	= component.getLogger();
		return IFuture.DONE;
	}
	
	/**
	 *  Load dependencies from a resource identifier.
	 *  @param rid	A local or global resource identifier. If both local and global ids are present,
	 *    local takes precedence, e.g. resolving to workspace urls before fetching an older snapshot from a repository.
	 *  @return A map containing the dependencies as mapping (parent RID -> list of children RIDs).
	 */
	public IFuture<Map<IResourceIdentifier, List<IResourceIdentifier>>>	loadDependencies(IResourceIdentifier rid)
	{
		// todo: implement dependencies based on manifest
		
		Map<IResourceIdentifier, List<IResourceIdentifier>> res = new HashMap<IResourceIdentifier, List<IResourceIdentifier>>();
		res.put(rid, new ArrayList<IResourceIdentifier>());
		return new Future<Map<IResourceIdentifier, List<IResourceIdentifier>>>(res);
	}
	
	/**
	 *  Get the resource identifier for an url.
	 *  @param url The url.
	 *  @return The resource identifier.
	 */
	public IFuture<IResourceIdentifier> getResourceIdentifier(URL url)
	{
		// Does not use global identifiers.
		Tuple2<IComponentIdentifier, URL> lid = new Tuple2<IComponentIdentifier, URL>(cid, url);
		String gid	= null;
		ResourceIdentifier rid = new ResourceIdentifier(lid, gid);
		return new Future<IResourceIdentifier>(rid);
	}
}
