package jadex.bdiv3.example.helloworld;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.BeliefCollection;
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
}
