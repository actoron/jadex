package jadex.bdiv3.example.helloworld;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.BeliefCollection;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.runtime.ChangeEvent;
import jadex.bdiv3.runtime.PlanFailureException;
import jadex.bdiv3.runtime.RPlan;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
@Agent
public class BeliefListBDI
{
	@Agent
	protected BDIAgent agent;
	
	@BeliefCollection(implementation=ArrayList.class)
	private List<String> names;
	
	/**
	 * 
	 */
	@AgentBody
	public void body()
	{
		System.out.println("body start: "+this);
		names.add("a");
		names.add("b");
		names.add("c");
		System.out.println("body end: "+this);
	}
	
	// todo: plan creation condition?!
	@Plan(trigger=@Trigger(factaddeds="names"))
	protected void printAddedFact(ChangeEvent event, RPlan rplan)
	{
		System.out.println("fact added: "+event.getValue()+" "+event.getSource()+" "+rplan);
	}
}
