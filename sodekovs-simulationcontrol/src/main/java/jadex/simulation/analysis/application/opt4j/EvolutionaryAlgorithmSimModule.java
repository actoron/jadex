package jadex.simulation.analysis.application.opt4j;

import org.opt4j.optimizer.ea.ConstantCrossoverRate;
import org.opt4j.optimizer.ea.CrossoverRate;
import org.opt4j.optimizer.ea.EvolutionaryAlgorithm;
import org.opt4j.optimizer.ea.EvolutionaryAlgorithmModule;

public class EvolutionaryAlgorithmSimModule extends EvolutionaryAlgorithmModule
{

	@Override
	public void config()
	{
		bindOptimizer(EvolutionaryAlgorithmSim.class);

		bind(CrossoverRate.class).to(ConstantCrossoverRate.class).in(SINGLETON);
	}

}
