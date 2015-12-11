package jadex.platform.service.dht;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.types.dht.IRingApplicationService;
import jadex.bridge.service.types.dht.IRingNodeDebugService;
import jadex.bridge.service.types.dht.IRingNodeService;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentService;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

/**
 * This Agent represents a part of the DHT ring, providing a Ring Node Service.
 */
@Agent
@Description("This Agent represents a node in a DHT ring, providing an overlay service for further applications.")
@Arguments({
	@Argument(name="overlayId", clazz=String.class, defaultvalue="\"defring\"", description="Identifier of the application. Used for distinction of different ring overlays."),
})
@ProvidedServices({
	@ProvidedService(name="providedRing", type = IRingNodeService.class, implementation = @Implementation(expression = "new jadex.platform.service.dht.RingNodeService($args.overlayId)"), scope = RequiredServiceInfo.SCOPE_GLOBAL),
	@ProvidedService(name="ring", type = IRingApplicationService.class, implementation = @Implementation(expression="$component.getComponentFeature(jadex.bridge.service.component.IProvidedServicesFeature.class).getProvidedServiceRawImpl(\"providedRing\")"), scope = RequiredServiceInfo.SCOPE_GLOBAL),
	@ProvidedService(name="providedDebugRing", type = IRingNodeDebugService.class, implementation = @Implementation(expression="$component.getComponentFeature(jadex.bridge.service.component.IProvidedServicesFeature.class).getProvidedServiceRawImpl(\"providedRing\")"), scope = RequiredServiceInfo.SCOPE_GLOBAL),
})
@RequiredServices({
	@RequiredService(name = "ringnodes", type = IRingNodeService.class, multiple = true, binding=@Binding(scope = RequiredServiceInfo.SCOPE_GLOBAL, dynamic = false)),
	@RequiredService(name="ring", type = IRingApplicationService.class, binding=@Binding(scope=Binding.SCOPE_COMPONENT, create = false))
})
//@RequiredServices( {
//@RequiredService(name="ring", type = IRingApplicationService.class, binding=@Binding(scope=Binding.SCOPE_COMPONENT, create = false,
//	creationinfo=@CreationInfo(type = "ringAgent")))
//})

public class RingNodeAgent 
{
	/** Identifier for the overlay for this node to be part of. */
	@AgentArgument
	protected String	overlayId;
	
	/** The local ringnode service **/
	@AgentService
	protected IRingApplicationService	ring;
	
	/** The agent access **/
	@Agent
	private IInternalAccess agent;
	
	@AgentCreated
	public void onCreate() {
//		System.out.println("ring agent created");
		ring.setInitialized(true);
	}
}
