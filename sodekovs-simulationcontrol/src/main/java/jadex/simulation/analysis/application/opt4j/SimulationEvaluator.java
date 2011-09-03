package jadex.simulation.analysis.application.opt4j;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.opt4j.core.Objective;
import org.opt4j.core.Objective.Sign;
import org.opt4j.core.Objectives;
import org.opt4j.core.problem.Evaluator;
import org.opt4j.core.problem.PhenotypeWrapper;

public class SimulationEvaluator implements Evaluator<PhenotypeWrapper<Map<String, Double>>>
{
	//EVALUATION CLASS NOT NEEDED FOR SIMULATION
	Objective objective = new Objective("Verletzte", Sign.MIN);

	@Override
	public Objectives evaluate(PhenotypeWrapper<Map<String, Double>> phenotype)
	{
		System.out.println("test");
		Double result = new Double(0);
		for (Double inte : phenotype.get().values())
		{
			result += inte;
		}
		Objectives objectives = new Objectives();
		objectives.add(objective, result);
		return objectives;
	}

	@Override
	public Collection<Objective> getObjectives()
	{
		return Arrays.asList(objective);
	}

}
