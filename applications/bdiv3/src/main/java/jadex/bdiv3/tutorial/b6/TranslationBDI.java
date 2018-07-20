package jadex.bdiv3.tutorial.b6;

import java.util.HashMap;
import java.util.Map;

import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentFeature;
import jadex.micro.annotation.Description;

/**
 *  The translation agent B3.
 *  
 *  Declare and activate an inline plan (declared as method).
 */
@Agent(type=BDIAgentFactory.TYPE)
@Description("The translation agent B3. <br>  Declare and activate an inline plan (declared as method).")
//@BDIConfigurations(@BDIConfiguration(name="default", 
//	initialplans=@NameValue(name="translateEnglishGerman")))
public class TranslationBDI
{
	//-------- attributes --------

//	/** The agent. */
//	@Agent
//	protected BDIAgent agent;
	
	/** The bdi api. */
	@AgentFeature
	protected IBDIAgentFeature bdi;
	
	/** The wordtable. */
	protected Map<String, String> wordtable;

	//-------- methods --------

	@AgentCreated
	public void init()
	{
//		System.out.println("Created: "+this);
		this.wordtable = new HashMap<String, String>();
		this.wordtable.put("coffee", "Kaffee");
		this.wordtable.put("milk", "Milch");
		this.wordtable.put("cow", "Kuh");
		this.wordtable.put("cat", "Katze");
		this.wordtable.put("dog", "Hund");
	}
	
	
	/**
	 *  The agent body.
	 */
	@AgentBody
	public void body()
	{
		IFuture<String[]>	fut	= bdi.adoptPlan("translateEnglishGerman");
		String[]	wordpair	= fut.get();
		System.out.println("Translated: "+wordpair[0]+" - "+wordpair[1]);
	}
	
	/**
	 *  Translate an English word to German.
	 */
	@Plan
	public String[] translateEnglishGerman()
	{
		String eword = "dog";
		String gword = wordtable.get(eword);
		return new String[]{eword, gword};
	}
}

