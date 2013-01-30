package jadex.agentkeeper.ai.creatures.troll;


import jadex.agentkeeper.ai.base.PatrolPlan;
import jadex.agentkeeper.ai.creatures.AbstractCreatureBDI;
import jadex.bdiv3.annotation.Body;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Plans;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.model.MGoal;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;


/**
 * The Troll
 * 
 * @author Philip Willuweit p.willuweit@gmx.de
 */

@Agent
@Plans({

@Plan(trigger=@Trigger(goals=TrollBDI.PerformPatrol.class), body=@Body(PatrolPlan.class))



})
public class TrollBDI extends AbstractCreatureBDI
{

	/**
	 * The agent body.
	 */
	@AgentBody
	@Override
	public void body()
	{
		agent.dispatchTopLevelGoal(new PerformPatrol());
//		agent.dispatchTopLevelGoal(new AchieveMoveToSector(new Vector2Int(9,18)));
	}
	
	/**
	 *  Goal that lets the ORC perform Patrols.
	 *  
	 */
	@Goal(excludemode=MGoal.EXCLUDE_NEVER, succeedonpassed=false)
	public class PerformPatrol
	{
		
	}
	
}
