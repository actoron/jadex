package jadex.bdiv3.examples.hellopure;

import jadex.base.PlatformConfigurationHandler;
import jadex.base.Starter;
import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalCreationCondition;
import jadex.bdiv3.annotation.GoalParameter;
import jadex.bdiv3.annotation.GoalTargetCondition;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.runtime.BDIAgent;
import jadex.bdiv3.runtime.BDIGoal;
import jadex.bdiv3.runtime.impl.PlanFailureException;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.annotation.OnStart;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;

/**
 *  BDI agent that uses belief to trigger goal and execute plans.
 *  The goal has a target condition based on the state of its 'text' parameter.
 *  
 *  Pure BDI agent that is not bytecode enhanced. 
 *  This is achieved by using the baseclass BDIAgent that signals enhancement
 *  has already been done.
 */
@Agent(type=BDIAgentFactory.TYPE)
public class HelloPureGoalAgent extends BDIAgent
{
	@Belief
	private String sayhello;
	
	@Goal
	public class HelloGoal extends BDIGoal
	{
		@GoalParameter
		protected String text;
		
		@GoalCreationCondition(beliefs="sayhello")
		public HelloGoal(String text) 
		{
			setText(text);
		}
		
		@GoalTargetCondition(parameters="text")
		public boolean checkTarget()
		{
			//System.out.println("checkTarget: "+text);
			return "finished".equals(text);
		}
		
		public String getText()
		{
			return text;
		}
		
		public void setText(String val)
		{
			setParameterValue("text", val);
		}
	}
	
	@OnStart
	public void body()
	{		
		//sayhello = "Hello BDI pure agent V3.";
		//beliefChanged("sayhello", null, sayhello);
		setBeliefValue("sayhello", "Hello BDI pure agent V3.");
		System.out.println("body end: "+getClass().getName());
	}
	
	@Plan(trigger=@Trigger(goals=HelloGoal.class))
	protected IFuture<Void> printHello1(HelloGoal goal)
	{
		System.out.println("1: "+goal.getText());
		throw new PlanFailureException();
	}
	
	@Plan(trigger=@Trigger(goals=HelloGoal.class))
	protected void printHello2(HelloGoal goal)
	{
		goal.setText("finished");
		System.out.println("2: "+goal.getText());
		//return new Future<Void>(new PlanFailureException());
	}
	
	@Plan(trigger=@Trigger(goals=HelloGoal.class))
	protected IFuture<Void> printHello3(HelloGoal goal)
	{
		System.out.println("3: "+goal.getText());
		return IFuture.DONE;
	}
	
	/**
	 *  Start a platform and the example.
	 */
	public static void main(String[] args) 
	{
		IExternalAccess platform = Starter.createPlatform(PlatformConfigurationHandler.getDefaultNoGui()).get();
		CreationInfo ci = new CreationInfo().setFilenameClass(HelloPureGoalAgent.class);
		platform.createComponent(ci).get();
	}
}
