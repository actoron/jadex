package jadex.platform.service.parallelizer;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.DefaultPoolStrategy;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateFuture;
import jadex.platform.service.servicepool.IServicePoolService;

/**
 *  Implementation of the service that uses divide and
 *  conquer to distribute tasks to a subordinated service.
 */
@Service
public class MappingService implements IParallelService
{
	@ServiceComponent
	protected IInternalAccess agent;
	
	/** The service pool. */
	protected ISequentialService seqser;
	
	/**
	 * 
	 */
	@ServiceStart
	public IFuture<Void> init()
	{
		final Future<Void> ret = new Future<Void>();
		
		IFuture<IServicePoolService> fut = agent.getFeature(IRequiredServicesFeature.class).getService("poolser");
		fut.addResultListener(new ExceptionDelegationResultListener<IServicePoolService, Void>(ret)
		{
			public void customResultAvailable(final IServicePoolService sps)
			{
				sps.addServiceType(ISequentialService.class, new DefaultPoolStrategy(10, 10000, 20), 
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
			}
		});
		
		return ret;
	}
	
	/**
	 * 
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
