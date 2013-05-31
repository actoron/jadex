package jadex.micro.testcases.longcall;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ServiceCall;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.Timeout;
import jadex.commons.future.Future;
import jadex.commons.future.ICommandFuture;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IntermediateFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;


/**
 * 
 */
@Agent
@ProvidedServices(@ProvidedService(type=ITestService.class, implementation=@Implementation(expression="$pojoagent")))
@Service
public class ProviderAgent implements ITestService
{
	@Agent
	protected MicroAgent agent;
	
	/**
	 *  Call a method that must use a secure
	 *  transport under the hood.
	 */
	public IFuture<Void> method(String msg)
	{
		final Future<Void> ret = new Future<Void>();
		
		System.out.println("Called method");
		
		doCall(ret);
		
		return ret;
//		return IFuture.DONE;
	}
	

	/**
	 *  A second test method.
	 */
	public IIntermediateFuture<Void> imethod()
	{
		final IntermediateFuture<Void> ret = new IntermediateFuture<Void>();
		
		System.out.println("Called imethod");
		
		doCall(ret);
		
		return ret;
//		return IFuture.DONE;
	}
	
	/**
	 * 
	 */
	protected void doCall(final Future<?> ret)
	{
		ServiceCall sc = ServiceCall.getCurrentInvocation();
		long to = sc.getTimeout();
//		boolean local = sc.getCaller().getPlatformName().equals(agent.getComponentIdentifier().getPlatformName());
//		long to = sc.getTimeout()>0? sc.getTimeout(): (local? BasicService.DEFAULT_LOCAL: BasicService.DEFAULT_REMOTE);
//		to = 5000;
		
		if(to>0)
		{
			final long w = (long)(to*0.8);
			IComponentStep<Void> step = new IComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
				{
					if(!ret.isDone())
					{
						ret.sendCommand(ICommandFuture.Type.UPDATETIMER);
						agent.waitForDelay(w, this);
					}
					return IFuture.DONE;
				}
			};
			agent.waitForDelay(w, step);
		}
	
		long wait = to>0? to*3: 0;
		System.out.println("waiting: "+wait);
		agent.waitForDelay(wait, new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				ret.setResult(null);
				return IFuture.DONE;
			}
		});
	}
}
