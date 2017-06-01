package jadex.platform.service.registry;

import jadex.bridge.IInternalAccess;
import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.bridge.service.IService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceNotFoundException;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.registry.IPeerRegistrySynchronizationService;
import jadex.bridge.service.types.registry.ISuperpeerRegistrySynchronizationService;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentKilled;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Peer registry synchronization agent. 
 */
@Agent
@ProvidedServices(@ProvidedService(type=IPeerRegistrySynchronizationService.class, implementation=@Implementation(PeerRegistrySynchronizationService.class)))
@Properties(value=@NameValue(name="system", value="true"))
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
			ISuperpeerRegistrySynchronizationService spser = SServiceProvider.getLocalService(component, ISuperpeerRegistrySynchronizationService.class, RequiredServiceInfo.SCOPE_PLATFORM);
			
			if(spser!=null)
			{
				IComponentManagementService cms = SServiceProvider.getLocalService(component, IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM);
				cms.destroyComponent(((IService)spser).getServiceIdentifier().getProviderId());
			}
		}
		catch(ServiceNotFoundException e)
		{
		}
	}
	
	/**
	 *  Called on agent kill.
	 */
	@AgentKilled
	public void terminate()
	{
		IComponentManagementService cms = SServiceProvider.getLocalService(component, IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM);
		cms.createComponent("registrysuperpeer", SuperpeerRegistrySynchronizationAgent.class.getName()+".class", null);
	}
}

