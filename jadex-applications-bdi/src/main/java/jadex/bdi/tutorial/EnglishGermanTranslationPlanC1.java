package jadex.bdi.tutorial;

import jadex.base.fipa.SFipa;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.Plan;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;


/**
 *  An english german translation plan can translate
 *  english words to german and is instantiated on demand.
 */
public class EnglishGermanTranslationPlanC1 extends Plan
{
	//-------- constructors --------

	/**
	 *  Create a new plan.
	 */
	public EnglishGermanTranslationPlanC1()
	{
		getLogger().info("Created:"+this);
	}

	//-------- methods  --------

	/**
	 *  Do a plan step.
	 */
	public void body()
	{
		Map wordtable = (Map)getBeliefbase().getBelief("egwords").getFact();
		StringTokenizer stok = new StringTokenizer((String)((IMessageEvent)getReason())
			.getParameter(SFipa.CONTENT).getValue(), " ");
		if(stok.countTokens()==3)
		{
			stok.nextToken();
			stok.nextToken();
			String eword = stok.nextToken();
			String gword = (String)wordtable.get(eword);

			if(gword!=null)
			{
				getLogger().info("Translating from english to german: "+eword+" - "+gword);
			}
			else
			{
				getLogger().info("Sorry word is not in database: "+eword);
			}
		}
		else
		{
			getLogger().warning("Sorry format not correct.");
		}
	}

	protected static Map dictionary;
	/**
	 *  Get the dictionary.
	 *  @return The dictionary.
	 */
	public static Map getDictionary()
	{
		if(dictionary==null)
		{
			dictionary = new HashMap();
			dictionary.put("milk", "Milch");
			dictionary.put("cow", "Kuh");
			dictionary.put("cat", "Katze");
			dictionary.put("dog", "Hund");
		}
		return dictionary;
	}
}
