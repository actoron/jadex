package jadex.bdiv3.tutorial.d1;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bridge.service.annotation.Service;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Description;

import java.util.HashMap;
import java.util.Map;

/**
 *  Translation agent D1.
 *  
 *  Using a translation goal.
 */
@Description("Translation agent d1. <br>  This translation agent uses a subgoal.")
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
	@Goal
	public class Translate
	{
		/** The english word. */
		protected String eword;
		
		/** The german word. */
		protected String gword;
		
		/**
		 *  Create a new translate goal. 
		 */
		public Translate(String eword)
		{
			this.eword = eword;
		}

		/**
		 *  Get the eword.
		 *  @return The eword.
		 */
		public String getEWord()
		{
			return eword;
		}

		/**
		 *  Get the gword.
		 *  @return The gword.
		 */
		public String getGWord()
		{
			return gword;
		}

		/**
		 *  Set the gword.
		 *  @param gword The gword to set.
		 */
		public void setGWord(String gword)
		{
			this.gword = gword;
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
		Translate goal = (Translate)agent.dispatchTopLevelGoal(new Translate(eword)).get();
		System.out.println("Translated: "+eword+" "+goal.getGWord());
	}
	
	/**
	 *  Translate a word.
	 */
	@Plan(trigger=@Trigger(goals=Translate.class))
	protected void translate(Translate goal)
	{
		String eword = goal.getEWord();
		String gword = wordtable.get(eword);
		goal.setGWord(gword);
	}
	
}
