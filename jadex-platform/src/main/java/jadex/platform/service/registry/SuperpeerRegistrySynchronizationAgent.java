package jadex.platform.service.registry;

import jadex.bridge.IInternalAccess;
import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.bridge.service.IService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.IServiceRegistry;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceNotFoundException;
import jadex.bridge.service.search.ServiceRegistry;
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
 *  Superpeer registry synchronization agent. 
 *  
 *  Kill peer agent if present.
 *  Starts peer agent on terminate.
 */
@Agent
@ProvidedServices(@ProvidedService(type=ISuperpeerRegistrySynchronizationService.class, implementation=@Implementation(SuperpeerRegistrySynchronizationService.class)))
//@Properties(value=@NameValue(name="system", value="true"))
public class SuperpeerRegistrySynchronizationAgent
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
			// Kill peer agent
			IPeerRegistrySynchronizationService pser = SServiceProvider.getLocalService(component, IPeerRegistrySynchronizationService.class, RequiredServiceInfo.SCOPE_PLATFORM);
			
			if(pser!=null)
			{
				IComponentManagementService cms = SServiceProvider.getLocalService(component, IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM);
				cms.destroyComponent(((IService)pser).getServiceIdentifier().getProviderId());
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
		IServiceRegistry reg = ServiceRegistry.getRegistry(component.getComponentIdentifier());
		
		// Remove all remote services handled by the registry 
		reg.removeServicesExcept(component.getComponentIdentifier().getRoot());
		
		IComponentManagementService cms = SServiceProvider.getLocalService(component, IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM);
		cms.createComponent("registrypeer", PeerRegistrySynchronizationAgent.class.getName()+".class", null);
	}
}
