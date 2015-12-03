package jadex.bdi.tutorial;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import jadex.bdiv3x.runtime.IMessageEvent;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.fipa.SFipa;


/**
 *  An english german translation plan can translate
 *  english words to german and is instantiated on demand.
 */
public class EnglishGermanTranslationPlanC1 extends Plan
{
	//-------- methods  --------

	/**
	 *  Do a plan step.
	 */
	public void body()
	{
		Map<String, String> wordtable = (Map<String, String>)getBeliefbase().getBelief("egwords").getFact();
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

	protected static Map<String, String> dictionary;
	/**
	 *  Get the dictionary.
	 *  @return The dictionary.
	 */
	public static Map<String, String> getDictionary()
	{
		if(dictionary==null)
		{
			Map<String, String>	m = new HashMap<String, String>();
			m.put("milk", "Milch");
			m.put("cow", "Kuh");
			m.put("cat", "Katze");
			m.put("dog", "Hund");
			dictionary	= m;
		}
		return dictionary;
	}
}
