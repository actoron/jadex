package jadex.micro.testcases.longcall;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.SFuture;
import jadex.bridge.ServiceCall;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.Timeout;
import jadex.commons.ICommand;
import jadex.commons.future.Future;
import jadex.commons.future.ICommandFuture;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IPullIntermediateFuture;
import jadex.commons.future.IPullSubscriptionIntermediateFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITerminableFuture;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.future.PullIntermediateFuture;
import jadex.commons.future.PullSubscriptionIntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.commons.future.TerminableFuture;
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
	public IFuture<Void> method1()
	{
		final Future<Void> ret = new Future<Void>();
		System.out.println("Called method");
		doCall(ret);
		return ret;
	}
	
	/**
	 *  A test method.
	 */
	@Timeout(2000)
	public ITerminableFuture<Void> method2()
	{
		final TerminableFuture<Void> ret = new TerminableFuture<Void>();
		System.out.println("Called tmethod");
		doCall(ret);
		return ret;
	}
	
	/**
	 *  A test method.
	 */
	@Timeout(2000)
	public IIntermediateFuture<Void> method3()
	{
		final IntermediateFuture<Void> ret = new IntermediateFuture<Void>();
		System.out.println("Called imethod");
		doCall(ret);
		return ret;
	}
	
	/**
	 *  A test method.
	 */
	@Timeout(2000)
	public ISubscriptionIntermediateFuture<Void> method4()
	{
		final SubscriptionIntermediateFuture<Void> ret = new SubscriptionIntermediateFuture<Void>();
		System.out.println("Called smethod");
		doCall(ret);
		return ret;
	}
	
	/**
	 *  A test method.
	 */
	@Timeout(2000)
	public IPullIntermediateFuture<Void> method5()
	{
		final PullIntermediateFuture<Void> ret = new PullIntermediateFuture<Void>((ICommand<PullIntermediateFuture<Void>>)null);
		System.out.println("Called pmethod");
		doCall(ret);
		return ret;
	}
	
	/**
	 *  A test method.
	 */
	@Timeout(2000)
	public IPullSubscriptionIntermediateFuture<Void> method6()
	{
		final PullSubscriptionIntermediateFuture<Void> ret = new PullSubscriptionIntermediateFuture<Void>((ICommand<PullSubscriptionIntermediateFuture<Void>>)null);
		System.out.println("Called psmethod");
		doCall(ret);
		return ret;
	}

	/**
	 * 
	 */
	protected void doCall(final Future<?> ret)
	{
		SFuture.avoidCallTimeouts(ret, agent);
		
		ServiceCall sc = ServiceCall.getCurrentInvocation();
		long to = sc.getTimeout();
	
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
