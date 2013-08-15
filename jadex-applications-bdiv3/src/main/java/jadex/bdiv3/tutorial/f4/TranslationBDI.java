package jadex.bdiv3.tutorial.f4;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalResult;
import jadex.bdiv3.annotation.Goals;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Publish;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.tutorial.f4.TranslationBDI.TranslationGoal;
import jadex.bridge.service.annotation.Service;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;

import java.util.HashMap;
import java.util.Map;

/**
 *  The translation agent B5.
 *  
 *  BDI goal that is automatically published as service.
 */
@Agent
@Service
@Goals(@Goal(clazz=TranslationGoal.class, 
	publish=@Publish(type=ITranslationService.class, method="translateEnglishGerman")))
public class TranslationBDI 
{
	//-------- attributes --------

	@Agent
	protected BDIAgent agent;
	
	/** The wordtable. */
	protected Map<String, String> wordtable;

	//-------- methods --------

	/**
	 * 
	 */
	@Goal
	public class TranslationGoal
	{
		@GoalResult
		protected String gword;
		
		protected String eword;

		/**
		 *  Create a new TranslateGoal. 
		 */
		public TranslationGoal(String eword)
		{
			this.eword = eword;
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

		/**
		 *  Get the eword.
		 *  @return The eword.
		 */
		public String getEWord()
		{
			return eword;
		}

		/**
		 *  Set the eword.
		 *  @param eword The eword to set.
		 */
		public void setEWord(String eword)
		{
			this.eword = eword;
		}

	}
	
	/**
	 *  Create a new plan.
	 */
	@AgentCreated
	public void body()
	{
//		System.out.println("Created: "+this);
		this.wordtable = new HashMap<String, String>();
		this.wordtable.put("coffee", "Kaffee");
		this.wordtable.put("milk", "Milch");
		this.wordtable.put("cow", "Kuh");
		this.wordtable.put("cat", "Katze");
		this.wordtable.put("dog", "Hund");
	}
	
	/**
	 *  Plan reacts to the automatically created 
	 *  translation goal.
	 *  @param tg The translation goal.
	 */
	@Plan(trigger=@Trigger(goals=TranslationGoal.class))
	public void translatePlan(TranslationGoal tg)
	{
		String eword = wordtable.get(tg.getEWord());
		tg.setGWord(eword);
	}
}
