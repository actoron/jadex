package jadex.simulation.analysis.application.opt4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Random;

import org.opt4j.config.ExecutionEnvironment;
import org.opt4j.config.Task;
import org.opt4j.config.TaskStateListener;
import org.opt4j.core.Archive;
import org.opt4j.core.Individual;
import org.opt4j.core.Objective;
import org.opt4j.core.Objectives;
import org.opt4j.core.Population;
import org.opt4j.core.Objective.Sign;
import org.opt4j.core.optimizer.Completer;
import org.opt4j.core.optimizer.Control;
import org.opt4j.core.optimizer.Optimizer;
import org.opt4j.core.problem.PhenotypeWrapper;
import org.opt4j.optimizer.ea.EvolutionaryAlgorithm;
import org.opt4j.optimizer.ea.EvolutionaryAlgorithmModule;
import org.opt4j.optimizer.ea.Selector;
import org.opt4j.start.Opt4JTask;
import org.opt4j.start.Opt4JTasksPanel;
import org.opt4j.viewer.Viewer;
import org.opt4j.viewer.ViewerModule;

import com.google.inject.Module;

public class SimulationStarter
{

	public static void main(String[] args)
	{
		EvolutionaryAlgorithmSimModule evolutionaryAlgorithm = new EvolutionaryAlgorithmSimModule();
		evolutionaryAlgorithm.setGenerations(1000);		
		evolutionaryAlgorithm.setAlpha(10);
		evolutionaryAlgorithm.setLambda(5);
		evolutionaryAlgorithm.setCrossoverRate(1);
		evolutionaryAlgorithm.setMu(4);

		SimulationModule simulation = new SimulationModule();
		
		SimulationCompleterModule simulationCompleter = new SimulationCompleterModule();
		
		ViewerSimModule viewer = new ViewerSimModule();
		viewer.setCloseOnStop(false);

		Collection<Module> modules = new ArrayList<Module>();
		modules.add(evolutionaryAlgorithm);
		modules.add(simulation);
		modules.add(simulationCompleter);
		modules.add(viewer);

		Opt4JTask task = new Opt4JTask(false);
		task.init(modules);
		task.open();
		
		Archive archive = task.getInstance(Archive.class);
		EvolutionaryAlgorithmSim opti = (EvolutionaryAlgorithmSim) task.getInstance(Optimizer.class);
		
		try
		{
			int count = 0;
			while (!opti.getTerminated())
			{
				
				task.execute();
				count++;
				Population pop = opti.getPopulation();
				System.out.println("POP " + count);
				Objective objective = new Objective("Sum", Sign.MIN);
				SimulationEvaluator eva = new SimulationEvaluator();
				for (Individual individual : pop)
				{
					individual.setObjectives(eva.evaluate((PhenotypeWrapper<Map<String, Integer>>) individual.getPhenotype()));
					System.out.println(individual.getObjectives());
				}
				
				for (Individual individual : archive)
				{
					System.out.println("BEST: " + individual.getPhenotype() + individual.getObjectives());
				}
				
			}
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		task.close();
		
//		try
//		{
//			
//			
//			SimulationCompleter control = (SimulationCompleter) task.getInstance(Completer.class);
//			
//			task.execute();
////			task.addStateListener(new TaskStateListener()
////			{
////				
////				@Override
////				public void stateChanged(Task arg0)
////				{
////					System.out.println(arg0.getState());
////					
////				}
////			});
////			control.setObjectives();
//			
//			for (Individual individual : archive)
//			{
//				System.out.println(individual.getGenotype());
//				System.out.println(individual.getObjectives());
//			}
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//		}
//		finally
//		{
//			task.close();
//		}

	}

}
