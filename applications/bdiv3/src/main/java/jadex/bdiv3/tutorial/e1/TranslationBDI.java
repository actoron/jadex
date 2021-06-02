package jadex.bdiv3.tutorial.e1;

import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.Capability;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bridge.service.annotation.OnStart;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentFeature;

/**
 *  The translation agent e1.
 *  
 *  Using a capability.
 */
@Agent(type=BDIAgentFactory.TYPE)
public class TranslationBDI
{
	/** The agent. */
	@AgentFeature
	protected IBDIAgentFeature bdiFeature;
	
	@Capability
	protected TranslationCapability capability = new TranslationCapability();
	
	/**
	 *  The agent body.
	 */
	//@AgentBody
	@OnStart
	public void body()
	{
		String eword = "dog";
		String gword = (String)bdiFeature.dispatchTopLevelGoal(capability.new Translate(eword)).get();
		System.out.println("Translated: "+eword+" "+gword);
	}
}
