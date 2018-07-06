package jadex.bridge.component.impl.remotecommands;

import java.util.Collection;

import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IRemoteCommand;
import jadex.bridge.service.annotation.Security;
import jadex.bridge.service.component.IInternalRequiredServicesFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.security.IMsgSecurityInfos;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Search for remote services.
 */
public class RemoteSearchCommand<T> extends AbstractInternalRemoteCommand	implements IRemoteCommand<Collection<T>>, ISecuredRemoteCommand
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
	public ServiceQuery<T> getQuery()
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
//		System.out.println("Remote search command triggered: " + query.toString());
//		if((""+query.getServiceType()).indexOf("IClock")!=-1)
//			System.out.println("Executing requested remote search: "+access.getIdentifier()+", "+query);
		
//		// No recursive global search -> change global scope to platform, and owner to local platform.
//		if(!RequiredServiceInfo.isScopeOnLocalPlatform(query.getScope()))
//		{
//			System.out.println("Performing remote global search (should only happen on superpeers): "+query);
////			if(query.getOwner()!=null && !access.getComponentIdentifier().getRoot().equals(query.getOwner().getRoot()))
////				query.setOwner(access.getComponentIdentifier().getRoot());
////			query.setScope(RequiredServiceInfo.SCOPE_PLATFORM);
//		}
		
		return new Future<Collection<T>>(((IInternalRequiredServicesFeature)access.getFeature(IRequiredServicesFeature.class)).resolveLocalServices(getQuery(), null));				
	}
	
	/**
	 *  Method to provide the required security level.
	 *  Overridden by subclasses.
	 */
	@Override
	public Security getSecurityLevel(IInternalAccess access)
	{
		Security ret = null;
		
		if(query.getServiceType()!=null)
		{
			Class<?>	type	= query.getServiceType().getType(access.getClassLoader());
			ret	= type!=null ? type.getAnnotation(Security.class) : null;
		}
		
		// TODO: Lars, why not Security of service?
		// Search access depends on the (imaginary) registry service access
		// Because no explicit service is available it checks if a global-superpeer is used
		// This is checked by fetching the level of ISuperpeerRegistrySynchronizationService.class (if available)
//		if(ret==null)
//		{
//			IServiceRegistry reg = ServiceRegistry.getRegistry(access.getComponentIdentifier());
//			ISuperpeerRegistrySynchronizationService srss = reg.searchService(new ServiceQuery<ISuperpeerRegistrySynchronizationService>(ISuperpeerRegistrySynchronizationService.class, RequiredServiceInfo.SCOPE_PLATFORM, null, access.getComponentIdentifier(), null));
//			if(srss!=null)
//			{
//				ret = ServiceIdentifier.getSecurityLevel(ISuperpeerRegistrySynchronizationService.class);
//			}
//		}
		
		return ret;
	}
	
	/**
	 *  Get a string representation.
	 */
	public String	toString()
	{
		return "RemoteSearchCommand("+query+")";
	}
}