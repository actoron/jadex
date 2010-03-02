package jadex.bdi.tutorial;

import jadex.base.fipa.SFipa;
import jadex.bdi.runtime.IExpression;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.Plan;

import java.util.StringTokenizer;

/**
 *  An english german translation plan can translate
 *  english words to german and is instantiated on demand.
 */
public class EnglishGermanTranslationPlanC2 extends Plan
{
	//-------- attributes --------

	/** Query the tuples for a word. */
	protected IExpression	queryword;

	//-------- constructors --------

	/**
	 *  Create a new plan.
	 */
	public EnglishGermanTranslationPlanC2()
	{
		getLogger().info("Created:"+this);
		this.queryword	= getExpression("query_egword");
	}

	//-------- methods --------

	/**
	 *  Do a plan step.
	 */
	public void body()
	{
		StringTokenizer stok = new StringTokenizer((String)((IMessageEvent)getReason())
			.getParameter(SFipa.CONTENT).getValue(), " ");
		if(stok.countTokens()==3)
		{
			stok.nextToken();
			stok.nextToken();
			String eword = stok.nextToken();
			String gword = (String)queryword.execute("$eword", eword);
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
}

