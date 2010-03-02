package jadex.bdi.tutorial;

import jadex.base.fipa.SFipa;
import jadex.bdi.runtime.IExpression;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.Plan;
import jadex.commons.Tuple;

import java.util.StringTokenizer;

/**
 *  Add a english - german word pair to the wordtable.
 */
public class EnglishGermanAddWordPlanD1 extends Plan
{
	//-------- attributes --------

	/** Query the tuples for a word. */
	protected IExpression	testword;

	//-------- constructors --------

	/**
	 *  Create a new plan.
	 */
	public EnglishGermanAddWordPlanD1()
	{
		getLogger().info("Created :"+this);
		this.testword	= getExpression("query_egword");
	}

	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		Object	cont;
		String	reply;
		StringTokenizer stok = new StringTokenizer(
			(String)((IMessageEvent)getReason()).getParameter(SFipa.CONTENT).getValue(), " ");
		if(stok.countTokens()==4)
		{
			stok.nextToken();
			stok.nextToken();
			String eword = stok.nextToken();
			String gword = stok.nextToken();
			Object words = testword.execute("$eword", eword);
			if(words==null)
			{
				getBeliefbase().getBeliefSet("egwords").addFact(new Tuple(eword, gword));
				cont = "Added  new wordpair to database: "+eword+" - "+gword;
				reply = "inform";
			}
			else
			{
				cont= "Sorry database already contains word: "+eword;
				reply = "failure";
			}
		}
		else
		{
			cont = "Sorry format not correct.";
			reply = "failure";
		}
		IMessageEvent	replymsg	= getEventbase().createReply((IMessageEvent)getReason(), reply);
		replymsg.getParameter(SFipa.CONTENT).setValue(cont);
		sendMessage(replymsg);
	}
}
