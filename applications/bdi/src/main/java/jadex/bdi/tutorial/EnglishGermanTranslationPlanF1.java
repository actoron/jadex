package jadex.bdi.tutorial;

import java.util.StringTokenizer;

import jadex.bdiv3x.runtime.IExpression;
import jadex.bdiv3x.runtime.IInternalEvent;
import jadex.bdiv3x.runtime.IMessageEvent;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.fipa.SFipa;

/**
 *  An english german translation plan can translate
 *  english words to german and is instantiated on demand.
 */
public class EnglishGermanTranslationPlanF1 extends Plan
{
	/**
	 *  Do a plan step.
	 */
	public void body()
	{
		 
		String	query	= "select one $wordpair.get(1) "
		//+"from Tuple $wordpair in $beliefbase.egwords "
		+"from Tuple $wordpair in $beliefbase.getBeliefSet(\"egwords\").getFacts() "
		+"where $wordpair.get(0).equals($eword)";

		IExpression	query_word = createExpression(query);
		
		StringTokenizer stok = new StringTokenizer((String)((IMessageEvent)getReason())
			.getParameter(SFipa.CONTENT).getValue(), " ");
		if(stok.countTokens()==3)
		{
			String action = stok.nextToken();
			String dir = stok.nextToken();
			String eword = stok.nextToken();
			String gword = (String)query_word.execute("$eword", eword);
			if(gword!=null)
			{
				getLogger().info("Translating from english to german: "+eword+" - "+gword);
			}
			else
			{
				System.out.println("Sorry word is not in database: "+eword);
			}
			IInternalEvent event = createInternalEvent("gui_update");
			event.getParameter("content").setValue(new String[]{action, dir, eword, gword});
			dispatchInternalEvent(event);
		}
		else
		{
			getLogger().warning("Sorry format not correct.");
		}
	}
}

