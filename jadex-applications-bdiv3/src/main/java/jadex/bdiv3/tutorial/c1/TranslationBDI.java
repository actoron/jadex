package jadex.bdiv3.tutorial.c1;

import java.util.HashMap;
import java.util.Map;

import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.runtime.ChangeEvent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Description;
import jadex.rules.eca.ChangeInfo;

/**
 *  The translation agent C1. (Belief that triggers plan)
 *  
 *  This translation agent allows for adding word pairs to extend
 *  its dictionary.
 */
@Description("The translation agent C1. <br>  This translation agent allows for adding word pairs to extend its dictionary.")
@Agent
public class TranslationBDI
{
	//-------- attributes --------

//	@Agent
//	protected BDIAgent agent;
	
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
	
//	/**
//	 *  Add a new word pair to the dictionary.
//	 */
//	@Plan(trigger=@Trigger(factaddeds="wordtable"))
//	public void checkWordPairPlan(Map.Entry<String, String> wordpair)
//	{
//		if(wordpair.getKey().equals("bugger"))
//			System.out.println("Warning, a colloquial word pair has been added: "+wordpair.getKey()+" "+wordpair.getValue());
//	}
	
	/**
	 *  Add a new word pair to the dictionary.
	 */
	@Plan(trigger=@Trigger(factaddeds="wordtable"))
	public void checkWordPairPlan(ChangeEvent<ChangeInfo<String>> event)
	{
		ChangeInfo<String>	change	= event.getValue();
		
		if(change.getInfo().equals("bugger"))
			System.out.println("Warning, a colloquial word pair has been added: "+change.getInfo()+" "+change.getValue());
	}
}

