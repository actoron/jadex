package jadex.bdiv3.tutorial.d4;

import java.util.HashMap;
import java.util.Map;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalCreationCondition;
import jadex.bdiv3.annotation.GoalParameter;
import jadex.bdiv3.annotation.GoalResult;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bridge.service.annotation.Service;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentFeature;
import jadex.micro.annotation.Description;

/**
 *  Translation agent D3.
 *  
 *  Using a creation goal condition.
 */
@Description("Translation agent D4. <br>  This translation agent using a creation condition.")
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

	@Belief
	@SuppressFBWarnings(value="SA_FIELD_DOUBLE_ASSIGNMENT", justification="Field is a belief and agent reacts to changes automatically.")
	protected String eword;
	
	/**
	 *  The translation goal.
	 */
	@Goal
//	public static class Translate
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
		@GoalCreationCondition(beliefs="eword")
		public Translate(String eword)
		{
			this.eword = eword;
		}
		
//		/**
//		 *  Create a new goal.
//		 */
//		@GoalCreationCondition(events="eword")
//		public static Translate createGoal(String eword)
//		{
//			return new Translate(eword);
//		}
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
		eword = "cat";
		eword = "milk";
	}
	
	/**
	 *  Translate a word. Plan that translates.
	 */
	@Plan(trigger=@Trigger(goals=Translate.class))
	protected void translate(String eword)
	{
		System.out.println("Translated: "+eword+" "+wordtable.get(eword));
	}
}
