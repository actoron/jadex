package jadex.bridge.component.impl.remotecommands;

import java.util.Set;

import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IRemoteCommand;
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
	public IFuture<Set<T>> execute(IInternalAccess access, Future<Set<T>> future, IMsgSecurityInfos secinf)
	{
		return new Future<Set<T>>(ServiceRegistry.getRegistry(access.getComponentIdentifier()).searchServicesSync(query));
	}
}