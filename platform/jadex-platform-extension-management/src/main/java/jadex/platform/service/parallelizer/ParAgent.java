package jadex.platform.service.parallelizer;

import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.CreationInfo;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.platform.service.servicepool.IServicePoolService;

/**
 *  Agent that wants to offer a service that delegates
 *  calls to workers for parallel execution.
 */
@Agent
@Service
@ProvidedServices(
	@ProvidedService(type=IParallelService.class, 
		implementation=@Implementation(MappingService.class)))
@RequiredServices(
{
	@RequiredService(name="poolser", type=IServicePoolService.class, binding=@Binding(
		scope=RequiredServiceInfo.SCOPE_PLATFORM, create=true, creationinfo=@CreationInfo(type="spa"))),
	@RequiredService(name="seqser", type=ISequentialService.class)
})
@ComponentTypes(@ComponentType(name="spa", filename="jadex.platform.service.servicepool.ServicePoolAgent.class"))
public class ParAgent 
{
}
