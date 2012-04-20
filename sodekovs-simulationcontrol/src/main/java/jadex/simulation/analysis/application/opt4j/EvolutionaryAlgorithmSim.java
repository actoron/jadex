package jadex.simulation.analysis.application.opt4j;

import java.util.Collection;
import java.util.Map;

import org.opt4j.core.Individual;
import org.opt4j.core.IndividualFactory;
import org.opt4j.core.Objective;
import org.opt4j.core.Objective.Sign;
import org.opt4j.core.optimizer.Archive;
import org.opt4j.core.optimizer.Control;
import org.opt4j.core.optimizer.IndividualCompleter;
import org.opt4j.core.optimizer.Iteration;
import org.opt4j.core.optimizer.OptimizerIterationListener;
import org.opt4j.core.optimizer.Population;
import org.opt4j.core.optimizer.StopException;
import org.opt4j.core.optimizer.TerminationException;
import org.opt4j.core.problem.PhenotypeWrapper;
import org.opt4j.optimizer.ea.EvolutionaryAlgorithm;
import org.opt4j.optimizer.ea.Mating;
import org.opt4j.optimizer.ea.Selector;
import org.opt4j.start.Constant;

import com.google.inject.Inject;

public class EvolutionaryAlgorithmSim extends EvolutionaryAlgorithm {
	
	Boolean terminated = false;
	private final IndividualFactory myIndividualFactory;

	@Inject
	public EvolutionaryAlgorithmSim(
			Population population,
			Archive archive,
			IndividualFactory individualFactory,
			IndividualCompleter completer,
			Control control,
			Selector selector,
			Mating mating,
			Iteration iteration,
			@Constant(value = "alpha", namespace = EvolutionaryAlgorithm.class) int alpha,
			@Constant(value = "mu", namespace = EvolutionaryAlgorithm.class) int mu,
			@Constant(value = "lambda", namespace = EvolutionaryAlgorithm.class) int lambda) {
		super(population, archive, individualFactory, completer, control,
				selector, mating, iteration, alpha, mu, lambda);
		myIndividualFactory = individualFactory;
	}

	@Override
	public void optimize() throws TerminationException, StopException {
		try {
			selector.init(alpha + lambda);

			if (population.isEmpty()) {
				System.out.println("ITERATION: " + getIteration());

				

				while (population.size() < alpha) {
					population.add(myIndividualFactory.create());
				}

				completer.complete(population);
			} else {
				
				if (population.size() > alpha) {
					Collection<Individual> lames = selector.getLames(
							population.size() - alpha, population);
					population.removeAll(lames);
				}
				
				archive.update(population);
				Individual indi = archive.iterator().next();
				System.out.println("Dif-"+ ((PhenotypeWrapper<Map<String, Integer>>) indi.getPhenotype()).get().get("diffusion-rate"));
				System.out.println("Eva-"+ ((PhenotypeWrapper<Map<String, Integer>>) indi.getPhenotype()).get().get("evaporation-rate"));
				System.out.println("Value-"+indi.getObjectives().get(new Objective("ticks", Sign.MIN)).getDouble());
				
				iteration.next();
				for (OptimizerIterationListener listener : iterationListeners) {
					listener.iterationComplete(this, iteration.value());
				}
				control.checkpointStop();

				System.out.println("ITERATION: " + getIteration());

				int offspringCount = lambda;

				while (population.size() < alpha && offspringCount > 0) {
					population.add(myIndividualFactory.create());
					offspringCount--;
				}

				if (offspringCount < lambda) { // evaluate new individuals first
					completer.complete(population);
				} else {

					Collection<Individual> parents = selector.getParents(mu,
							population);
					Collection<Individual> offspring = mating.getOffspring(
							offspringCount, parents);
					population.addAll(offspring);

					completer.complete(population);
				}
				
				
				if (iteration.value() >= iteration.max()) {
					// System.out.println("--ITERATION:" + iteration);
					// System.out.println("--GENERATION:" + generations);
					throw new TerminationException();
				}
			}
		} catch (TerminationException e) {
			terminated = true;
		}
	}

	public Boolean getTerminated() {
		return terminated;
	}

	public Population getPopulation() {
		return population;
	}
}
