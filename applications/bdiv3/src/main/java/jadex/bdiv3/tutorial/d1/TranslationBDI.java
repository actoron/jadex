package jadex.bdiv3.tutorial.d1;

import java.util.HashMap;
import java.util.Map;

import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bridge.IInternalAccess;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentFeature;
import jadex.micro.annotation.Description;

/**
 *  Translation agent D1.
 *  
 *  Using a translation goal.
 */
@Description("Translation agent d1. <br>  This translation agent uses a top level goal.")
@Agent(type=BDIAgentFactory.TYPE)
public class TranslationBDI
{
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	/** The bdi api. */
	@AgentFeature
	protected IBDIAgentFeature bdiFeature;
	
	/** The wordtable. */
	@Belief
	protected Map<String, String> wordtable;// = new HashMap<String, String>();

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
		this.wordtable = new HashMap<String, String>();
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
		Translate goal = (Translate) bdiFeature.dispatchTopLevelGoal(new Translate(eword)).get();
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
