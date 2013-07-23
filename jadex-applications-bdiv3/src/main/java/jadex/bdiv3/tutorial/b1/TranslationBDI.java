package jadex.bdiv3.tutorial.b1;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.Body;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Plans;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Description;

/**
 *  The translation agent B1.
 *  
 *  Declare and activate an extra Java plan.
 */
@Agent
@Description("The translation agent B1. <br>  Declare and activate an extra Java plan.")
@Plans(@Plan(body=@Body(TranslationPlan.class)))
public class TranslationBDI
{
	/** The agent. */
	@Agent
	protected BDIAgent agent;
	
	/**
	 *  The agent body.
	 */
	@AgentBody
	public void body()
	{
		agent.adoptPlan(new TranslationPlan());
	}
}

