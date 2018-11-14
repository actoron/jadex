package jadex.bdiv3.tutorial.d3;

import java.util.HashMap;
import java.util.Map;

import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalParameter;
import jadex.bdiv3.annotation.GoalResult;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.runtime.impl.PlanFailureException;
import jadex.bridge.service.annotation.Service;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentFeature;
import jadex.micro.annotation.Description;

/**
 *  Translation agent D3.
 *  
 *  Using goal retry.
 */
@Description("Translation agent D3. <br>  Using goal retry.")
@Agent(type=BDIAgentFactory.TYPE)
@Service
public class TranslationBDI
{
	/** The BDI feature. */
	@AgentFeature
	protected IBDIAgentFeature bdiFeature;

	/** The current time. */
	@Belief
	protected Map<String, String> wordtable = new HashMap<String, String>();

	/**
	 *  The translation goal.
	 */
	@Goal//(retry=false)
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
		String gword = (String)bdiFeature.dispatchTopLevelGoal(new Translate(eword)).get();
		System.out.println("Translated: "+eword+" "+gword);
	}
	
	/**
	 *  Translate a word. Plan that fails.
	 */
	@Plan(trigger=@Trigger(goals=Translate.class))
	protected String translateA(String eword)
	{
		System.out.println("Plan A");
		throw new PlanFailureException();
	}
	
	/**
	 *  Translate a word. Plan that translates.
	 */
	@Plan(trigger=@Trigger(goals=Translate.class))
	protected String translateB(String eword)
	{
		System.out.println("Plan B");
		return wordtable.get(eword);
	}
}
