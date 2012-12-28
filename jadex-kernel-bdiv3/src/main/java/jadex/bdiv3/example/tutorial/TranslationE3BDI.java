package jadex.bdiv3.example.tutorial;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalMaintainCondition;
import jadex.bdiv3.annotation.GoalTargetCondition;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.model.MGoal;
import jadex.bdiv3.runtime.ChangeEvent;
import jadex.bdiv3.runtime.RPlan;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.SUtil;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.rules.eca.annotations.Event;

import java.util.Map;

/**
 *  The translation agent E3 in V3.
 */
@Agent
public class TranslationE3BDI
{
	/** The injected agent. */
	@Agent
	protected BDIAgent agent;
	
	/** The map of words. */
	@Belief
	protected Map<String, String> egwords;
	
	/** The max value of entries allowed in the map. */
	@Belief
	protected int maxstorage = 4;
	
	/**
	 *  Maintain goal that ensures that only maxstorage
	 *  number of entries are in the table egwords.
	 */
	@Goal(excludemode=MGoal.EXCLUDE_NEVER)
	public class MaintainStorageGoal
	{
		@GoalMaintainCondition
		protected boolean maintain(@Event("egwords") Object event)
		{
//			System.out.println("check maintain: "+egwords.size()+" "+(egwords.size()<=maxstorage));
			return egwords.size()<=maxstorage;
		}
		
		@GoalTargetCondition
		protected boolean target(@Event("egwords") Object event)
		{
//			System.out.println("check target: "+egwords.size()+" "+event);
			return egwords.size()<3;
		}
	}
	
	/**
	 *  The agent body.
	 */
	@AgentBody
	public void body()
	{
		agent.dispatchGoalAndWait(new MaintainStorageGoal());

		egwords.put("milk", "Milch");
		egwords.put("cow", "Kuh");
		egwords.put("cat", "Katze");
		egwords.put("dog", "Hund");

		IComponentStep<Void> step = new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				String rand = SUtil.createUniqueId("abc_", 3);
				egwords.put(rand, rand);
//				System.out.println("added: "+rand);
				System.out.println("egwords: "+egwords);
				agent.waitFor(2000, this);
				return IFuture.DONE;
			}
		};
		
		agent.waitFor(2000, step);
	}
	
	/**
	 *  Removal plan that remove one entry.
	 */
	@Plan(trigger=@Trigger(goals=MaintainStorageGoal.class))
	protected void removeEntry(ChangeEvent event, RPlan rplan)
	{
		String key = egwords.keySet().iterator().next();
		String val = egwords.remove(key);
//		System.out.println("removed: "+key+" "+val+" "+egwords);
	}
}
