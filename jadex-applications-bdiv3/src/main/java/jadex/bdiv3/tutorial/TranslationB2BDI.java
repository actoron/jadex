package jadex.bdiv3.tutorial;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.Goals;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Publish;
import jadex.bdiv3.annotation.Trigger;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;

import java.util.HashMap;
import java.util.Map;

/**
 *  The translation agent B2.
 *  
 *  BDI goal that is automatically published as service.
 */
@Agent
@Service
@Goals(@Goal(clazz=TranslationGoalB2.class, 
	publish=@Publish(type=ITranslationService.class, method="translateEnglishGerman")))
public class TranslationB2BDI 
{
	//-------- attributes --------

	@Agent
	protected BDIAgent agent;
	
	/** The wordtable. */
	protected Map<String, String> wordtable;

	//-------- methods --------

	/**
	 *  Create a new plan.
	 */
	@AgentBody
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
	
//	@Goal(publish=@Publish(type=ITranslationService.class, method="translateEnglishGerman"))
//	public class TranslateGoal
//	{
//		protected String gword;
//		protected String eword;
//
//		/**
//		 *  Create a new TranslateGoal. 
//		 */
//		public TranslateGoal(String eword)
//		{
//			this.eword = eword;
//		}
//
//		@GoalResult
//		public String getGWord()
//		{
//			return gword;
//		}
//		
//		public void setGWord(String gword)
//		{
//			this.gword = gword;
//		}
//		
//		public String getEWord()
//		{
//			return eword;
//		}
//		
//		public String getEword()
//		{
//			return eword;
//		}
//	}
	
	@Plan(trigger=@Trigger(goals=TranslationGoalB2.class))
	public void translatePlan(TranslationGoalB2 tg)
	{
		String eword = wordtable.get(tg.getEWord());
		tg.setGWord(eword);
	}
	
	/**
	 *  Translate an English word to German.
	 *  @param eword The english word.
	 *  @return The german translation.
	 */
	public IFuture<String> translateEnglishGerman(String eword)
	{
		String gword = wordtable.get(eword);
		return new Future<String>(gword);
	}
}
