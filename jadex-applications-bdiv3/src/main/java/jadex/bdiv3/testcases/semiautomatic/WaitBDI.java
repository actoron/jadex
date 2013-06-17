package jadex.bdiv3.testcases.semiautomatic;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.BDIConfiguration;
import jadex.bdiv3.annotation.BDIConfigurations;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.runtime.impl.RPlan;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.NameValue;

import java.util.List;

/**
 *  Agent that tests plan waiting.
 */
@Agent
@BDIConfigurations(@BDIConfiguration(name="def", initialplans=@NameValue(name="waitPlan")))
//@BDIConfigurations(@BDIConfiguration(name="def", initialplans=@NameValue(name="waitqueuePlan")))
public class WaitBDI
{
	@Agent
	protected BDIAgent agent;
	
	@Belief
	protected List<String> names;
	
	/**
	 *  The agent body.
	 */
	@AgentBody
	public void body()
	{
		agent.waitFor(1000, new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				System.out.println("Adding");
				addName("a");
				
				agent.waitFor(1000, new IComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						System.out.println("Removing");
						removeName("a");
						return IFuture.DONE;
					}
				});
				return IFuture.DONE;
			}
		});
	}
	
	/**
	 *  Add a name.
	 *  @param name The name.
	 */
	protected void addName(String name)
	{
		names.add(name);
	}
	
	/**
	 *  Remove a name.
	 *  @param name The name.
	 */
	protected void removeName(String name)
	{
		names.remove(name);
	}
	
	/**
	 *  Plan that waits for addition and removal of a name.
	 */
	@Plan
	protected IFuture<Void> waitPlan(final RPlan rplan)
	{
		final Future<Void> ret = new Future<Void>();
		System.out.println("plan waiting: "+rplan.getId());
		
//		rplan.waitForCondition(null, new String[]{ChangeEvent.FACTADDED+".names"}).addResultListener(new DelegationResultListener<Void>(ret)
//		{
//			public void customResultAvailable(Void result)
//			{
//				System.out.println("continued");
//			}
//		});
		
		rplan.waitForFactAdded("names").addResultListener(new ExceptionDelegationResultListener<Object, Void>(ret)
		{
			public void customResultAvailable(Object result)
			{
				System.out.println("plan continues 1: "+rplan.getId()+" "+result);
				rplan.waitForFactRemoved("names").addResultListener(new ExceptionDelegationResultListener<Object, Void>(ret)
				{
					public void customResultAvailable(Object result)
					{
						System.out.println("plan continues 2: "+rplan.getId()+" "+result);
						ret.setResult(null);
					}
				});
			}
			
			public void exceptionOccurred(Exception exception)
			{
				exception.printStackTrace();
			}
		});
		
//		rplan.waitForFactAddedOrRemoved("names").addResultListener(new ExceptionDelegationResultListener<ChangeEvent, Void>(ret)
//		{
//			public void customResultAvailable(ChangeEvent result)
//			{
//				System.out.println("plan continues 1: "+rplan.getId()+" "+result);
//				rplan.waitForFactAddedOrRemoved("names").addResultListener(new ExceptionDelegationResultListener<ChangeEvent, Void>(ret)
//				{
//					public void customResultAvailable(ChangeEvent result)
//					{
//						System.out.println("plan continues 2: "+rplan.getId()+" "+result);
//						ret.setResult(null);
//					}
//				});
//			}
//		});
		
		return ret;
	}
	
//	/**
//	 *  Plan that waits with waitqueue for addition of a name.
//	 */
//	@Plan(waitqueue=@Trigger(factaddeds="names"))
//	protected IFuture<Void> waitqueuePlan(final RPlan rplan)
//	{
//		final Future<Void> ret = new Future<Void>();
//		System.out.println("plan waiting: "+rplan.getId());
//		
//		rplan.waitFor(3000).addResultListener(new DelegationResultListener<Void>(ret)
//		{
//			public void customResultAvailable(Void result)
//			{
//				rplan.waitForFactAdded("names").addResultListener(new ExceptionDelegationResultListener<Object, Void>(ret)
//				{
//					public void customResultAvailable(Object result)
//					{
//						System.out.println("continued: "+result);
//					}
//				});
//			}
//		});
//		
//		return ret;
//	}	
}