package jadex.bdiv3.testcases.semiautomatic;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.runtime.ChangeEvent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;

/**
 *  Agent that has two beliefs. 
 *  num2 belief depends on num1 and a plan depends 
 *   
 */
@Agent
public class DynamicBeliefBDI
{
	@Agent
	protected BDIAgent agent;
	
	@Belief
	protected int num1 = 1;
	
	@Belief(dynamic="num1")
	protected int num2 = num1+1;
	
	@Plan(trigger=@Trigger(factchangeds="num2"))
	protected void successPlan(ChangeEvent event)
	{
		System.out.println("plan activated: num2 changed to "+event.getValue());
	}

	@AgentBody
	public void body()
	{
		num1++;
		num1++;
	}
}


