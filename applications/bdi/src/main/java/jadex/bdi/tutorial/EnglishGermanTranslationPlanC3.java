package jadex.bdi.tutorial;

import java.util.StringTokenizer;

import jadex.bdiv3x.runtime.IExpression;
import jadex.bdiv3x.runtime.IMessageEvent;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.fipa.SFipa;

/**
 *  An english german translation plan can translate
 *  english words to german and is instantiated on demand.
 */
public class EnglishGermanTranslationPlanC3 extends Plan
{
//	//-------- attributes --------
//
//	/** Query the tuples for a word. */
//	protected IExpression query_word;
//
//	//-------- constructors --------
//
//	/**
//	 *  Create a new plan.
//	 */
//	public EnglishGermanTranslationPlanC3()
//	{
//		getLogger().info("Created: "+this);
//		this.query_word	= getExpression("query_egword");
//	}

	//-------- methods --------

	/**
	 *  Execute the plan.
	 */
	public void body()
	{
		IExpression query_word = getExpression("query_egword");
		StringTokenizer stok = new StringTokenizer((String)((IMessageEvent)getReason())
			.getParameter(SFipa.CONTENT).getValue(), " ");
		if(stok.countTokens()==3)
		{
			stok.nextToken();
			stok.nextToken();
			String eword = stok.nextToken();
			String gword = (String)query_word.execute("$eword", eword);
			if(gword!=null)
			{
				getLogger().info("Translating from english to german: "+eword+" - "+gword);

				// Increment the succeeded translation counter.
				int cnt = ((Integer)getBeliefbase().getBelief("transcnt").getFact()).intValue();
				getBeliefbase().getBelief("transcnt").setFact(Integer.valueOf(cnt+1));
				getLogger().info("Translation count is now:"+(cnt+1));
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
}
