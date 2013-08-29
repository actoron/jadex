package jadex.bdiv3.tutorial.d2;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalParameter;
import jadex.bdiv3.annotation.GoalResult;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bridge.service.annotation.Service;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Description;

import java.util.HashMap;
import java.util.Map;

/**
 *  Translation agent D2.
 *  
 *  Using parameters and results.
 */
@Description("Translation agent D2. <br>  This translation agent uses a subgoal.")
@Agent
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
	@Goal
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
	 * 
	 */
	@AgentBody
	public void body()
	{
		String eword = "cat";
		String gword = (String)agent.dispatchTopLevelGoal(new Translate(eword)).get();
		System.out.println("Translated: "+eword+" "+gword);
	}
	
	/**
	 *  Translate a word.
	 */
	@Plan(trigger=@Trigger(goals=Translate.class))
	protected String translate(String eword)
	{
		return wordtable.get(eword);
	}
	
}
