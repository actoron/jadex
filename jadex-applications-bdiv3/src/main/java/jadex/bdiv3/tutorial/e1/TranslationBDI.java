package jadex.bdiv3.tutorial.e1;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.Capability;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;

/**
 *  The translation agent e1.
 *  
 *  Using a capability.
 */
@Agent
public class TranslationBDI
{
	/** The agent. */
	@Agent
	protected BDIAgent agent;
	
	@Capability
	protected TranslationCapability capa = new TranslationCapability();
	
	/**
	 *  The agent body.
	 */
	@AgentBody
	public void body()
	{
		String eword = "dog";
		String gword = (String)agent.dispatchTopLevelGoal(capa.new Translate(eword)).get();
		System.out.println("Translated: "+eword+" "+gword);
	}
}
