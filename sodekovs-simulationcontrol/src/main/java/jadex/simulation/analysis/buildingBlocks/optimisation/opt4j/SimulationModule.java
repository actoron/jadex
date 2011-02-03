package jadex.simulation.analysis.buildingBlocks.optimisation.opt4j;

import org.opt4j.core.problem.ProblemModule;

//@Parent(TutorialModule.class)
public class SimulationModule extends ProblemModule
{

	@Override
	protected void config()
	{
		// bindProblem(SimulationCreator.class, SimulationDecoder.class,
		// SimulationEvaluator.class);
		bindProblem(SimulationCreator.class, SimulationDecoder.class,
				SimulationEvaluator.class);
	}

}
