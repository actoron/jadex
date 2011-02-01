package jadex.simulation.analysis.buildingBlocks.optimisation.opt4j;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.nlogo.lite.InterfaceComponent;
import org.opt4j.core.Objective;
import org.opt4j.core.Objectives;
import org.opt4j.core.Objective.Sign;
import org.opt4j.core.problem.Evaluator;
import org.opt4j.core.problem.PhenotypeWrapper;

public class SimulationEvaluator implements Evaluator<PhenotypeWrapper<Map<String, Integer>>> {

	Objective objective = new Objective("Ticks", Sign.MIN);
	final javax.swing.JFrame frame = new javax.swing.JFrame();
	final InterfaceComponent comp = new InterfaceComponent(frame);
	{
		comp.setVisible(false);
	}

	@Override
	public Objectives evaluate(PhenotypeWrapper<Map<String, Integer>> phenotype) {
		if (!comp.isVisible()) {
			try {
				java.awt.EventQueue.invokeAndWait
						(new Runnable()
								{
									public void run()
									{
										frame.setSize(1000, 700);
										frame.add(comp);
										frame.setVisible(true);
										try {
											String filePre = new File("..").getCanonicalPath()
													+ "/sodekovs-simulationcontrol/src/main/java/jadex/simulation/analysis/models";
											String fileName = filePre + "/netLogo/" + "AntsStop.nlogo";
											comp.open(fileName);
											comp.setVisible(true);
										}
										catch (Exception ex)
										{
											ex.printStackTrace();
										}
									}
								});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		Double result = 0.0;

		try {
			Map<String, Integer> paraMap = phenotype.get();
			for (Map.Entry<String, Integer> parameter : paraMap.entrySet()) {
				parameter.getValue();
				String comm = "set " + parameter.getKey() + " " + parameter.getValue().toString();
				comp.command(comm);
				System.out.print(parameter.getKey() + "=" + parameter.getValue() + " ");
			}
			System.out.println("");

			comp.command("setup");
			comp.command("go");

			result = (Double) comp.report("ticks");

			// comp.setVisible(false);
//			frame.dispose();
			System.gc();
			} catch (Exception e) {
				e.printStackTrace();
			}

		Objectives objectives = new Objectives();
		objectives.add(objective, result);
		return objectives;
	}

	@Override
	public Collection<Objective> getObjectives() {
		return Arrays.asList(objective);
	}

}
