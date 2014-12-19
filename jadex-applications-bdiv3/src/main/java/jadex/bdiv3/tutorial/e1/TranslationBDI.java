package jadex.bdiv3.tutorial.e1;

import jadex.bdiv3.annotation.Capability;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bridge.IInternalAccess;
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
	protected IInternalAccess agent;
	
	@Capability
	protected TranslationCapability capa = new TranslationCapability();
	
	/**
	 *  The agent body.
	 */
	@AgentBody
	public void body()
	{
		String eword = "dog";
		String gword = (String)agent.getComponentFeature(IBDIAgentFeature.class).dispatchTopLevelGoal(capa.new Translate(eword)).get();
		System.out.println("Translated: "+eword+" "+gword);
	}
}
