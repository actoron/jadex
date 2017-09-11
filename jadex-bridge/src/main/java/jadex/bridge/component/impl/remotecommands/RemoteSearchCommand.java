package jadex.bridge.component.impl.remotecommands;

import java.util.Collection;

import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IRemoteCommand;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Security;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.search.ServiceRegistry;
import jadex.bridge.service.types.security.IMsgSecurityInfos;
import jadex.commons.IAsyncFilter;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Search for remote services.
 */
public class RemoteSearchCommand<T> extends AbstractInternalRemoteCommand	implements IRemoteCommand<Collection<T>>
{
	/** The query. */
	private ServiceQuery<T> query;

	/**
	 *  Create a remote search command.
	 */
	public RemoteSearchCommand()
	{
		// Bean constructor.
	}

	/**
	 *  Create a remote search command.
	 */
	public RemoteSearchCommand(ServiceQuery<T> query)
	{
		this.query = query;
	}
	
	/**
	 *  Get the query.
	 */
	public ServiceQuery<T>	getQuery()
	{
		return query;
	}
	
	/**
	 *  Set the query.
	 */
	public void	setQuery(ServiceQuery<T> query)
	{
		this.query	= query;
	}

	/**
	 *  Perform the search.
	 */
	@Override
	public IFuture<Collection<T>>	execute(IInternalAccess access, IMsgSecurityInfos secinf)
	{
//		if((""+query.getServiceType()).indexOf("AutoTerminate")!=-1)
//			System.out.println("Executing requested remote search: "+access.getComponentIdentifier()+", "+query);
		
		// No recursive global search -> change global scope to platform, and owner to local platform.
		if(!RequiredServiceInfo.isScopeOnLocalPlatform(query.getScope()))
		{
			if(query.getOwner()!=null && !access.getComponentIdentifier().getRoot().equals(query.getOwner().getRoot()))
			{
				query.setOwner(access.getComponentIdentifier().getRoot());
			}
			query.setScope(RequiredServiceInfo.SCOPE_PLATFORM);
		}
		
		final IFuture<Collection<T>>	ret;
		if(query.getFilter() instanceof IAsyncFilter)
		{
			ret	= ServiceRegistry.getRegistry(access.getComponentIdentifier()).searchServicesAsync(query);
		}
		else
		{
			ret	= new Future<Collection<T>>(ServiceRegistry.getRegistry(access.getComponentIdentifier()).searchServicesSync(query));				
		}
		
		return ret;
	}
	
	/**
	 *  Method to provide the required security level.
	 *  Overridden by subclasses.
	 */
	@Override
	protected String	getSecurityLevel(IInternalAccess access)
	{
		Class<?>	type	= query.getServiceType()!=null ? query.getServiceType().getType(access.getClassLoader()) : null;
		Security	secreq	= type!=null ? type.getAnnotation(Security.class) : null;
		return secreq!=null ? secreq.value()[0] : null;	// TODO: multiple roles
	}
}