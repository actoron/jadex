package jadex.platform.service.parallelizer;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.OnInit;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.servicepool.IServicePoolService;
import jadex.commons.DefaultPoolStrategy;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentServiceSearch;
import jadex.micro.annotation.Component;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.OnService;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

/**
 * 
 */
@Agent
@Service
@ProvidedServices(@ProvidedService(type=IParallelService.class))
@RequiredServices(
{
	@RequiredService(name="poolser", type=IServicePoolService.class),
	@RequiredService(name="seqser", type=ISequentialService.class)
})
@ComponentTypes(@ComponentType(name="spa", filename="jadex.platform.service.servicepool.ServicePoolAgent.class"))
@Configurations(@Configuration(name="def", components=@Component(type="spa")))
public class Par2Agent implements IParallelService
{
	//-------- attributes --------
	
	/** The pool service (injected by jadex runtime). */
//	@AgentServiceSearch
	@OnService
	protected IServicePoolService	poolser;
	
	/** The sequential service (set in init). */
	protected ISequentialService	seqser;
	
	//@AgentCreated 
	@OnInit
	public IFuture<Void> init(final IInternalAccess agent)
	{
		final Future<Void> ret = new Future<Void>();
		
		poolser.addServiceType(ISequentialService.class, new DefaultPoolStrategy(10, 20), 
			"jadex.platform.service.parallelizer.SeqAgent.class")
		.addResultListener(new DelegationResultListener<Void>(ret)
		{
			public void customResultAvailable(Void result)
			{
				IFuture<ISequentialService> fut = agent.getFeature(IRequiredServicesFeature.class).getService("seqser");
				fut.addResultListener(new ExceptionDelegationResultListener<ISequentialService, Void>(ret)
				{
					public void customResultAvailable(ISequentialService result)
					{
						seqser = result;
						ret.setResult(null);
					}
				});
			}
		});
		
		return ret;
	}
	
	/**
	 *  Method that wants to process data in parallel.
	 */
	public IIntermediateFuture<String> doParallel(final String[] data)
	{
		final IntermediateFuture<String> ret = new IntermediateFuture<String>();
		
		final int cnt[] = new int[1];
		for(int i=0; i<data.length; i++)
		{
			seqser.doSequential(data[i]).addResultListener(new IResultListener<String>()
			{
				public void resultAvailable(String result)
				{
					ret.addIntermediateResult(result);
					if(++cnt[0]==data.length)
						ret.setFinished();
				}

				public void exceptionOccurred(Exception exception)
				{
					exception.printStackTrace();
					if(++cnt[0]==data.length)
						ret.setFinished();
				}
			});
		}

		return ret;
	}
}
