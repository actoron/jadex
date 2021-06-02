package jadex.platform.remotereference;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Imports;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

/**
 *  Agent providing the remote search service.
 */
@Agent
@Imports("jadex.micro.*")
@RequiredServices(@RequiredService(name="local", type=ILocalService.class, scope=ServiceScope.GLOBAL))
@ProvidedServices(@ProvidedService(type=ISearchService.class, scope=ServiceScope.GLOBAL))
@Service
public class SearchServiceProviderAgent implements ISearchService
{
	//-------- attributes --------
	
	/** The agent. */
	@Agent
	protected IInternalAccess	agent;
	
	public IFuture<ILocalService> searchService(String dummy)
	{
//		System.out.println("searcher");
		IFuture<ILocalService>	ret	= agent.getFeature(IRequiredServicesFeature.class).getService("local");
		return ret;
	}
}
