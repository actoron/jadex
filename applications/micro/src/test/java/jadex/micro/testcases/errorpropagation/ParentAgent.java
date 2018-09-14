package jadex.micro.testcases.errorpropagation;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentChildKilled;
import jadex.micro.annotation.Component;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;

/**
 *  The parent ag
 */
@Agent
@ComponentTypes(@ComponentType(name="child", clazz=ChildAgent.class))
@Configurations(@Configuration(name="def", components=@Component(type="child")))
public class ParentAgent
{
	/** The agent. */
	@Agent 
	protected IInternalAccess agent;
	
	/**
	 *  The agent body.
	 */
	@AgentBody
	protected void body()
	{
		System.out.println("Created parent: "+agent.getId());
	}
	
	/**
	 *  Called when a child component was killed.
	 */
	@AgentChildKilled
	protected void childTerminated(IComponentDescription desc, Exception ex)
	{
		System.out.println("My child component was terminated: "+desc.getName());
	}
}
