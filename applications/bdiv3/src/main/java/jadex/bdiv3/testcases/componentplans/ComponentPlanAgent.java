package jadex.bdiv3.testcases.componentplans;

import jadex.bdiv3.BDIAgentFactory;
import jadex.commons.Boolean3;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Parent;

/**
 *  Plan implemented as component.
 */
@Agent(type=BDIAgentFactory.TYPE, synchronous=Boolean3.TRUE)
public class ComponentPlanAgent
{
	//-------- attributes --------
	
	/** Access to the parent agent. */
	@Parent
	protected ComponentPlanBDI	parent;
	
//	/** Access to the plan. */
//	@Plan
//	protected IPlan	plan;
	
	//-------- methods --------
	
	/**
	 *  Plan body.
	 */
	@AgentBody
	public IFuture<Void>	body()
	{
		System.out.println("Setting success: "+parent);
		parent.setSuccess(true);
		
		return IFuture.DONE;
	}
}
