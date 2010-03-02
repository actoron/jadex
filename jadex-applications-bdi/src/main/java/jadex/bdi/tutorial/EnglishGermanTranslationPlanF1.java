package jadex.bdi.tutorial;

import jadex.base.fipa.SFipa;
import jadex.bdi.runtime.IExpression;
import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.Plan;

import java.util.StringTokenizer;

/**
 *  An english german translation plan can translate
 *  english words to german and is instantiated on demand.
 */
public class EnglishGermanTranslationPlanF1 extends Plan
{
	//-------- attributes --------

	/** Query the tuples for a word. */
	protected IExpression	query_word;

	//-------- constructors --------

	/**
	 *  Create a new plan.
	 */
	public EnglishGermanTranslationPlanF1()
	{
		getLogger().info("Created:"+this);

		// Create precompiled queries.
		String	query	= "select one $wordpair.get(1) "
			//+"from Tuple $wordpair in $beliefbase.egwords "
			+"from Tuple $wordpair in $beliefbase.getBeliefSet(\"egwords\").getFacts() "
			+"where $wordpair.get(0).equals($eword)";

		this.query_word	= createExpression(query);
	}

	/**
	 *  Do a plan step.
	 */
	public void body()
	{
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

