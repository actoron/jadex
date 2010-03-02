package jadex.bdi.tutorial;

import jadex.base.fipa.SFipa;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.Plan;

import java.util.HashMap;
import java.util.Map;

/**
 *  An english german translation plan can translate
 *  english words to german and is instantiated on demand.
 */
public class EnglishGermanTranslationPlanF2 extends Plan
{
	//-------- attributes --------

	/** The wordtable. */
	protected Map wordtable;

	//-------- constructors --------

	/**
	 *  Create a new plan.
	 */
	public EnglishGermanTranslationPlanF2()
	{
		getLogger().info("Created: "+this);

		this.wordtable = new HashMap();
		this.wordtable.put("coffee", "Kaffee");
		this.wordtable.put("milk", "Milch");
		this.wordtable.put("cow", "Kuh");
		this.wordtable.put("cat", "Katze");
		this.wordtable.put("dog", "Hund");
	}

	//-------- methods --------

	/**
	 *  Execute the plan.
	 */
	public void body()
	{
		String	reply;
		String	content;
		IMessageEvent me = (IMessageEvent)getReason();
		String eword = (String)me.getParameter(SFipa.CONTENT).getValue();
		String gword = (String)this.wordtable.get(eword);
		if(gword!=null)
		{
			reply = "inform";
			content = "Translating from english to german: "+eword+" - "+gword;
		}
		else
		{
			reply = "failure";
			content = "Sorry word is not in database: "+eword;
		}
		IMessageEvent re = getEventbase().createReply(me, reply);
		re.getParameter(SFipa.CONTENT).setValue(content);
		sendMessage(re);
		//sendMessage(me.createReply(reply, content));
	}
}
