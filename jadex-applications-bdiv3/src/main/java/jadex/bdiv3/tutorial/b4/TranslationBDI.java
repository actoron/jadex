package jadex.bdiv3.tutorial.b4;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.BDIConfiguration;
import jadex.bdiv3.annotation.BDIConfigurations;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.tutorial.b2.TranslationBDI.TranslationPlan;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.NameValue;

import java.util.HashMap;
import java.util.Map;

/**
 *  The translation agent B4.
 *  
 *  Using plan precondition and context condition.
 */
@Agent
@Description("The translation agent B3. <br>  Declare and activate an inline plan (declared as method).")
//@BDIConfigurations(@BDIConfiguration(name="default", 
//	initialplans=@NameValue(name="translateEnglishGerman")))
public class TranslationBDI
{
	//-------- attributes --------
//
//	/** The agent. */
//	@Agent
//	protected BDIAgent agent;
//	
//	/** The wordtable. */
//	protected Map<String, String> wordtable;
//
//	//-------- methods --------
//
//	@AgentCreated
//	public void init()
//	{
////		System.out.println("Created: "+this);
//		this.wordtable = new HashMap<String, String>();
//		this.wordtable.put("coffee", "Kaffee");
//		this.wordtable.put("milk", "Milch");
//		this.wordtable.put("cow", "Kuh");
//		this.wordtable.put("cat", "Katze");
//		this.wordtable.put("dog", "Hund");
//	}
//	
//	
//	/**
//	 *  The agent body.
//	 */
//	@AgentBody
//	public void body()
//	{
//		agent.adoptPlan("translateEnglishGerman");
//	}
//	
//	/**
//	 *  Translate an English word to German.
//	 */
//	@Plan
//	public void translateEnglishGerman()
//	{
//		String eword = "dog";
//		String gword = wordtable.get(eword);
//		System.out.println("Translated: "+eword+" - "+gword);
//	}
}

