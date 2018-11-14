package jadex.bdi.tutorial;

import jadex.bdiv3x.runtime.IExpression;
import jadex.bdiv3x.runtime.Plan;

/**
 *  An english german translation plan can translate
 *  english words to german and is instantiated on demand.
 */
public class EnglishGermanTranslationPlanE2 extends Plan
{
//	//-------- attributes --------
//
//	/** Query the tuples for a word. */
//	protected IExpression	queryword;
//
//	//-------- constructors --------
//
//	/**
//	 *  Create a new plan.
//	 */
//	public EnglishGermanTranslationPlanE2()
//	{
//		getLogger().info("Created: "+this);
//		this.queryword	= getExpression("query_egword");
//	}

	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{ 
		IExpression	queryword = getExpression("query_egword");
		if(!"english_german".equals(getParameter("direction").getValue()))
		{
			getLogger().warning("Cannot translate direction: "+getParameter("direction").getValue());
			fail();
		}

		String eword = (String)getParameter("word").getValue();
		String gword = (String)queryword.execute("$eword", eword);
		if(gword!=null)
		{
			//getLogger().info("Translating from english to german: "+eword+" - "+gword);
			getParameter("result").setValue(gword);
		}
		else
		{
			//getLogger().info("Sorry word is not in database: "+eword);
			fail();
		}
	}
}
