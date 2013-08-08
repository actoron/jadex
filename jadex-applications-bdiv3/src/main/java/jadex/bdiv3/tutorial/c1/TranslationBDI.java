package jadex.bdiv3.tutorial.c1;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.runtime.ChangeEvent;
import jadex.bridge.service.annotation.Service;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Description;

import java.util.HashMap;
import java.util.Map;

/**
 *  The translation agent C1. (Belief that triggers plan)
 *  
 *  This translation agent allows for adding word pairs to extend
 *  its dictionary.
 */
@Description("The translation agent C1. <br>  This translation agent allows for adding word pairs to extend its dictionary.")
@Agent
@Service
public class TranslationBDI
{
	//-------- attributes --------

	@Agent
	protected BDIAgent agent;
	
	/** The wordtable. */
	@Belief
	protected Map<String, String> wordtable;

	//-------- methods --------

	@AgentCreated
	public void init()
	{
		// Do not create the map here (only as initial assignment)
		this.wordtable = new HashMap<String, String>();
		
		wordtable.put("coffee", "Kaffee");
		wordtable.put("milk", "Milch");
		wordtable.put("cow", "Kuh");
		wordtable.put("cat", "Katze");
		wordtable.put("dog", "Hund");
		
		wordtable.put("bugger", "Flegel");
	}
	
	/**
	 *  Add a new word pair to the dictionary.
	 */
	@Plan(trigger=@Trigger(factaddeds="wordtable"))
//	public void checkWordPairPlan(Map.Entry<String, String> wordpair)
	public void checkWordPairPlan(ChangeEvent event)
	{
		Map.Entry<String, String> wordpair = (Map.Entry<String, String>)event.getValue();
		if(wordpair.getKey().equals("bugger"))
			System.out.println("Warning, a colloquial word pair has been added: "+wordpair.getKey()+" "+wordpair.getValue());
	}
}

