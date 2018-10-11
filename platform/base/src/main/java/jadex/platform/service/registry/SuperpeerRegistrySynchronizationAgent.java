package jadex.platform.service.registry;

import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.service.IService;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.IServiceRegistry;
import jadex.bridge.service.search.ServiceNotFoundException;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.search.ServiceRegistry;
import jadex.bridge.service.types.registry.IPeerRegistrySynchronizationService;
import jadex.bridge.service.types.registry.ISuperpeerRegistrySynchronizationService;
import jadex.commons.Boolean3;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentKilled;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Autostart;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Superpeer registry synchronization agent. 
 *  
 *  Kill peer agent if present.
 *  Starts peer agent on terminate.
 */
@Agent(autostart=@Autostart(value=Boolean3.FALSE, name="oldsuperpeer"))
@ProvidedServices(@ProvidedService(type=ISuperpeerRegistrySynchronizationService.class, 
	implementation=@Implementation(expression="new SuperpeerRegistrySynchronizationService(SuperpeerRegistrySynchronizationService.DEFAULT_SUPERSUPERPEERS, $args.supersuperpeer? 0: 1)")))
// TODO: publication scope.
@Arguments({
	@Argument(name="supersuperpeer", clazz=boolean.class, defaultvalue="false")//,
//	@Argument(name="supersuperpeers", clazz=String.class, defaultvalue="$args.supersuperpeer? null: \"platformname1{scheme11://addi11,scheme12://addi12},platformname2{scheme21://addi21,scheme22://addi22}\""),
})
//@Properties(value=@NameValue(name="system", value="true"))
public class SuperpeerRegistrySynchronizationAgent
{
	/** The component. */
	@Agent
	protected IInternalAccess component;
	
//	@AgentArgument(convert="jadex.bridge.service.types.address.TransportAddress.fromString($value)")
//	protected TransportAddress[] supersuperpeers;
	
	/**
	 *  Called on agent start.
	 */
	@AgentCreated
	public void init()
	{
//		System.out.println("superpeers: "+Arrays.toString(supersuperpeers));
		
		try
		{
			// Kill peer agent
			IPeerRegistrySynchronizationService pser = component.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(IPeerRegistrySynchronizationService.class));
			
			if(pser!=null)
			{
				component.getExternalAccess(((IService)pser).getServiceId().getProviderId()).killComponent();
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
		return ((Boolean)component.getFeature(IArgumentsResultsFeature.class).getArguments().get("supersuperpeer")).booleanValue();
	}
	
	/**
	 *  Called on agent kill.
	 */
	@AgentKilled
	public void terminate()
	{
		IServiceRegistry reg = ServiceRegistry.getRegistry(component.getId());
		
		// Remove all remote services handled by the registry 
		//TODO
//		reg.removeServicesExcept(component.getComponentIdentifier().getRoot());
		
		// Produces problems in platform shutdown
//		IComponentManagementService cms = component.getComponentFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>( IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM));
//		cms.createComponent("registrypeer", PeerRegistrySynchronizationAgent.class.getName()+".class", null);
	}
}
