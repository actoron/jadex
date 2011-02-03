package jadex.simulation.analysis.buildingBlocks.optimisation.opt4j;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.nlogo.headless.HeadlessWorkspace;
import org.opt4j.core.Objective;
import org.opt4j.core.Objectives;
import org.opt4j.core.Objective.Sign;
import org.opt4j.core.problem.Evaluator;
import org.opt4j.core.problem.PhenotypeWrapper;

public class SimulationEvaluatorLite implements Evaluator<PhenotypeWrapper<Map<String, Integer>>>
{

	HeadlessWorkspace workspace = HeadlessWorkspace.newInstance();
	Objective objective = new Objective("Ticks", Sign.MIN);

	public SimulationEvaluatorLite()
	{
		try
		{
			String filePre = new File("..").getCanonicalPath()
					+ "/sodekovs-simulationcontrol/src/main/java/jadex/simulation/analysis/models";
			String fileName = filePre + "/netLogo/" + "AntsStop.nlogo";
			workspace.open
					(fileName);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public Objectives evaluate(PhenotypeWrapper<Map<String, Integer>> phenotype)
	{

		Double result = 0.0;
		try
		{
			Map<String, Integer> paraMap = phenotype.get();
			for (Map.Entry<String, Integer> parameter : paraMap.entrySet())
			{
				String comm = "set " + parameter.getKey() + " " + parameter.getValue().toString();
				workspace.command(comm);
				System.out.print(parameter.getKey() + "=" + parameter.getValue() + " ");
			}
			System.out.println("");

			workspace.command("setup");
			workspace.command("go");

			result = (Double) workspace.report("ticks");
			// workspace.dispose();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
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
