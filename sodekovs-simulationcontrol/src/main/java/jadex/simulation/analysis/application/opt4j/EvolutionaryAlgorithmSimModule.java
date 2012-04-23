package jadex.simulation.analysis.application.opt4j;

import org.opt4j.config.annotations.Info;
import org.opt4j.config.annotations.Order;
import org.opt4j.optimizer.ea.ConstantCrossoverRate;
import org.opt4j.optimizer.ea.CrossoverRate;
import org.opt4j.optimizer.ea.EvolutionaryAlgorithmModule;
import org.opt4j.start.Constant;

public class EvolutionaryAlgorithmSimModule extends EvolutionaryAlgorithmModule
{
	@Override
	public void config()
	{
		bindOptimizer(EvolutionaryAlgorithmSim.class);

		bind(CrossoverRate.class).to(ConstantCrossoverRate.class).in(SINGLETON);
	}

}
