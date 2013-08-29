package jadex.bdiv3.tutorial.c2;

import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bridge.service.annotation.Service;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Description;

import java.util.HashMap;
import java.util.Map;

/**
 *  Dynamic belief.
 */
@Description("The clock agent C2. <br>  This translation agent uses a belief with update rate.")
@Agent
@Service
public class TranslationBDI
{
	/** The current time. */
	@Belief
	protected Map<String, String> wordtable = new HashMap<String, String>();

	@Belief(dynamic=true)
	protected boolean alarm = wordtable.containsKey("burgler");
	
	//-------- methods --------

	@AgentCreated
	public void init()
	{
		wordtable.put("coffee", "Kaffee");
		wordtable.put("milk", "Milch");
		wordtable.put("cow", "Kuh");
		wordtable.put("cat", "Katze");
		wordtable.put("dog", "Hund");
		
		wordtable.put("bugger", "Flegel");
	}
	
	/**
	 *  Initiate an alarm.
	 */
	@Plan(trigger=@Trigger(factchangeds="alarm"))
	public void checkWordPairPlan()
	{
		System.out.println("Warning, a colloquial word pair has been added.");
	}
}
