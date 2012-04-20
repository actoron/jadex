package jadex.simulation.analysis.application.opt4j;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.opt4j.core.Objective;
import org.opt4j.core.Objective.Sign;
import org.opt4j.core.Objectives;
import org.opt4j.core.problem.Evaluator;
import org.opt4j.core.problem.PhenotypeWrapper;

public class SimulationEvaluator implements Evaluator<PhenotypeWrapper<Map<String, Integer>>>
{
	//EVALUATION CLASS NOT NEEDED FOR SIMULATION
	Objective objective = new Objective("ticks", Sign.MIN);

	@Override
	public Objectives evaluate(PhenotypeWrapper<Map<String, Integer>> phenotype)
	{
		System.out.println("ERROR Evaluator");
		Double result = new Double(0);
		for (Integer inte : phenotype.get().values())
		{
			result += inte;
		}
		Objectives objectives = new Objectives();
		objectives.add(objective, result);
		return objectives;
	}

	public Collection<Objective> getObjectives()
	{
		return Arrays.asList(objective);
	}

}
