package jadex.micro.testcases.semiautomatic.features;

import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.ILifecycleComponentFeature;
import jadex.bridge.component.IPojoComponentFeature;
import jadex.bridge.component.impl.ArgumentsResultsComponentFeature;
import jadex.bridge.component.impl.ExecutionComponentFeature;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Feature;
import jadex.micro.annotation.Features;
import jadex.micro.features.IMicroInjectionFeature;
import jadex.micro.features.impl.MicroInjectionComponentFeature;
import jadex.micro.features.impl.MicroLifecycleComponentFeature;
import jadex.micro.features.impl.MicroPojoComponentFeature;

/**
 *  Agent testing incorporation of a different feature set.
 */
@Agent
@Features(
{	
	@Feature(type=IExecutionFeature.class, clazz=ExecutionComponentFeature.class),
	@Feature(type=IArgumentsResultsFeature.class, clazz=ArgumentsResultsComponentFeature.class),
	@Feature(type=IPojoComponentFeature.class, clazz=MicroPojoComponentFeature.class),
	@Feature(type=IMicroInjectionFeature.class, clazz=MicroInjectionComponentFeature.class,
		predecessors={IPojoComponentFeature.class, IArgumentsResultsFeature.class}),
	@Feature(type=ILifecycleComponentFeature.class, clazz=MicroLifecycleComponentFeature.class,
		predecessors={IPojoComponentFeature.class}, addlast=false)
})
public class MinimalFeatureAgent
{
	@Agent
	protected IInternalAccess agent;
	
	@AgentCreated
	public void hello()
	{
		System.out.println("hello: "+agent);
	}
}
