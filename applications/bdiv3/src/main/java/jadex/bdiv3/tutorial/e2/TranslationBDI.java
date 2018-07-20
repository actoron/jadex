package jadex.bdiv3.tutorial.e2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Capability;
import jadex.bdiv3.annotation.Mapping;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.runtime.ChangeEvent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentFeature;

/**
 *  The translation agent e1.
 *  
 *  Using a capability.
 */
@Agent(type=BDIAgentFactory.TYPE)
public class TranslationBDI
{
	/** The bdi feature. */
	@AgentFeature
	protected IBDIAgentFeature bdiFeature;
	
	@Capability(beliefmapping=@Mapping(value="wordtable"))
	protected TranslationCapability capa = new TranslationCapability();
	
	/** The map of words. */
	@Belief
	protected Map<String, String> wordtable = new HashMap<String, String>();

	/**
	 *  The init code.
	 */
	@AgentCreated
	public void init()
	{
		wordtable.put("coffee", "Kaffee");
		wordtable.put("milk", "Milch");
		wordtable.put("cow", "Kuh");
		wordtable.put("cat", "Katze");
		wordtable.put("dog", "Hund");
		wordtable.put("puppy", "Hund");
		wordtable.put("hound", "Hund");
		wordtable.put("jack", "Katze");
		wordtable.put("crummie", "Kuh");
	}
	
	/**
	 *  The agent body.
	 */
	@AgentBody
	public void body()
	{
		String eword = "dog";
		String gword = (String)bdiFeature.dispatchTopLevelGoal(capa.new Translate(eword)).get();
		System.out.println("Translated: "+eword+" "+gword);

		List<String> syns = (List<String>)bdiFeature.adoptPlan("findSynonyms", new Object[]{eword}).get();
		System.out.println("Found synonyms: "+eword+" "+syns);
	}
	
	/**
	 *  Find a synonym.
	 */
	@Plan
//	protected List<String> findSynonyms(String eword)
	protected List<String> findSynonyms(ChangeEvent<Object[]> ev)
	{
		String eword = (String)ev.getValue()[0];
		List<String> ret = new ArrayList<String>();
		String gword = wordtable.get(eword);
		for(String key: wordtable.keySet())
		{
			if(wordtable.get(key).equals(gword))
			{
				ret.add(key);
			}
		}
		return ret;
	}
}
