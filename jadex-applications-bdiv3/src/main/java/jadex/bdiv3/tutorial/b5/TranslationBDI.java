package jadex.bdiv3.tutorial.b5;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanPrecondition;
import jadex.bdiv3.annotation.PlanReason;
import jadex.bdiv3.annotation.ServiceTrigger;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.runtime.impl.PlanFailureException;
import jadex.bridge.service.annotation.Service;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 *  The translation agent B4.
 *  
 *  Translation agent that implements itself the translation
 *  service indirectly using plans. In each plan a service trigger
 *  is specified that defines to which service calls it should react to.
 */
@Description("The translation agent B4. <br> Translation agent that implements itself the translation service. Just looks up translation word in hashtable and returns the corresponding entry.")
@Agent
@Service
@ProvidedServices(@ProvidedService(name="transser", type=ITranslationService.class, 
	implementation=@Implementation(expression="$pojoagent.createService(ITranslationService.class)")))
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
//		@PlanReason
//		public InvocationInfo reason;
		
		/**
		 * @return True, if word map is applicable.
		 */
		@PlanPrecondition
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
//		public void body(String eword)
		public void body(Object[] params)
		{
			String eword = (String)params[0];
			String gword = wordtable.get(eword);
			System.out.println("Translated with internal dictionary dictionary: "+eword+" - "+gword);
		}
	}
	
	/**
	 *  Translate an English word to German.
	 *  @param eword The english word.
	 *  @return The german translation.
	 */
	@Plan(trigger=@Trigger(service=@ServiceTrigger(type=ITranslationService.class)))
//	public void internetTranslate(String eword)
	public void internetTranslate(Object[] params)
	{
		String eword = (String)params[0];
		try
		{
			//URL dict = new URL("http://dict.leo.org/?search="+eword);
			URL dict = new URL("http://wolfram.schneider.org/dict/dict.cgi?query="+eword);
			System.out.println("Following translations were found online at: "+dict);
			BufferedReader in = new BufferedReader(new InputStreamReader(dict.openStream()));
			String inline;
			while((inline = in.readLine())!=null)
			{
				if(inline.indexOf("<td>")!=-1 && inline.indexOf(eword)!=-1)
				{
					try
					{
						int start = inline.indexOf("<td>")+4;
						int end = inline.indexOf("</td", start);
						String worda = inline.substring(start, end);
						start = inline.indexOf("<td", start);
						start = inline.indexOf(">", start);
						end = inline.indexOf("</td", start);
						String wordb = inline.substring(start, end==-1? inline.length()-1: end);
						wordb = wordb.replaceAll("<b>", "");
						wordb = wordb.replaceAll("</b>", "");
						System.out.println(worda+" - "+wordb);
						System.out.println("Translated with internet dictionary: "+worda+" - "+wordb);
					}
					catch(Exception e)
					{
						System.out.println(inline);
					}
				}
			}
			in.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new PlanFailureException(e.getMessage());
		}
	}
	
	
	/**
	 *  Create a wrapper service implementation.
	 */
	public Object createService(Class<?> iface)
	{
		return Proxy.newProxyInstance(agent.getClassLoader(), new Class[]{iface}, 
			new BDIServiceInvocationHandler(agent, iface));
	}
}

