package jadex.bdiv3.example.helloworld;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.BDIAgentInterpreter;
import jadex.bdiv3.annotation.CreationCondition;
import jadex.bdiv3.annotation.Goal;
import jadex.rules.eca.IEvent;
import jadex.rules.eca.annotations.Action;
import jadex.rules.eca.annotations.Condition;
import jadex.rules.eca.annotations.Event;

@Goal 
public class HelloGoal
{
	protected String text;
	
	public HelloGoal(String text)
	{
		this.text = text;
	}
	
//	@CreationCondition()
	@Condition("creation")
	protected static boolean create(@Event("sayhello") boolean sayhello)
	{
		return sayhello;
	}
	
	@Action("creation")
	protected static void action(IEvent event, Object object)
	{
//		BDIAgentInterpreter ip = (BDIAgentInterpreter)agent.getInterpreter();
		BDIAgent agent = (BDIAgent)object;
		agent.adoptGoal(new HelloGoal("bla"));
	}
}