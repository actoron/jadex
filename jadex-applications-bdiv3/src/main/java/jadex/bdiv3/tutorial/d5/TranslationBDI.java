package jadex.bdiv3.tutorial.d5;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalParameter;
import jadex.bdiv3.annotation.GoalRecurCondition;
import jadex.bdiv3.annotation.GoalResult;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.runtime.impl.PlanFailureException;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Description;

import java.util.HashMap;
import java.util.Map;

/**
 *  Translation agent D5.
 *  
 *  Using a goal with recur condition.
 */
@Description("Translation agent D5. <br>  This translation agent using translate goal with recur condition.")
@Agent
@Service
public class TranslationBDI
{
	/** The agent. */
	@Agent
	protected BDIAgent agent;
	
	/** The current time. */
	@Belief
	protected Map<String, String> wordtable = new HashMap<String, String>();

	/**
	 *  The translation goal.
	 */
	@Goal(recur=true)
	public class Translate
	{
		/** The english word. */
		@GoalParameter
		protected String eword;
		
		/** The german word. */
		@GoalResult
		protected String gword;
		
		/**
		 *  Create a new translate goal. 
		 */
		public Translate(String eword)
		{
			this.eword = eword;
		}
		
		/**
		 *  Check if goal should be retried.
		 */
		@GoalRecurCondition(events="wordtable")
		public boolean checkRecur()
		{
			return true;
		}
	}
	
	//-------- methods --------
	
	@AgentCreated
	public void init()
	{
		wordtable.put("coffee", "Kaffee");
		wordtable.put("milk", "Milch");
		wordtable.put("cow", "Kuh");
		wordtable.put("cat", "Katze");
		wordtable.put("dog", "Hund");
	}
	
	/**
	 *  The agent body.
	 */
	@AgentBody
	public void body()
	{
		// Add a new wordpair after a few seconds
		agent.scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				wordtable.put("bugger", "Flegel");
				return IFuture.DONE;
			}
		}, 3000);
		
		String eword = "bugger";
		String gword = (String)agent.dispatchTopLevelGoal(new Translate(eword)).get();
		System.out.println("Translated: "+eword+" "+gword);
	}
	
	/**
	 *  Translate a word.
	 */
	@Plan(trigger=@Trigger(goals=Translate.class))
	protected String translate(String eword)
	{
		String ret = wordtable.get(eword);
		if(ret==null)
			throw new PlanFailureException();
		return ret;
	}
}
