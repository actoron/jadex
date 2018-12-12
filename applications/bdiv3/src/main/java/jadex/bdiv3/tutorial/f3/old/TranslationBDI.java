package jadex.bdiv3.tutorial.f3.old;

import java.util.HashMap;
import java.util.Map;

import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.Goals;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Publish;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.tutorial.f3.TranslationGoal;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;

/**
 *  The translation agent B5.
 *  
 *  BDI goal that is automatically published as service.
 */
@Agent(type=BDIAgentFactory.TYPE)
@Service
@Goals(@Goal(clazz=TranslationGoal.class, 
	publish=@Publish(type=ITranslationService.class, method="translateEnglishGerman")))
public class TranslationBDI 
{
	//-------- attributes --------

	@Agent
	protected IInternalAccess agent;
	
	/** The wordtable. */
	protected Map<String, String> wordtable;

	//-------- methods --------

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
