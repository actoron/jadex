package jadex.bdiv3.example.helloworld;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;

@Agent
//@Goals(HelloGoal.class) // todo: type declarations for non-inline goals and plans to be able to setup conditions

// class is checked for annotations
// goal, plan type declarations from annotations or inline plans 
// are added to the agent type and conditions to eca rule system 
// class is rewritten to announce belief changes (field accesses and annotated methods)

// body is executed
// changes variable value (sayhello=true)
// notification is sent to eca rule system
// rule system finds creation condition of goal and executes it
// right hand side creates goal and executes it
// Plan is selected and executed (hello is printed out)
public class HelloWorldBDI
{
	@Agent
	protected BDIAgent agent;
	
	@Belief
	protected String text;
	
	@Belief
	protected boolean sayhello;
	
//	public HelloWorldBDI()
//	{
//		System.out.println("created: "+getClass().getName());
//	}
	
	@AgentBody
	public void body()
	{
		sayhello = true;
		System.out.println("body end");
		
//		HelloGoal g = new HelloGoal(text);
//		System.out.println("Body: "+agent.getComponentIdentifier());
	}
	
	@Plan(trigger=@Trigger(goals=HelloGoal.class))
	protected void printHello()
	{
//		System.out.println(((HelloGoal)getReason()).text);
		System.out.println(text);
	}
}
