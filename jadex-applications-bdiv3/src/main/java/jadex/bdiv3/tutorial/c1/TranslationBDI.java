package jadex.bdiv3.tutorial.c1;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bridge.service.annotation.Service;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Description;

import java.util.Map;

/**
 *  The translation agent C1.
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
//		System.out.println("Created: "+this);
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
		wordtable.put("pig", "Schwein");
		wordtable.put("bugger", "Flegel");
	}
	
	/**
	 *  Add a new word pair to the dictionary.
	 */
	@Plan(trigger=@Trigger(factaddeds="wordtable"))
	public void checkWordPairPlan(Map.Entry<String, String> wordpair)
	{
		if(wordpair.getKey().equals("bugger"))
			System.out.println("Warning, a colloquial word pair has been added: "+wordpair.getKey()+" "+wordpair.getValue());
	}
}

