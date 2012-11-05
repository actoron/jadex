package jadex.bdiv3.example.helloworld;

import jadex.bdiv3.annotation.Goal;
import jadex.rules.eca.IEvent;
import jadex.rules.eca.IRule;
import jadex.rules.eca.annotations.Action;
import jadex.rules.eca.annotations.Condition;
import jadex.rules.eca.annotations.Event;

@Goal 
//@GoalDeliberation(cardinality=1, inhibits={
//	@Inhibits(GoodByeGoal.class),
//	@Inhibits(value=HelloAgainGoal.class, when=Inhibits.WHEN_IN_PROCESS, expression=???)
//})
public class HelloGoal
{
//	@GoalParameter???(unique=true, bindingoptions=???, assignto=???)
	protected String text;
	
//	@GoalCreation
//	public HelloGoal(@Event("sayhello") String text)
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

	//	@GoalCreation()
	@Condition("creation")
	protected static boolean create(@Event("sayhello") String sayhello)
	{
		return true;
	}
	
	@Action("creation")
	protected static void action(IEvent event, IRule rule, Object context)
	{
//		BDIAgentInterpreter ip = (BDIAgentInterpreter)context;
		HelloWorldBDI agent = (HelloWorldBDI)context;
		agent.getAgent().adoptGoal(new HelloGoal((String)event.getContent()));
	}
	
//	@GoalContext / @GoalDrop / @GoalRecur
//	event???
//	public boolean	isValid()
//	{
//		return text!=null;
//	}
	
//	@TargetCondition()
//	@Condition("target")
//	protected boolean target(@Event("sayhello") String sayhello)
//	{
//		return true;
//	}
}