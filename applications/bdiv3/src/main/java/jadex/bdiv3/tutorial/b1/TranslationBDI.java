package jadex.bdiv3.tutorial.b1;

import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.Body;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Plans;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentFeature;
import jadex.micro.annotation.Description;

/**
 *  The translation agent B1.
 *  
 *  Declare and activate an extra Java plan.
 */
@Agent(type=BDIAgentFactory.TYPE)
@Description("The translation agent B1. <br>  Declare and activate an extra Java plan.")
@Plans(@Plan(body=@Body(TranslationPlan.class)))
public class TranslationBDI
{
	/** The agent. */
//	@Agent
//	protected IInternalAccess agent;
	
	/** The bdi api. */
	@AgentFeature
	protected IBDIAgentFeature bdi;

	
	/**
	 *  The agent body.
	 */
	@AgentBody
	public void body()
	{
		bdi.adoptPlan(new TranslationPlan());
	}
}

