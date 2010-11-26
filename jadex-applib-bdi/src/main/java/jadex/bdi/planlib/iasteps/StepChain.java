package jadex.bdi.planlib.iasteps;

import java.util.Collection;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;

public class StepChain implements IComponentStep
{
	protected IComponentStep[] steps;
	
	public StepChain(IComponentStep[] steps)
	{
		this.steps = steps;
	}
	
	public StepChain(Collection steps)
	{
		this.steps = (IComponentStep[]) steps.toArray(new IComponentStep[steps.size()]);
	}
	
	public Object execute(IInternalAccess ia)
	{
		Object[] results = new Object[steps.length];
		for (int i = 0; i < steps.length; ++i)
			results[i] = steps[i].execute(ia);
		
		return results;
	}
}
