package jadex.bridge.component.impl.remotecommands;

import java.util.Set;

import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IRemoteCommand;
import jadex.bridge.service.annotation.Security;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.search.ServiceRegistry;
import jadex.bridge.service.types.security.IMsgSecurityInfos;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Search for remote services.
 */
public class RemoteSearchCommand<T> implements IRemoteCommand<Set<T>>
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
	public IFuture<Set<T>>	execute(IInternalAccess access, IMsgSecurityInfos secinf)
	{
		Class<?>	type	= query.getServiceType()!=null ? query.getServiceType().getType(access.getClassLoader()) : null;
		Security	secreq	= type!=null ? type.getAnnotation(Security.class) : null;
		String	seclevel	= secreq!=null ? secreq.value() : null;
		
		if(Security.UNRESTRICTED.equals(seclevel) || secinf.isAuthenticatedPlatform())
		{
			return new Future<Set<T>>(ServiceRegistry.getRegistry(access.getComponentIdentifier()).searchServicesSync(query));
		}
		else
		{
			return new Future<Set<T>>(new SecurityException("Not allowed to search for type "+type));
		}
	}
}