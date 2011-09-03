package jadex.simulation.analysis.application.opt4j;

import java.util.Collection;

import org.opt4j.core.Archive;
import org.opt4j.core.Individual;
import org.opt4j.core.IndividualBuilder;
import org.opt4j.core.Population;
import org.opt4j.core.optimizer.Completer;
import org.opt4j.core.optimizer.Control;
import org.opt4j.core.optimizer.Iterations;
import org.opt4j.core.optimizer.OptimizerIterationListener;
import org.opt4j.core.optimizer.StopException;
import org.opt4j.core.optimizer.TerminationException;
import org.opt4j.optimizer.ea.EvolutionaryAlgorithm;
import org.opt4j.optimizer.ea.Mating;
import org.opt4j.optimizer.ea.Selector;
import org.opt4j.start.Constant;

import com.google.inject.Inject;

public class EvolutionaryAlgorithmSim extends EvolutionaryAlgorithm
{
	Boolean terminated = false;

	// private Collection<Individual> toEvaluate = new HashSet<Individual>();

	@Inject
	public EvolutionaryAlgorithmSim(
			Population population,
			Archive archive,
			IndividualBuilder individualBuilder,
			Completer completer,
			Control control,
			Selector selector,
			Mating mating,
			@Iterations int generations,
			@Constant(value = "alpha", namespace = EvolutionaryAlgorithm.class) int alpha,
			@Constant(value = "mu", namespace = EvolutionaryAlgorithm.class) int mu,
			@Constant(value = "lambda", namespace = EvolutionaryAlgorithm.class) int lambda)
	{
		super(population, archive, individualBuilder, completer, control, selector, mating, generations, alpha, mu, lambda);
	};

	@Override
	public void optimize() throws TerminationException, StopException
	{
		try
		{
			if (iteration == 0)
			{
				System.out.println("*****" + getIteration());
//				System.out.println("ITERATION:" + iteration);
//				System.out.println("GENERATION:" + generations);
				selector.init(alpha + lambda);

				while (population.size() < alpha)
				{
					population.add(individualBuilder.build());
				}

				completer.complete(population);

				iteration++;
				control.checkpointStop();
			}
			else 
			{
//				System.out.println("ITERATION:" + iteration);
//				System.out.println("GENERATION:" + generations);
				archive.update(population);
				for (OptimizerIterationListener listener : iterationListeners)
				{
					listener.iterationComplete(this, iteration);
				}

				Collection<Individual> lames = selector
						.getLames(lambda, population);
				
//				for (Individual individual : lames)
//				{
//					System.out.println("lame: " + individual.getPhenotype() + " = " + individual.getObjectives());
//				}
				
				population.removeAll(lames);

				Collection<Individual> parents = selector
						.getParents(mu, population);

				Collection<Individual> offspring = mating.getOffspring(lambda,
						parents);
				population.addAll(offspring);

				// evaluate offspring before selecting lames
//				completer.complete(offspring);

				completer.complete(population);
				// archive.update(population);
				// for (OptimizerIterationListener listener : iterationListeners)
				// {
				// listener.iterationComplete(this, iteration);
				// }
				iteration++;
				control.checkpointStop();
				if (iteration >= generations)
				{
//					System.out.println("--ITERATION:" + iteration);
//					System.out.println("--GENERATION:" + generations);
					throw new TerminationException();
				}
			}
		}
		catch (TerminationException e)
		{
			terminated = true;
		}
	}

	public Boolean getTerminated()
	{
		return terminated;
	}

	public Population getPopulation()
	{
		return population;
	}
}
