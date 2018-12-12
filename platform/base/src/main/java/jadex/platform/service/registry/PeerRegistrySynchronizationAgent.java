package jadex.platform.service.registry;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.IService;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceNotFoundException;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.registry.IPeerRegistrySynchronizationService;
import jadex.bridge.service.types.registry.ISuperpeerRegistrySynchronizationService;
import jadex.commons.Boolean3;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Peer registry synchronization agent. 
 */
@Agent(name="peer", autostart=Boolean3.FALSE)
@ProvidedServices(@ProvidedService(type=IPeerRegistrySynchronizationService.class, implementation=@Implementation(PeerRegistrySynchronizationService.class)))
//@Properties(value=@NameValue(name="system", value="true"))
public class PeerRegistrySynchronizationAgent
{
	/** The component. */
	@Agent
	protected IInternalAccess component;
	
	/**
	 *  Called on agent start.
	 */
	@AgentCreated
	public void init()
	{
		try
		{
			// Kill superpeer agent
			ISuperpeerRegistrySynchronizationService spser = component.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>( ISuperpeerRegistrySynchronizationService.class, ServiceScope.PLATFORM));
			
			if(spser!=null)
			{
				component.getExternalAccess(((IService)spser).getServiceId().getProviderId()).killComponent();
			}
		}
		catch(ServiceNotFoundException e)
		{
		}
	}
	
//	/**
//	 *  Called on agent kill.
//	 */
//	@AgentKilled
//	public void terminate()
//	{
//		// Produces problems in platform shutdown
//		IComponentManagementService cms = component.getComponentFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>( IComponentManagementService.class, ServiceScope.PLATFORM));
//		cms.createComponent("registrysuperpeer", SuperpeerRegistrySynchronizationAgent.class.getName()+".class", null);
//	}
}

