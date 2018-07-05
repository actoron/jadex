package jadex.micro.testcases.semiautomatic.features;

import jadex.bridge.IInternalAccess;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Feature;
import jadex.micro.annotation.Features;

/**
 *  Agent testing incorporation of an additional feature.
 */
@Agent
@Features(additional=true,
	value=@Feature(type=ICustomFeature.class, clazz=CustomFeature.class))
public class CustomFeatureAgent
{
	@Agent
	protected IInternalAccess agent;
	
	@AgentCreated
	public void hello()
	{
		System.out.println(agent+" "+agent.getFeature(ICustomFeature.class).someMethod());
	}
}

