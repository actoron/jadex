package jadex.bdiv3.example.helloworld;

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
	
	/**
	 *  Get the text.
	 *  @return the text.
	 */
	public String getText()
	{
		return text;
	}

	//	@CreationCondition()
	@Condition("creation")
	protected static boolean create(@Event("sayhello") String sayhello)
	{
		return true;
	}
	
	@Action("creation")
	protected static void action(IEvent event, Object context)
	{
//		BDIAgentInterpreter ip = (BDIAgentInterpreter)context;
		HelloWorldBDI agent = (HelloWorldBDI)context;
		agent.getAgent().adoptGoal(new HelloGoal((String)event.getContent()));
	}
	
//	@TargetCondition()
//	@Condition("target")
//	protected boolean target(@Event("sayhello") String sayhello)
//	{
//		return true;
//	}
}