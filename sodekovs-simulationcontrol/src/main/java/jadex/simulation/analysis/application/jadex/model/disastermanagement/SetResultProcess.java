package jadex.simulation.analysis.application.jadex.model.disastermanagement;

import jadex.bridge.service.clock.IClockService;
import jadex.component.ComponentInterpreter;
import jadex.extension.envsupport.environment.DefaultObjectCreationProcess;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.space2d.ContinuousSpace2D;

public class SetResultProcess extends DefaultObjectCreationProcess
{
	public SetResultProcess()
	{
		super();
		DisasterType.reset();
	}
	@Override
	public void execute(IClockService clock, IEnvironmentSpace space)
	{
		if (getProperty("timerate") != null)
		{
			// double rate = ((Number)getProperty("timerate")).doubleValue();
			double current = clock.getTime();
			while (lastrate > 0 && lasttime + lastrate < current)
			{
				lasttime += lastrate;
				ComponentInterpreter comp = (ComponentInterpreter) getProperty("component");
				comp.setResultValue("Fire", ((XYMeanChartDataConsumer)((ContinuousSpace2D)space).getDataConsumer("statistics_chart")).getMean("Fire"));
				comp.setResultValue("Victims", ((XYMeanChartDataConsumer)((ContinuousSpace2D)space).getDataConsumer("statistics_chart")).getMean("Victims"));
				comp.setResultValue("Chemicals", ((XYMeanChartDataConsumer)((ContinuousSpace2D)space).getDataConsumer("statistics_chart")).getMean("Chemicals"));
//				System.out.println(comp.getResults());
				this.lastrate = ((Number) getProperty("timerate")).doubleValue();
			}
		}
	}

}
