package jadex.simulation.analysis.application.opt4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.opt4j.common.completer.ParallelIndividualCompleter;
import org.opt4j.core.Individual;
import org.opt4j.core.Individual.State;
import org.opt4j.core.optimizer.Control;
import org.opt4j.core.optimizer.TerminationException;
import org.opt4j.core.problem.Decoder;
import org.opt4j.core.problem.Evaluator;
import org.opt4j.start.Constant;

import com.google.inject.Inject;

public class SimulationCompleter extends ParallelIndividualCompleter
{
	@Inject
	public SimulationCompleter(Control control, Decoder decoder, Evaluator evaluator, @Constant(value = "maxThreads", namespace = ParallelIndividualCompleter.class) int maxThreads)
	{
		super(control, decoder, evaluator, maxThreads);
	}

	// Just set EVALUATING
	protected void evaluate(Individual individual)
	{
		individual.setState(State.EVALUATING);
	};
	
	@Override
	public void complete(Iterable<? extends Individual> iterable) throws TerminationException {

		for (Individual individual : iterable) {
			if (!individual.isEvaluated()) {
				control.checkpoint();
				decode(individual);
				control.checkpoint();
				evaluate(individual);
				control.checkpoint();
			}
		}
	}
}
