package jadex.bdiv3.tutorial.c4;

import java.util.HashMap;
import java.util.Map;

import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.Belief;
import jadex.bridge.service.annotation.Service;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Description;

/**
 *  Translation agent C4.
 *  
 *  Argument mapped to a field.
 */
@Description("The clock agent C4. <br>  This translation agent uses an argument.")
@Agent(type=BDIAgentFactory.TYPE)
@Service
@Arguments({
	@Argument(name="wordpair", clazz=String[].class,  defaultvalue="new String[]{\"bugger\", \"Flegel\"}")//,
	//@Argument(name="test", clazz=int.class,  defaultvalue="23")
})
public class TranslationBDI
{
	/** The current time. */
	@Belief
	protected Map<String, String> wordtable = new HashMap<String, String>();

	@AgentArgument
	protected String[] wordpair;
	
	//-------- methods --------

	@AgentCreated
	public void init()
	{
		wordtable.put("coffee", "Kaffee");
		wordtable.put("milk", "Milch");
		wordtable.put("cow", "Kuh");
		wordtable.put("cat", "Katze");
		wordtable.put("dog", "Hund");
		
		wordtable.put(wordpair[0], wordpair[1]);
		
		System.out.println("dictionary is: "+wordtable);
	}
}
