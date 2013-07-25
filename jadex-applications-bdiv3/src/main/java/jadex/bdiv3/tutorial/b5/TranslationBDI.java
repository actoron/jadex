package jadex.bdiv3.tutorial.b5;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.ServiceTrigger;
import jadex.bdiv3.annotation.Trigger;
import jadex.bridge.service.annotation.Service;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

import java.util.HashMap;
import java.util.Map;

/**
 *  The translation agent B4.
 *  
 *  Translation agent that implements itself the translation
 *  service. Just looks up translation word in hashtable and
 *  returns the corresponding entry.
 */
@Description("The translation agent B4. <br> Translation agent that implements itself the translation service. Just looks up translation word in hashtable and returns the corresponding entry.")
@Agent
@Service
@ProvidedServices(@ProvidedService(name="transser", type=ITranslationService.class, 
	implementation=@Implementation(BDIServicePlan.class)))
public class TranslationBDI
{
	//-------- attributes --------

	@Agent
	protected BDIAgent agent;
	
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
	 *  Translate an English word to German.
	 *  @param eword The english word.
	 *  @return The german translation.
	 */
	@Plan(trigger=@Trigger(service=@ServiceTrigger(type=ITranslationService.class)))
	public class TranslatePlan
	{
		/**
		 * @return True, if word map is applicable.
		 */
		public boolean checkPrecondition(Object[] params)
		{
			return wordtable.containsKey(params[0]);
		}
		
		/**
		 * 
		 * @param eword
		 * @return
		 */
		@PlanBody
		public void body(String eword)
		{
			System.out.println("eword: "+eword);
		}
	}
	
	/**
	 *  Translate an English word to German.
	 *  @param eword The english word.
	 *  @return The german translation.
	 */
	@Plan(trigger=@Trigger(service=@ServiceTrigger(type=ITranslationService.class)))
	public void internetTranslate(String eword)
	{
		System.out.println("eword: "+eword);
	}
}

