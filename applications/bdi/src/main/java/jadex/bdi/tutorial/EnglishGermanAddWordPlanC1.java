package jadex.bdi.tutorial;

import java.util.Map;
import java.util.StringTokenizer;

import jadex.bdiv3x.runtime.IMessageEvent;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.fipa.SFipa;

/**
 *  Add a english - german word pair to the wordtable..
 */
public class EnglishGermanAddWordPlanC1 extends Plan
{
	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		Map<String, String> wordtable = (Map<String, String>)getBeliefbase().getBelief("egwords").getFact();
		StringTokenizer stok = new StringTokenizer((String)((IMessageEvent)getReason())
			.getParameter(SFipa.CONTENT).getValue(), " ");
		if(stok.countTokens()==4)
		{
			stok.nextToken();
			stok.nextToken();
			String eword = stok.nextToken();
			String gword = stok.nextToken();
			if(!wordtable.containsKey(eword))
			{
				wordtable.put(eword, gword);
				getBeliefbase().getBelief("egwords").setFact(wordtable);
				getLogger().info("Added  new wordpair to database: "+eword+" - "+gword);
			}
			else
			{
				getLogger().info("Sorry database already contains word: "+eword);
			}
		}
		else
		{
			getLogger().warning("Sorry format not correct.");
		}
	}
}
