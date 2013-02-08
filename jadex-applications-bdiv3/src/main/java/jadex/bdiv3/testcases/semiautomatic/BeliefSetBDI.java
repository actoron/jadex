package jadex.bdiv3.testcases.semiautomatic;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.runtime.ChangeEvent;
import jadex.bdiv3.runtime.impl.RPlan;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;

import java.util.Set;

/**
 * 
 */
@Agent
public class BeliefSetBDI
{
	@Agent
	protected BDIAgent agent;
	
	@Belief
	protected Set<String> names;
	
	/**
	 * 
	 */
	@AgentBody
	public void body()
	{
		names.add("a");
		names.add("b");
		names.add("a");
	}
	
	// todo: plan creation condition?!
	@Plan(trigger=@Trigger(factaddeds="names"))
	protected void printAddedFact(ChangeEvent event, RPlan rplan)
	{
		System.out.println("fact added: "+event.getValue()+" "+event.getSource()+" "+rplan);
	}
}
