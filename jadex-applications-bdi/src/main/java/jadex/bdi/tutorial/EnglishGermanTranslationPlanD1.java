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
public class EnglishGermanTranslationPlanD1 extends Plan
{
	//-------- attributes --------

	/** Query the tuples for a word. */
	protected IExpression	query_word;

	//-------- constructors --------

	/**
	 *  Create a new plan.
	 */
	public EnglishGermanTranslationPlanD1()
	{
		getLogger().info("Created:"+this);
		this.query_word	= getExpression("query_egword");
	}

	//-------- methods --------

	/**
	 *  Do a plan step.
	 */
	public void body()
	{
		String	reply;
		String	cont;
		StringTokenizer stok = new StringTokenizer(
			(String)((IMessageEvent)getReason()).getParameter(SFipa.CONTENT).getValue(), " ");
		if(stok.countTokens()==3)
		{
			stok.nextToken();
			stok.nextToken();
			String eword = stok.nextToken();
			String gword = (String)query_word.execute("$eword", eword);
			if(gword!=null)
			{
				getLogger().info("Translating from english to german: "+eword+" - "+gword);
				cont = gword;
				reply = "inform";
			}
			else
			{
				cont = "Sorry word is not in database: "+eword;
				reply = "failure";
			}
		}
		else
		{
			cont = "Sorry format not correct.";
			reply = "failure";
		}
		IMessageEvent	replymsg = getEventbase().createReply((IMessageEvent)getReason(), reply);
		replymsg.getParameter(SFipa.CONTENT).setValue(cont);
		sendMessage(replymsg);
	}
}

