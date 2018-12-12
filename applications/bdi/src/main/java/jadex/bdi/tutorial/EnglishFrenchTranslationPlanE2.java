package jadex.bdi.tutorial;

import jadex.bdiv3x.runtime.IExpression;
import jadex.bdiv3x.runtime.Plan;

/**
 *  An english french translation plan can translate
 *  english words to french and is instantiated on demand.
 */
public class EnglishFrenchTranslationPlanE2 extends Plan
{
//	//-------- attributes --------
//
//	/** Query the tuples for a word. */
//	protected IExpression	query_word;
//
//	//-------- constructors --------
//
//	/**
//	 *  Create a new plan.
//	 */
//	public EnglishFrenchTranslationPlanE2()
//	{
//		getLogger().info("Created: "+this);
//		this.query_word	= getExpression("query_efword");
//	}

	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		IExpression	query_word = getExpression("query_efword");
		if(!"english_french".equals(getParameter("direction").getValue()))
		{
			getLogger().warning("Cannot translate direction: "+getParameter("direction").getValue());
			fail();
		}

		String eword = (String)getParameter("word").getValue();
		String fword = (String)query_word.execute("$eword", eword);
		if(fword!=null)
		{
			//getLogger().info("Translating from english to german: "+eword+" - "+fword);
			getParameter("result").setValue(fword);
		}
		else
		{
			fail();
			//getLogger().info("Sorry word is not in database: "+eword);
		}
	}
}
