package jadex.bdi.tutorial;

import jadex.base.fipa.SFipa;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.Plan;

import java.util.HashMap;
import java.util.Map;

/**
 *  An english german translation plan can translate
 *  english words to german and is instantiated on demand.
 */
public class EnglishGermanTranslationPlanB4 extends Plan
{
	//-------- constructors --------

	/**
	 *  Create a new plan.
	 */
	public EnglishGermanTranslationPlanB4()
	{
		getLogger().info("Created: "+this);
	}

	//-------- methods --------

	/**
	 *  Execute the plan.
	 */
	public void body()
	{
		String eword = (String)((IMessageEvent)getReason()).getParameter(SFipa.CONTENT).getValue();
		String gword = (String)wordtable.get(eword);
		//getLogger().info("Translating from english to german: "+eword+" - "+gword);
		System.out.println("Translating from english to german: "+eword+" - "+gword);
	}

	//-------- static part --------

	/** The wordtable. */
	protected static Map wordtable;

	static
	{
		wordtable = new HashMap();
		wordtable.put("coffee", "Kaffee");
		wordtable.put("milk", "Milch");
		wordtable.put("cow", "Kuh");
		wordtable.put("cat", "Katze");
		wordtable.put("dog", "Hund");
	}

	/**
	 *  Get the dictionary cache.
	 */
	public static boolean containsWord(String name)
	{
		return wordtable.containsKey(name);
	}
}
