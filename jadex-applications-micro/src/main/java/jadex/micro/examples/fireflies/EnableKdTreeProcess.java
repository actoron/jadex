package jadex.micro.examples.fireflies;

import jadex.application.space.envsupport.environment.IEnvironmentSpace;
import jadex.application.space.envsupport.environment.ISpaceProcess;
import jadex.application.space.envsupport.environment.space2d.Space2D;
import jadex.commons.SimplePropertyObject;
import jadex.commons.service.clock.IClockService;

public class EnableKdTreeProcess extends SimplePropertyObject implements ISpaceProcess
{
	public void execute(IClockService clock, IEnvironmentSpace space)
	{
		((Space2D) space).enableKdTree("firefly");
		space.removeSpaceProcess(getProperty(ISpaceProcess.ID));
	}

	public void shutdown(IEnvironmentSpace space)
	{
	}

	public void start(IClockService clock, IEnvironmentSpace space)
	{
	}
	
}
