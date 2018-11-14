package jadex.bdiv3.testcases.semiautomatic;

import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalCreationCondition;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.runtime.ChangeEvent;
import jadex.bridge.IInternalAccess;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentCreated;
import jadex.rules.eca.ChangeInfo;

/**
 *   (2017/03/08) https://sourceforge.net/p/jadex/discussion/274112/thread/1e465814/
 *   
 *   Hello,
 *   I wrote an agent that was triggering self regulating plans based on a belief
 *   (ie. @Trigger(factchanged="belief")). then I tried using this same belief
 *   in a goal condition (@GoalCreationCondition).
 *   When I ran the agent with the Goal implemented, the plans associated with triggers
 *   senstive to this belief would not execute. I created a test case Agent to observe this behaviour.
 *   
 *   I have implemented a belief called number which triggers the numberChangedPlan plan with the
 *   factchanged propertyListener in the trigger.
 *   
 *   I have also created a dummy goal GenericGoal which uses the belief number
 *   in the @GoalCreationCondition. If you run the agent with the goal present
 *   in the agent description class, then the numberChangedPlan will not be triggered.
 *   with the Goal commented out, numberChangedPlan is triggered.
 *   
 *   Is this expected behaviour?
 *   
 *   -> problem was method not being static
 */
@Agent(type=BDIAgentFactory.TYPE)
public class SelectiveBeliefChangeBDI
{
	@Belief
	protected double number;

	@AgentCreated
	public void init(IInternalAccess agent)
	{
		number = 9.2;
	}

	@AgentBody
	public void body(IInternalAccess agent)
	{
		number = 9.4;
		number = 29.5;
	}

	@Plan(trigger = @Trigger(factchanged = "number"))
	public void numberChangedPlan(ChangeEvent event)
	{
		ChangeInfo<Double> change = (ChangeInfo<Double>)event.getValue();
		System.out.println("number has changed to:  " + change.getValue() + " from " + change.getOldValue());
	}

	@Goal
	public static class GenericGoal
	{
		protected double	num1;

		protected double	result;

		@GoalCreationCondition
		public static boolean preCheckNumber(SelectiveBeliefChangeBDI agent)
		{
			System.out.println("checking: "+agent.number);
			return agent.number > 20.0;
		}

		public GenericGoal(SelectiveBeliefChangeBDI agent)
		{
			this.num1 = agent.number;
			System.out.println("created goal: "+num1);
		}
	}
}