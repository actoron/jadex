package jadex.bdiv3.testcases.plans;

import java.util.ArrayList;
import java.util.List;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.BDIConfiguration;
import jadex.bdiv3.annotation.BDIConfigurations;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.runtime.impl.RPlan;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentKilled;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;
import jadex.rules.eca.ChangeInfo;

/**
 *  Agent that tests plan waiting for fact added and removed.
 */
@Agent(type=BDIAgentFactory.TYPE)
@Results(@Result(name="testresults", clazz=Testcase.class))
@BDIConfigurations(@BDIConfiguration(name="def", initialplans=@NameValue(name="waitPlan")))
//@BDIConfigurations(@BDIConfiguration(name="def", initialplans=@NameValue(name="waitqueuePlan")))
public class WaitBDI
{
	@Agent
	protected IInternalAccess agent;
	
	@Belief
	protected List<String> names = new ArrayList<String>();
	
	protected TestReport[] tr = new TestReport[2];
	
	/**
	 *  The agent body.
	 */
	@AgentBody
	public void body()
	{
		tr[0] = new TestReport("#1", "Test waitForFactAdded");
		tr[1] = new TestReport("#2", "Test waitForFactRemoved");
		
		agent.getFeature(IExecutionFeature.class).waitForDelay(1000, new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				System.out.println("Adding");
				addName("a");
				
				agent.getFeature(IExecutionFeature.class).waitForDelay(1000, new IComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						System.out.println("Removing");
						removeName("a");
						
						agent.getFeature(IExecutionFeature.class).waitForDelay(1000, new IComponentStep<Void>()
						{
							public IFuture<Void> execute(IInternalAccess ia)
							{
								agent.killComponent();
								return IFuture.DONE;
							}
						});
						
						return IFuture.DONE;
					}
				});
				return IFuture.DONE;
			}
		});
	}
	
	/**
	 *  Called when agent is killed.
	 */
	@AgentKilled
	public void	destroy(IInternalAccess agent)
	{
		for(TestReport ter: tr)
		{
			if(!ter.isFinished())
				ter.setFailed("Plan not activated");
		}
		agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(tr.length, tr));
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
		
		final CounterResultListener<Void> lis = new CounterResultListener<Void>(2, new DelegationResultListener<Void>(ret));
		
		rplan.waitForFactAdded("names").addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<ChangeInfo<?>, Void>(ret)
		{
			public void customResultAvailable(ChangeInfo<?> result)
			{
				System.out.println("plan continues 1: "+rplan.getId()+" "+result);
				tr[0].setSucceeded(true);
				lis.resultAvailable(null);
			}
		}));
		
		rplan.waitForFactRemoved("names").addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<ChangeInfo<?>, Void>(ret)
		{
			public void customResultAvailable(ChangeInfo<?> result)
			{
				System.out.println("plan continues 2: "+rplan.getId()+" "+result);
				tr[1].setSucceeded(true);
				lis.resultAvailable(null);
			}
		}));
		
		return ret;
	}
	
//	/**
//	 *  Plan that waits for addition and removal of a name.
//	 */
//	@Plan
//	protected IFuture<Void> waitPlan(final RPlan rplan)
//	{
//		final Future<Void> ret = new Future<Void>();
//		System.out.println("plan waiting: "+rplan.getId());
//		
////		rplan.waitForCondition(null, new String[]{ChangeEvent.FACTADDED+".names"}).addResultListener(new DelegationResultListener<Void>(ret)
////		{
////			public void customResultAvailable(Void result)
////			{
////				System.out.println("continued");
////			}
////		});
//		
//		rplan.waitForFactAdded("names").addResultListener(agent.createResultListener(new ExceptionDelegationResultListener<Object, Void>(ret)
//		{
//			public void customResultAvailable(Object result)
//			{
//				System.out.println("plan continues 1: "+rplan.getId()+" "+result);
//				tr[0].setSucceeded(true);
//				rplan.waitForFactRemoved("names").addResultListener(agent.createResultListener(new ExceptionDelegationResultListener<Object, Void>(ret)
//				{
//					public void customResultAvailable(Object result)
//					{
//						System.out.println("plan continues 2: "+rplan.getId()+" "+result);
//						tr[1].setSucceeded(true);
//						ret.setResult(null);
//					}
//				}));
//			}
//			
//			public void exceptionOccurred(Exception exception)
//			{
//				exception.printStackTrace();
//			}
//		}));
//		
////		rplan.waitForFactAddedOrRemoved("names").addResultListener(new ExceptionDelegationResultListener<ChangeEvent, Void>(ret)
////		{
////			public void customResultAvailable(ChangeEvent result)
////			{
////				System.out.println("plan continues 1: "+rplan.getId()+" "+result);
////				rplan.waitForFactAddedOrRemoved("names").addResultListener(new ExceptionDelegationResultListener<ChangeEvent, Void>(ret)
////				{
////					public void customResultAvailable(ChangeEvent result)
////					{
////						System.out.println("plan continues 2: "+rplan.getId()+" "+result);
////						ret.setResult(null);
////					}
////				});
////			}
////		});
//		
//		return ret;
//	}
	
//	/**
//	 *  Plan that waits with waitqueue for addition of a name.
//	 */
//	@Plan(waitqueue=@Trigger(factadded="names"))
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