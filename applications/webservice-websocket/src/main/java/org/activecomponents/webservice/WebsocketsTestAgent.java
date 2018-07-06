package org.activecomponents.webservice;

import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.annotation.Service;
import jadex.commons.Boolean3;
import jadex.commons.ICommand;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IPullIntermediateFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.PullIntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.micro.annotation.Agent;

@Agent(autoprovide=Boolean3.TRUE)
@Service
public class WebsocketsTestAgent implements IWebsocketTestService
{
	@Agent
	protected IInternalAccess agent;
	
//	/**
//	 *  Say hello.
//	 */
//	public IFuture<String> sayHello()
//	{
//		System.out.println("Say hello called on: "+agent.getComponentIdentifier());
//		return new Future<String>("Hello World");
//	}
	
	/**
	 *  Say hello to somebody.
	 */
	public IFuture<String> sayHelloTo(String name)
	{
		System.out.println("Say hello called on: "+agent.getComponentIdentifier());
		return new Future<String>("Hello "+name+" from "+agent.getComponentIdentifier());
	}
	
	/**
	 *  Say hello to somebody.
	 */
	public IFuture<String> sayHelloTo(String name, String name2)
	{
		System.out.println("Say hello 2 called on: "+agent.getComponentIdentifier());
		return new Future<String>("Hello "+name+" "+name2);
	}
	
	/**
	 *  Produce some exception.
	 */
	public IFuture<Void> produceException()
	{
		return new Future<Void>(new RuntimeException("Intended Exception"));
	}
	
	/**
	 *  Add two numbers.
	 */
	public IFuture<Integer> add(int a, int b)
	{
		return new Future<Integer>(Integer.valueOf(a+b));
	}
	
	/**
	 *  Add two numbers.
	 */
	public IFuture<Double> add(double a, double b)
	{
		return new Future<Double>(new Double(a+b));
	}
	
	/**
	 *  Method with intermediate results.
	 */
	public ISubscriptionIntermediateFuture<Integer> count(final int max, final long delay)
	{
		final SubscriptionIntermediateFuture<Integer> ret = new SubscriptionIntermediateFuture<Integer>();
		
		final int[] cnt = new int[1];
		
		agent.getComponentFeature(IExecutionFeature.class).waitForDelay(delay).addResultListener(new IResultListener<Void>()
		{
			public void resultAvailable(Void arg0)
			{
				// If terminated just do nothing
				if(!ret.isDone())
				{
					if(cnt[0]<max)
					{
						ret.addIntermediateResult(Integer.valueOf(cnt[0]++));
						agent.getComponentFeature(IExecutionFeature.class).waitForDelay(delay).addResultListener(this);
					}
					else
					{
						ret.setFinished();
					}
				}
			}
			
			public void exceptionOccurred(Exception e)
			{
				ret.setExceptionIfUndone(e);
			}
		});
			
		return ret;
	}
	
	/**
	 *  Method with pullable results.
	 */
	public IPullIntermediateFuture<String> pull(final int max)
	{
		final PullIntermediateFuture<String> ret = new PullIntermediateFuture<String>(
			new ICommand<PullIntermediateFuture<String>>()
		{
			int cnt = 0;
			public void execute(PullIntermediateFuture<String> fut)
			{
				if(cnt<max)
					fut.addIntermediateResult("step("+(cnt++)+"/"+max+")");
				
				if(cnt==max)
					fut.setFinished();
			}
		});
		
		return ret;
	}
}
