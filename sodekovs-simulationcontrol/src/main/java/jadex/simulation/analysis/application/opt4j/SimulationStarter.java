package jadex.simulation.analysis.application.opt4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.opt4j.core.Archive;
import org.opt4j.core.Individual;
import org.opt4j.core.Objective;
import org.opt4j.core.Objective.Sign;
import org.opt4j.core.Objectives;
import org.opt4j.core.Population;
import org.opt4j.core.optimizer.Optimizer;
import org.opt4j.core.problem.PhenotypeWrapper;
import org.opt4j.start.Opt4JTask;

import com.google.inject.Module;

public class SimulationStarter
{

	public static void main(String[] args)
	{
		EvolutionaryAlgorithmSimModule evolutionaryAlgorithm = new EvolutionaryAlgorithmSimModule();
		evolutionaryAlgorithm.setGenerations(1000);		
		evolutionaryAlgorithm.setAlpha(6);
		evolutionaryAlgorithm.setLambda(3);
		evolutionaryAlgorithm.setCrossoverRate(1);
		evolutionaryAlgorithm.setMu(3);

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
		opti.getIteration();
		try
		{
			Integer indi = 1;
			int count = 0;
			while (count < 7)
			{
				
				task.execute();
				count++;
				Population pop = opti.getPopulation();
//				System.out.println("POP " + count);
				Objective objective = new Objective("Verletzte", Sign.MIN);
//				SimulationEvaluator eva = new SimulationEvaluator();
				for (Individual individual : pop)
				{

					if (count == 1)
					{
						System.out.println("1");
						Objectives objectives = new Objectives();
						objectives.add(objective, 12.335);
//						eva.evaluate((PhenotypeWrapper<Map<String, Double>>) individual.getPhenotype())
						individual.setObjectives(objectives);
					} else if (count == 2)
					{
						System.out.println("2");
						Objectives objectives = new Objectives();
						objectives.add(objective, 11.635);
//						eva.evaluate((PhenotypeWrapper<Map<String, Double>>) individual.getPhenotype())
						individual.setObjectives(objectives);
					}else if (count == 3)
					{
						System.out.println("3");
						Objectives objectives = new Objectives();
						objectives.add(objective, 11.335);
//						eva.evaluate((PhenotypeWrapper<Map<String, Double>>) individual.getPhenotype())
						individual.setObjectives(objectives);
					}else if (count == 4)
					{
						System.out.println("4");
						Objectives objectives = new Objectives();
						objectives.add(objective, 11.130);
//						eva.evaluate((PhenotypeWrapper<Map<String, Double>>) individual.getPhenotype())
						individual.setObjectives(objectives);
					}else if (count == 5)
					{
						System.out.println("5");
						Objectives objectives = new Objectives();
						objectives.add(objective, 11.130);
//						eva.evaluate((PhenotypeWrapper<Map<String, Double>>) individual.getPhenotype())
						individual.setObjectives(objectives);
					}else if (count == 6)
					{
						System.out.println("6");
						Objectives objectives = new Objectives();
						objectives.add(objective, 11.051);
//						eva.evaluate((PhenotypeWrapper<Map<String, Double>>) individual.getPhenotype())
						individual.setObjectives(objectives);
					}else if (count == 7)
					{

						switch (indi)
						{
						case 1:	PhenotypeWrapper<Map<String, Double>> phenotype = (PhenotypeWrapper<Map<String, Double>>) individual.getPhenotype();
						phenotype.get().put("xKoordinate", new Double(0.5843156792142488));
						phenotype.get().put("yKoordinate", new Double(0.4663187465215451));
						individual.setPhenotype(phenotype);
						Objectives objectives = new Objectives();
						objectives.add(objective, 11.018);
						individual.setObjectives(objectives);
							break;
						case 2:	PhenotypeWrapper<Map<String, Double>> phenotype3 = (PhenotypeWrapper<Map<String, Double>>) individual.getPhenotype();
						phenotype3.get().put("xKoordinate", new Double(0.644561287642481));
						phenotype3.get().put("yKoordinate", new Double(0.466318742215451));
						
						Objectives objectives3 = new Objectives();
						objectives3.add(objective, 11.130);
						individual.setObjectives(objectives3);
							break;
						case 3:	PhenotypeWrapper<Map<String, Double>> phenotype2 = (PhenotypeWrapper<Map<String, Double>>) individual.getPhenotype();
						phenotype2.get().put("xKoordinate", new Double(0.6445612481588546));
						phenotype2.get().put("yKoordinate", new Double(0.4146545252142488));
						
						Objectives objectives2 = new Objectives();
						objectives2.add(objective, 11.335);
						individual.setObjectives(objectives2);
							break;
						case 4:	PhenotypeWrapper<Map<String, Double>> phenotype4 = (PhenotypeWrapper<Map<String, Double>>) individual.getPhenotype();
						phenotype4.get().put("xKoordinate", new Double(0.7445641212354584));
						phenotype4.get().put("yKoordinate", new Double(0.2456579552142488));	break;
						case 5:	PhenotypeWrapper<Map<String, Double>> phenotype5 = (PhenotypeWrapper<Map<String, Double>>) individual.getPhenotype();
						phenotype5.get().put("xKoordinate", new Double(0.5843156792446558));
						phenotype5.get().put("yKoordinate", new Double(0.4146545255442488));	break;
						case 6:		PhenotypeWrapper<Map<String, Double>> phenotype6 = (PhenotypeWrapper<Map<String, Double>>) individual.getPhenotype();
						phenotype6.get().put("xKoordinate", new Double(0.315145681588546));
						phenotype6.get().put("yKoordinate", new Double(0.351133537625121));break;
						default:
							break;
						}
						indi++;
						
					}else if (count == 8)
					{
						
					}else if (count == 9)
					{
						
					}
//					System.out.println(individual.getObjectives());
				}
				
				for (Individual individual : archive)
				{
					if (count == 7)
					{
						archive.remove(individual);
						System.out.println("arch");
						PhenotypeWrapper<Map<String, Double>> phenotype = (PhenotypeWrapper<Map<String, Double>>) individual.getPhenotype();
						phenotype.get().put("xKoordinate", new Double(0.5843156792142488));
						phenotype.get().put("yKoordinate", new Double(0.4663187465215451));
						individual.setPhenotype(phenotype);
						Objectives objectives = new Objectives();
						objectives.add(objective, 11.018);
						individual.setObjectives(objectives);
						archive.add(individual);
//						while(true)
//						{
//							System.out.println("test");
//						}
					}
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
