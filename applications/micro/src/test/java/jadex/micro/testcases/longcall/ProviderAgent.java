package jadex.micro.testcases.longcall;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.SFuture;
import jadex.bridge.ServiceCall;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.annotation.Service;
import jadex.commons.ICommand;
import jadex.commons.future.Future;
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
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;


/**
 * 
 */
@Agent
@ProvidedServices(@ProvidedService(type=ITestService.class, scope=ServiceScope.GLOBAL))
@Service
public class ProviderAgent implements ITestService
{
	@Agent
	protected IInternalAccess agent;
	
	/**
	 *  Call a method that must use a secure
	 *  transport under the hood.
	 */
	public IFuture<Void> method1()
	{
//		final Future<Void> ret = new Future<Void>();
		final Future<Void> ret = (Future<Void>)SFuture.getNoTimeoutFuture(agent);
//		System.out.println("Called method1");
		doCall(ret);
		return ret;
	}
	
	/**
	 *  A test method.
	 */
	public ITerminableFuture<Void> method2()
	{
		TerminableFuture<Void> ret = new TerminableFuture<Void>();
//		System.out.println("Called tmethod2");
		doCall(ret);
		return ret;
	}
	
	/**
	 *  A test method.
	 */
	public IIntermediateFuture<Void> method3()
	{
		final IntermediateFuture<Void> ret = new IntermediateFuture<Void>();
//		System.out.println("Called imethod3");
		doCall(ret);
		return ret;
	}
	
	/**
	 *  A test method.
	 */
	public ISubscriptionIntermediateFuture<Void> method4()
	{
		final SubscriptionIntermediateFuture<Void> ret = new SubscriptionIntermediateFuture<Void>();
//		System.out.println("Called smethod4");
		doCall(ret);
		return ret;
	}
	
	/**
	 *  A test method.
	 */
	public IPullIntermediateFuture<Void> method5()
	{
		final PullIntermediateFuture<Void> ret = new PullIntermediateFuture<Void>((ICommand<PullIntermediateFuture<Void>>)null);
		SFuture.avoidCallTimeouts(ret, agent);
//		System.out.println("Called pmethod5");
		doCall(ret);
		return ret;
	}
	
	/**
	 *  A test method.
	 */
	public IPullSubscriptionIntermediateFuture<Void> method6()
	{
		final PullSubscriptionIntermediateFuture<Void> ret = new PullSubscriptionIntermediateFuture<Void>((ICommand<PullSubscriptionIntermediateFuture<Void>>)null);
		SFuture.avoidCallTimeouts(ret, agent);
//		System.out.println("Called psmethod6");
		doCall(ret);
		return ret;
	}

	/**
	 * 
	 */
	protected void doCall(final Future<?> ret)
	{
		ServiceCall sc = ServiceCall.getCurrentInvocation();
		long to = sc.getTimeout();
		boolean realtime = sc.isRemoteCall(agent.getId());
		
		SFuture.avoidCallTimeouts(ret, agent.getExternalAccess(), to, 0.5, realtime);
		
		System.out.println("Timeout is: " + to);		
		System.out.println(agent + " isRemote / Realtime: " + realtime);
	
		final long wait = to>0? (long)(to*1.2): 0;
		final long startwait = System.currentTimeMillis();
//		System.out.println("waiting: "+wait+", "+System.currentTimeMillis());
		agent.getFeature(IExecutionFeature.class).waitForDelay(wait, new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				System.out.println("waited: "+ (System.currentTimeMillis() - startwait));
				ret.setResultIfUndone(null);
				return IFuture.DONE;
			}
		}, realtime);
	}
}
