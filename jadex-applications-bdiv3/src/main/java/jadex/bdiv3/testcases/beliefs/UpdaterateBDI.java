package jadex.bdiv3.testcases.beliefs;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.runtime.ChangeEvent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentKilled;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 *  Test beliefs with update rate.
 */
@Agent
@Results(@Result(name="testresults", clazz=Testcase.class))
public class UpdaterateBDI
{
	//-------- attributes --------
	
	/** The test report. */
	protected TestReport	tr	= new TestReport("#1", "Test if belief with updaterate is updated.");
	
	/** The counter. */
	protected int	cnt;
	
	/** The agent. */
	@Agent
	protected BDIAgent	agent;
	
	//-------- beliefs --------
	
	@Belief(updaterate=100)
	protected int	cntbel	= cnt++;
		
	//-------- constructors --------
	
	@AgentBody
	public void	body(BDIAgent agent)
	{
		agent.waitForDelay(1000).get();
		tr.setFailed("Plan was not triggered.");
		agent.killComponent();
	}
	
	@AgentKilled
	public void	destroy(BDIAgent agent)
	{
		agent.setResultValue("testresults", new Testcase(1, new TestReport[]{tr}));
	}
	
	//-------- plans --------
	
	@Plan(trigger=@Trigger(factchangeds={"cntbel"}))
	public void	beliefChanged(ChangeEvent event)
	{
		if(cntbel==5 && ((Integer)event.getValue()).intValue()==5)
		{
			tr.setSucceeded(true);
		}
		else if(cntbel>5 || ((Integer)event.getValue()).intValue()>5)
		{
			tr.setFailed("Inconsistent values: "+cntbel+", "+event.getValue());
		}
		
		if(tr.isFinished())
		{
			agent.killComponent();
		}
	}
}
