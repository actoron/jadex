package jadex.bdiv3.testcases.semiautomatic;

import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalAPI;
import jadex.bdiv3.annotation.GoalCreationCondition;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAPI;
import jadex.bdiv3.annotation.PlanAborted;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanFailed;
import jadex.bdiv3.annotation.PlanPassed;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3.runtime.IPlan;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Imports;
import jadex.micro.annotation.Properties;
import jadex.rules.eca.annotations.Event;

/**
 *  Hello World with goal driven print out.
 *  
 *  class is checked for annotations
 *  goal, plan type declarations from annotations or inline plans 
 *  are added to the agent type and conditions to eca rule system 
 *  class is rewritten to announce belief changes (field accesses and annotated methods)
 */
@Agent
@Imports({"java.util.logging.*"})
@Properties({@NameValue(name="logging.level", value="Level.INFO")})
public class HelloWorld2BDI
{
	/** The bdi agent. */
	@Agent
	protected IInternalAccess agent;
	
	/** The text that is printed. */
	@Belief
	private String sayhello;
	
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
		@GoalCreationCondition
		public HelloGoal(@Event("sayhello") String text)
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
		
		/**
		 * 
		 */
		public IGoal getGoal()
		{
			return goal;
		}
	}
	
	/**
	 *  The agent body.
	 *  
	 *  body is executed
	 *  changes variable value (sayhello=true)
	 *  notification is sent to eca rule system
 	 *  rule system finds creation condition of goal and executes it
	 *  right hand side creates goal and executes it
	 *  Plan is selected and executed (hello is printed out)
	 */
	@AgentBody
	public void body()
	{
		sayhello = "Hello BDI agent V3.";
		System.out.println("body end: "+getClass().getName());
	}
	
	/**
	 *  Hello world plan.
	 */
	@Plan(trigger=@Trigger(goals=HelloGoal.class))
	public class HelloWorldPlan
	{
		@PlanAPI
		protected IPlan plan;
		
		@PlanBody
		protected IFuture<Void> printHello1(final HelloGoal goal)
		{
			System.out.println("1: "+goal.getText());
			
			agent.getComponentFeature(IExecutionFeature.class).waitForDelay(3000, new IComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
				{
					goal.getGoal().drop();
					return IFuture.DONE;
				}
			});
			
			return waitLoop(0);
			
//			throw new RuntimeException();
//			return IFuture.DONE;
		}
		
		protected IFuture<Void> waitLoop(final int cnt)
		{
			final Future<Void> ret = new Future<Void>();
			if(cnt==100)
			{
				ret.setResult(null);
			}
			else
			{
				plan.waitFor(1000).addResultListener(new DelegationResultListener<Void>(ret)
				{
					public void customResultAvailable(Void result)
					{
						System.out.println("plan is waiting: "+HelloWorldPlan.this+" "+cnt);
						waitLoop(cnt+1).addResultListener(new DelegationResultListener<Void>(ret));
					}
				});
			}
			return ret;
		}
		
		@PlanPassed
		protected IFuture<Void> passed(HelloGoal goal)
		{
			System.out.println("Passed: "+HelloWorldPlan.this+" "+goal.getText());
			return IFuture.DONE;
		}
		
		@PlanFailed
		protected IFuture<Void> failed(HelloGoal goal)
		{
			System.out.println("Failed: "+HelloWorldPlan.this+" "+goal.getText());
			return IFuture.DONE;
		}
		
		@PlanAborted
		protected IFuture<Void> aborted(HelloGoal goal)
		{
			System.out.println("Aborted: "+HelloWorldPlan.this+" "+goal.getText());
			return IFuture.DONE;
		}
	}
}

