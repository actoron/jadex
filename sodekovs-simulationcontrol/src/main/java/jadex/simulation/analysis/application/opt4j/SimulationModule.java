package jadex.simulation.analysis.application.opt4j;

import org.opt4j.core.problem.ProblemModule;

public class SimulationModule extends ProblemModule
{

	@Override
	protected void config()
	{
		bindProblem(SimulationCreator.class, SimulationDecoder.class,
				SimulationEvaluator.class);
	}

}
