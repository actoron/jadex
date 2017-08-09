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
public class RemoteSearchCommand<T> implements IRemoteCommand<Collection<T>>
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
//		System.out.println("Executing requested remote search: "+access.getComponentIdentifier()+", "+query.getServiceType());
		
		final IFuture<Collection<T>>	ret;
		Class<?>	type	= query.getServiceType()!=null ? query.getServiceType().getType(access.getClassLoader()) : null;
		Security	secreq	= type!=null ? type.getAnnotation(Security.class) : null;
		String	seclevel	= secreq!=null ? secreq.value() : null;
		
		if(Security.UNRESTRICTED.equals(seclevel) || secinf.isAuthenticated())
		{
			// No recursive global search -> change global scope to platform, and owner to local platform.
			if(!RequiredServiceInfo.isScopeOnLocalPlatform(query.getScope()))
			{
				if(query.getOwner()!=null && !access.getComponentIdentifier().getRoot().equals(query.getOwner().getRoot()))
				{
					query.setOwner(access.getComponentIdentifier().getRoot());
				}
				query.setScope(RequiredServiceInfo.SCOPE_PLATFORM);
			}
			
			if(query.getFilter() instanceof IAsyncFilter)
			{
				ret	= ServiceRegistry.getRegistry(access.getComponentIdentifier()).searchServicesAsync(query);
			}
			else
			{
				ret	= new Future<Collection<T>>(ServiceRegistry.getRegistry(access.getComponentIdentifier()).searchServicesSync(query));				
			}
		}
		else
		{
			ret	= new Future<Collection<T>>(new SecurityException("Not allowed to search for type "+type));
		}
		
		return ret;
	}
}