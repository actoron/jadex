package jadex.micro.testcases;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.base.test.impl.JunitAgentTest;
import jadex.bridge.IConditionalComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.OnStart;
import jadex.commons.future.FutureTerminatedException;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 *  Agent that tests repeat step.
 */
@Agent
@Results(@Result(name="testresults", clazz=Testcase.class))
public class RepeatStepAgent extends JunitAgentTest
{
	@Agent
	protected IInternalAccess agent;
	
	/**
	 *  Start the agent.
	 */
	@OnStart
	public IFuture<Void> start()
	{
		final TestReport tr1 = new TestReport("#1", "Repeat step with exception");
		final int[] cnt1 = new int[1];
		final ISubscriptionIntermediateFuture<Void> fut1 = agent.repeatStep(100, 100, (IInternalAccess ia) -> 
		{
			System.out.println("cnt: "+(cnt1[0]++));
			if(cnt1[0]>9)
				throw new RuntimeException();
			return IFuture.DONE;
		});
		
		try
		{
			fut1.get();
			tr1.setFailed("Should show exception");
		}
		catch(Exception e)
		{
			tr1.setSucceeded(true);
			//System.out.println(e);
		}
		
		
		final TestReport tr2 = new TestReport("#2", "Repeat step conditional step");
		final int[] cnt2 = new int[1];
		final ISubscriptionIntermediateFuture<Void> fut2 = agent.repeatStep(100, 100, new IConditionalComponentStep<Void>() 
		{
			public IFuture<Void> execute(IInternalAccess ia) 
			{
				System.out.println("cnt: "+(cnt2[0]++));
				return IFuture.DONE;
			}
			
			public boolean isValid() 
			{
				boolean ret = cnt2[0]<=9;
				//System.out.println("valid: "+ret);
				return ret;
			}
		});
				
		try
		{
			fut2.get();
			tr2.setSucceeded(true);
		}
		catch(Exception e)
		{
			tr2.setFailed("Exception: "+e);
			//System.out.println(e);
		}
		
		final TestReport tr3 = new TestReport("#3", "Repeat step with terminate");
		final int[] cnt3 = new int[1];
		final ISubscriptionIntermediateFuture<Void> fut3 = agent.repeatStep(100, 100, (IInternalAccess ia) -> 
		{
			System.out.println("cnt: "+(cnt3[0]++));
			return IFuture.DONE;
		});
		
		agent.waitForDelay(500).get();
		fut3.terminate();
		
		try
		{
			fut3.get();
			tr3.setFailed("Should show exception");
		}
		catch(FutureTerminatedException e)
		{
			tr3.setSucceeded(true);
			//System.out.println(e);
		}
		catch(Exception e)
		{
			tr3.setFailed("Exception: "+e);
		}
		
		//agent.getResults().put("testresults", new Testcase(1, new TestReport[]{tr3}));
		agent.getResults().put("testresults", new Testcase(3, new TestReport[]{tr1, tr2, tr3}));
		
		return IFuture.DONE;
	}
}

