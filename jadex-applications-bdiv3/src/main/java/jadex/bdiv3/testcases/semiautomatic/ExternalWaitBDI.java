package jadex.bdiv3.testcases.semiautomatic;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.BDIConfiguration;
import jadex.bdiv3.annotation.BDIConfigurations;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.runtime.IPlan;
import jadex.bdiv3.runtime.impl.RPlan;
import jadex.commons.IResultCommand;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.NameValue;

/**
 * 
 */
@Agent
@BDIConfigurations(@BDIConfiguration(name="def", initialplans=@NameValue(name="extWait")))
public class ExternalWaitBDI
{
	@Agent
	protected BDIAgent agent;
	
	@Plan
	protected IFuture<Void> extWait(IPlan plan)
	{
		final Future<Void> ret = new Future<Void>();
		
		System.out.println("before waiting");
		
		plan.invokeInterruptable(new IResultCommand<IFuture<Void>, Void>()
		{
			public IFuture<Void> execute(Void args)
			{
				System.out.println("start waiting...");
				return agent.waitForDelay(3000);
			}
		}).addResultListener(new IResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
				System.out.println("ended waiting normally");
			}
			
			public void exceptionOccurred(Exception exception)
			{
				System.out.println("ex: "+exception);
			}
		});
		
		plan.abort();
		
		return ret;
	}	
}
