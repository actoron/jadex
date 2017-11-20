package jadex.platform.service.registry;

import java.util.Arrays;

import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.service.IService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.IServiceRegistry;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceNotFoundException;
import jadex.bridge.service.search.ServiceRegistry;
import jadex.bridge.service.types.address.TransportAddress;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.registry.IPeerRegistrySynchronizationService;
import jadex.bridge.service.types.registry.ISuperpeerRegistrySynchronizationService;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentKilled;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Implementation;
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
@Arguments({
	@Argument(name="supersuperpeer", clazz=boolean.class, defaultvalue="true"),
	@Argument(name="supersuperpeers", clazz=String.class, defaultvalue="$args.supersuperpeer? null: \"platformname1{scheme11://addi11,scheme12://addi12},platformname2{scheme21://addi21,scheme22://addi22}\""),
})
//@Properties(value=@NameValue(name="system", value="true"))
public class SuperpeerRegistrySynchronizationAgent
{
	/** The component. */
	@Agent
	protected IInternalAccess component;
	
	@AgentArgument(convert="jadex.bridge.service.types.address.TransportAddress.fromString($value)")
	protected TransportAddress[] supersuperpeers;
	
	/**
	 *  Called on agent start.
	 */
	@AgentCreated
	public void init()
	{
		System.out.println("superpeers: "+Arrays.toString(supersuperpeers));
		
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
	 *  Test if is supersuperpeer.
	 */
	public boolean isSupersuperpeer()
	{
		return ((Boolean)component.getComponentFeature(IArgumentsResultsFeature.class).getArguments().get("supersuperpeer")).booleanValue();
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
		
		// Produces problems in platform shutdown
//		IComponentManagementService cms = SServiceProvider.getLocalService(component, IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM);
//		cms.createComponent("registrypeer", PeerRegistrySynchronizationAgent.class.getName()+".class", null);
	}
}
