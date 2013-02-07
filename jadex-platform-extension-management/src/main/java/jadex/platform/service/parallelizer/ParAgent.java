package jadex.platform.service.parallelizer;

import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.micro.MicroAgent;
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
 * 
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
//@Configurations(@Configuration(name="def", components=@Component(type="spa")))
public class ParAgent //implements IParallelService
{
	//-------- attributes --------
	
	@Agent
	protected MicroAgent agent;
	
//	@AgentCreated 
//	public IFuture<Void> init()
//	{
//		final Future<Void> ret = new Future<Void>();
//		
//		IFuture<IServicePoolService> fut = agent.getServiceContainer().getRequiredService("poolser");
//		fut.addResultListener(new ExceptionDelegationResultListener<IServicePoolService, Void>(ret)
//		{
//			public void customResultAvailable(final IServicePoolService sps)
//			{
//				sps.addServiceType(ISequentialService.class, new DefaultPoolStrategy(10, 20), 
//					"jadex.platform.service.parallelizer.SeqAgent.class")
//					.addResultListener(new DelegationResultListener<Void>(ret)
//				{
//					public void customResultAvailable(Void result)
//					{
//						ret.setResult(null);
////						IFuture<ISequentialService> fut = agent.getServiceContainer().getRequiredService("seqser");
////						fut.addResultListener(new ExceptionDelegationResultListener<ISequentialService, Void>(ret)
////						{
////							public void customResultAvailable(ISequentialService result)
////							{
////								seqser = result;
////								ret.setResult(null);
////							}
////						});
//					}
//				});
//			}
//		});
//		
//		return ret;
//	}
	
//	/**
//	 * 
//	 */
//	public IIntermediateFuture<String> doParallel(String[] files)
//	{
//	}
}
