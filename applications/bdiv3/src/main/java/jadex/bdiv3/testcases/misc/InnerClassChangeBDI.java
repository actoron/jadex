package jadex.bdiv3.testcases.misc;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalAPI;
import jadex.bdiv3.annotation.GoalCreationCondition;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.runtime.IGoal;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentKilled;
import jadex.micro.annotation.Imports;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 *  Goal driven print out.
 *  
 *  class is checked for annotations
 *  goal, plan type declarations from annotations or inline plans 
 *  are added to the agent type and conditions to eca rule system 
 *  class is rewritten to announce belief changes (field accesses and annotated methods)
 */
@Agent
@Imports({"java.util.logging.*"})
@Properties({@NameValue(name="logging.level", value="Level.INFO")})
@Results(@Result(name="testresults", clazz=Testcase.class))
public class InnerClassChangeBDI
{
	/** The bdi agent. */
	@Agent
	protected IInternalAccess agent;
	
	/** The text that is printed. */
	@Belief
	private String sayhello = "first";
	
	/** The test report. */
	protected TestReport[] tr = new TestReport[]{new TestReport("#1", "Test first goal."), 
		new TestReport("#2", "Test second goal.")};
	
	/**
	 *  Simple hello world goal.
	 */
	@Goal
	public class HelloGoal
	{
		@GoalAPI
		protected IGoal goal;
		
		/** The text. */
		protected String text;
		
		/**
		 *  Create a new goal whenever sayhello belief is changed.
		 */
		@GoalCreationCondition//(beliefs="sayhello")
//		public HelloGoal(@Event("sayhello") String text)
		public HelloGoal(String text)
		{
			this.text = text;
			
			if("first".equals(sayhello))
				sayhello = "second";
		}
		
		/**
		 *  Get the text.
		 *  @return the text.
		 */
		public String getText()
		{
			return text;
		}
		
		/**
		 * 
		 */
		public IGoal getGoal()
		{
			return goal;
		}
	}
	
	/**
	 *  The plan body.
	 */
	@AgentBody
	public void body()
	{
		agent.getFeature(IExecutionFeature.class).waitForDelay(3000, new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				agent.killComponent();
				return IFuture.DONE;
			}
		});
	}
	
	/**
	 *  Called when agent is killed.
	 */
	@AgentKilled
	public void	destroy(IInternalAccess agent)
	{
		for(TestReport ter: tr)
		{
			if(!ter.isFinished())
				ter.setFailed("Plan not activated");
		}
		agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(tr.length, tr));
	}
	
	/**
	 *  First plan. Fails with exception.
	 */
	@Plan(trigger=@Trigger(goals=HelloGoal.class))
	protected IFuture<Void> printHello1(HelloGoal goal)
	{
		System.out.println(goal.getText());
		if(!tr[0].isFinished())
		{
			tr[0].setSucceeded(true);
		}
		else
		{
			tr[1].setSucceeded(true);
			agent.killComponent();
		}
		return IFuture.DONE;
	}
}

