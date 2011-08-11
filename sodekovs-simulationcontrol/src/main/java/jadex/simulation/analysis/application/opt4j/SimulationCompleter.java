package jadex.simulation.analysis.application.opt4j;

import org.opt4j.common.completer.ParallelCompleter;
import org.opt4j.core.Individual;
import org.opt4j.core.Individual.State;
import org.opt4j.core.optimizer.Control;
import org.opt4j.core.problem.Decoder;
import org.opt4j.core.problem.Evaluator;
import org.opt4j.start.Constant;

import com.google.inject.Inject;

public class SimulationCompleter extends ParallelCompleter
{
	@Inject
	public SimulationCompleter(Control control, Decoder decoder, Evaluator evaluator, @Constant(value = "maxThreads", namespace = ParallelCompleter.class) int maxThreads)
	{
		super(control, decoder, evaluator, maxThreads);
	}

	// Just set EVALUATING
	protected void evaluate(Individual individual)
	{
		State state = individual.getState();
		if (state == State.PHENOTYPED)
		{
			individual.setState(State.EVALUATING);
		}
		else
		{
			throw new IllegalStateException(
					"Cannot evaluate Individual, current state: " + state);
		}
	};
}
