package jadex.bdiv3.tutorial.c2;

import java.util.HashMap;
import java.util.Map;

import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.runtime.ChangeEvent;
import jadex.bridge.service.annotation.Service;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Description;
import jadex.rules.eca.ChangeInfo;

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
	protected boolean alarm = wordtable.containsKey("bugger");
	
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
	public void checkWordPairPlan(ChangeEvent<ChangeInfo<Boolean>> event)
	{
		ChangeInfo<Boolean>	change	= event.getValue();
		// Print warning when value changes from false to true.
		if(Boolean.FALSE.equals(change.getOldValue()) && Boolean.TRUE.equals(change.getValue()))
		{
			System.out.println("Warning, a colloquial word pair has been added.");
		}
	}
}
