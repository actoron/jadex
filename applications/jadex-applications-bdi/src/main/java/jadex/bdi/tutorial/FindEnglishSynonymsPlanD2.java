package jadex.bdi.tutorial;

import java.util.List;
import java.util.StringTokenizer;

import jadex.bdiv3x.runtime.IExpression;
import jadex.bdiv3x.runtime.IMessageEvent;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.fipa.SFipa;

/**
 *  Find english synonyms for a word.
 */
public class FindEnglishSynonymsPlanD2 extends Plan
{
//	//-------- attributes --------
//
//	/** Query the tuples for a word. */
//	protected IExpression	querytranslate;
//
//	/** Query to find synonyms. */
//	protected IExpression	queryfind;
//
//
//	//-------- constructors --------
//
//	/**
//	 *  Create a new plan.
//	 */
//	public FindEnglishSynonymsPlanD2()
//	{
//		getLogger().info("Created: "+this);
//
//		// Create precompiled queries.
////		String	translate	= "select one $wordpair.get(1) "
////			+"from Tuple $wordpair in $beliefbase.getBeliefSet(\"transcap.egwords\").getFacts() "
////			+"where $wordpair.get(0).equals($eword)";
//		
////		String	translate	= "select one $wordpair.get(1) "
////			+"from Tuple $wordpair in $beliefbase.egwords "
////			+"where $wordpair.get(0).equals($eword)";
//
////		String	find	= "select $wordpair.get(0) "
////			+"from Tuple $wordpair in $beliefbase.getBeliefSet(\"transcap.egwords\").getFacts() "
////			+"where $wordpair.get(1).equals($gword) && !$wordpair.get(0).equals($eword)";
//		
//		String	find	= "select $wordpair.get(0) "
//			+"from Tuple $wordpair in $beliefbase.egwords "
//			+"where $wordpair.get(1).equals($gword) && !$wordpair.get(0).equals($eword)";
//
////		this.querytranslate	= createExpression(translate, new String[]{"$eword"}, new Class[]{String.class});
////		this.querytranslate	= getExpression("transcap.query_egword");
//		this.querytranslate	= getExpression("query_egword");
//		
//		this.queryfind	= createExpression(find, new String[]{"$gword", "$eword"}
//			, new Class[]{String.class, String.class});
//	}

	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		String	find	= "select $wordpair.get(0) "
			+"from Tuple $wordpair in $beliefbase.egwords "
			+"where $wordpair.get(1).equals($gword) && !$wordpair.get(0).equals($eword)";
		
		IExpression querytranslate = getExpression("query_egword");
		IExpression	queryfind = createExpression(find);//, new String[]{"$gword", "$eword"}
//		, new Class[]{String.class, String.class});
		
		IMessageEvent me = (IMessageEvent)getReason();
		String reply, cont;
		StringTokenizer stok = new StringTokenizer((String)me.getParameter(SFipa.CONTENT).getValue(), " ");

		if(stok.countTokens()==3)
		{
			stok.nextToken();
			stok.nextToken();
			String eword = stok.nextToken();
			String gword = (String)querytranslate.execute("$eword", eword);
//			queryfind.setParameter("$gword", gword);
//			queryfind.setParameter("$eword", eword);
			List syns = (List)queryfind.execute(new String[]{"$gword", "$eword"},  new Object[]{gword, eword});
			getLogger().info("Synonyms for eword: "+syns);
			reply	= "inform";
			cont	= "Synonyms for "+eword+" : "+syns;
		}
		else
		{
			reply	= "failure";
			cont	= "Request format not correct.";
		}
		IMessageEvent re = getEventbase().createReply(me, reply);
		re.getParameter(SFipa.CONTENT).setValue(cont);
		sendMessage(re);
	}
}
