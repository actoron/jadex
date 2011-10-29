package jadex.micro.examples.ws;

import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

/**
 *  Agent that wraps a normal web service as Jadex service.
 *  In this way the web service can be used by active components
 *  in the same way as normal Jadex component services.
 */
@Agent
@ProvidedServices(@ProvidedService(type=IQuoteService.class, implementation=@Implementation(QuoteWrapperService.class)))
@RequiredServices(@RequiredService(name="cms", type=IComponentManagementService.class, 
	binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)))
@ComponentTypes(@ComponentType(name="invocation", filename="jadex/micro/examples/ws/InvocationAgent.class"))
public class WebServiceAgent
{
}
