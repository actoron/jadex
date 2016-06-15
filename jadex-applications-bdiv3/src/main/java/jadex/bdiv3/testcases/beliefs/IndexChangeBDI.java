package jadex.bdiv3.testcases.beliefs;

import java.util.Arrays;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bdiv3.IBDIAgent;
import jadex.bdiv3.annotation.BDIConfiguration;
import jadex.bdiv3.annotation.BDIConfigurations;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.runtime.IPlan;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 *  Agent that tests if waiting for a specific index change in a collection works.
 */
@Results(@Result(name="testresults", clazz=Testcase.class))
@Agent
@BDIConfigurations(@BDIConfiguration(name="def", initialplans=@NameValue(name="waitPlan")))
public abstract class IndexChangeBDI implements IBDIAgent
{
	@Belief
	protected boolean[] guards = new boolean[2];

	@Plan
	protected void waitPlan(IPlan plan)
	{
//		System.out.println("plan started");
		
		final TestReport tr = new TestReport("#1", "Test if waiting for an specific index works.");

		final int[] cnt = new int[1];
		getExternalAccess().scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				guards[cnt[0]] = true;
				System.out.println("now is: "+Arrays.toString(guards));
				if(++cnt[0]<guards.length)
				{
					getExternalAccess().scheduleStep(this, 1000);
				}
				return IFuture.DONE;
			}
		}, 1000);
		
		try
		{
//			plan.waitForCollectionChange("guards", 3000, new IFilter<ChangeInfo<Boolean>>()
//			{
//				public boolean filter(ChangeInfo<Boolean> info) 
//				{
//					boolean ret = false;
//					if(info.getInfo()!=null)
//					{
//						int idx = (Integer)info.getInfo();
//						ret = idx==1;
//					}
//					return ret;
//				}
//			}).get();
			plan.waitForCollectionChange("guards", 3000, Integer.valueOf(1)).get();
			tr.setSucceeded(true);
		}
		catch(Exception e)
		{
			tr.setReason("Exception occurred: "+e.getMessage());
		}
		
		getComponentFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(1, new TestReport[]{tr}));
		
		killComponent();
		
//		System.out.println("plan finished");
	}
}
