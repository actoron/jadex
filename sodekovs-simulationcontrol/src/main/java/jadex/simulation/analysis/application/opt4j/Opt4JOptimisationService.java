package jadex.simulation.analysis.application.opt4j;

import jadex.bridge.IExternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.data.AExperimentBatch;
import jadex.simulation.analysis.common.data.IAExperiment;
import jadex.simulation.analysis.common.data.IAExperimentBatch;
import jadex.simulation.analysis.common.data.optimisation.IAObjectiveFunction;
import jadex.simulation.analysis.common.data.parameter.ABasicParameter;
import jadex.simulation.analysis.common.data.parameter.AParameterEnsemble;
import jadex.simulation.analysis.common.data.parameter.IAParameter;
import jadex.simulation.analysis.common.data.parameter.IAParameterEnsemble;
import jadex.simulation.analysis.common.superClasses.service.analysis.ABasicAnalysisSessionService;
import jadex.simulation.analysis.service.continuative.optimisation.IAOptimisationService;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.opt4j.core.Individual;
import org.opt4j.core.Objective;
import org.opt4j.core.Objective.Sign;
import org.opt4j.core.Objectives;
import org.opt4j.core.Phenotype;
import org.opt4j.core.optimizer.Archive;
import org.opt4j.core.optimizer.Optimizer;
import org.opt4j.core.optimizer.Population;
import org.opt4j.core.problem.PhenotypeWrapper;
import org.opt4j.start.Opt4JTask;

import com.google.inject.Module;

/**
 * Opt4J implementation of {@link IAOptimisationService}vice
 */
@Service
public class Opt4JOptimisationService extends ABasicAnalysisSessionService
		implements IAOptimisationService {
	Set<String> methods = new HashSet<String>();
	Map<String, Map<String, Object>> sessionState = new HashMap<String, Map<String, Object>>();

	public Opt4JOptimisationService(IExternalAccess access) {
		super(access, IAOptimisationService.class, true);
		methods.add("Evolutionaerer Algorithmus");
	}

	@Override
	public IFuture supportedMethods() {
		return new Future(methods);
	}

	@Override
	public IFuture getMethodParameter(String methodName) {
		Future result = new Future();
		if (methodName.equals("Evolutionaerer Algorithmus")) {
			AParameterEnsemble ens = new AParameterEnsemble("methodParameter");
			ens.addParameter(new ABasicParameter("generations", Integer.class,
					20));
			ens.addParameter(new ABasicParameter("alpha", Integer.class, 8));
			ens.addParameter(new ABasicParameter("lambda", Integer.class, 3));
			ens.addParameter(new ABasicParameter("crossover", Double.class, 0.5));
			ens.addParameter(new ABasicParameter("mu", Double.class, 3));
			result.setResult(ens);
		}
		return result;
	}

	@Override
	public IFuture nextSolutions(String session,
			IAExperimentBatch previousSolutions) {
		Map<String, Object> state = sessionState.get(session);

		Boolean terminate = (Boolean) sessionState.get(session)
				.get("terminate");
		List<Map.Entry<String, IAParameter>> mappings = (List<Map.Entry<String, IAParameter>>) sessionState
				.get(session).get("mappings");
		Collection<Module> modules = (Collection<Module>) sessionState.get(
				session).get("modules");
		Opt4JTask task = (Opt4JTask) sessionState.get(session).get("task");
		Integer iteration = (Integer) sessionState.get(session)
				.get("iteration");
		IAExperiment baseExperiment = (IAExperiment) sessionState.get(session)
				.get("baseExperiment");

		Archive archive = task.getInstance(Archive.class);
		EvolutionaryAlgorithmSim opti = (EvolutionaryAlgorithmSim) task
				.getInstance(Optimizer.class);
		//
		if (iteration > 0) {
			// set Experiment results
			Population pop = opti.getPopulation();
			for (IAExperiment exp : previousSolutions.getExperiments().values()) {
				if (exp.isEvaluated())
				{
					for (Individual individual : pop) {
						if (!individual.isEvaluated()) {
							if (((PhenotypeWrapper<Map<String, Integer>>) individual
									.getPhenotype()).get().get("diffusion-rate") == ((Integer) exp
									.getConfigParameter("diffusion-rate").getValue())
									&& ((PhenotypeWrapper<Map<String, Integer>>) individual
											.getPhenotype()).get().get(
											"evaporation-rate") == ((Integer) exp
											.getConfigParameter("evaporation-rate")
											.getValue())) {
								Objectives objectives = new Objectives();
								Objective objective = new Objective("ticks",
										Sign.MIN);
								objectives.add(objective, (Double) exp
										.getResultParameter("ticks").getValue());
								individual.setObjectives(objectives);
							}
						}
					}
				} else
				{
					throw new RuntimeException("EVALUATION");
				}
			}

			// check converged
			if (opti.getTerminated()) {
				// We have found an optimum.
				terminate = true;
				sessionState.get(session).put("terminate", terminate);

				System.out.println("OPTIMUM!");
				Individual best = archive.iterator().next();
				PhenotypeWrapper<Map<String, Integer>> pheno = (PhenotypeWrapper<Map<String, Integer>>) best
						.getPhenotype();

				// IAParameterEnsemble result = (IAParameterEnsemble)
				// ((IAExperiment) state
				// .get("baseExperiment")).getConfigParameters().clonen();

				// Iterator it = pheno.get().values().iterator();
				// for (int j = 0; j < 2; j++) {
				// result.getParameter(mappings.get(j).getKey()).setValue(
				// it.next());
				// }

				// state.put("optimum", result);
				// state.put("optimumValue", best.getObjectives().getValues()
				// .iterator().next());
			}
		} else {
			state.put("baseExperiment", (IAExperiment) previousSolutions
					.getExperiments().values().iterator().next());
			baseExperiment = (IAExperiment) sessionState.get(session).get(
					"baseExperiment");

		}

		// next iteration
		IAExperimentBatch newExperiments = new AExperimentBatch("solutions"
				+ iteration);
		if (!terminate) {
			try {
				task.execute();
			} catch (Exception e) {
				e.printStackTrace();
			}
			Population pop = opti.getPopulation();
			for (Individual individual : pop) {
				if (!individual.isEvaluated())
				{
					IAExperiment exp = (IAExperiment) baseExperiment.clonen();
					exp.setName(individual.toString());
					((ABasicParameter) exp.getConfigParameter("diffusion-rate"))
							.setValue(((PhenotypeWrapper<Map<String, Integer>>) individual
									.getPhenotype()).get().get("diffusion-rate"));
					((ABasicParameter) exp.getConfigParameter("evaporation-rate"))
							.setValue(((PhenotypeWrapper<Map<String, Integer>>) individual
									.getPhenotype()).get().get("evaporation-rate"));
					((ABasicParameter) exp.getResultParameter("ticks"))
							.setValue(Double.NaN);
					newExperiments.addExperiment(exp);
				}				
			}
			iteration++;
			sessionState.get(session).put("iteration", iteration);
		}
		return new Future(newExperiments);
	}

	@Override
	public IFuture checkEndofOptimisation(String session) {
		return new Future((Boolean) sessionState.get(session).get("terminate"));
	}

	@Override
	public Future<String> configurateOptimisation(String session,
			String method, IAParameterEnsemble methodParameter,
			IAParameterEnsemble solution, IAObjectiveFunction objective,
			IAParameterEnsemble config) {
		// session erstellen
		String newSession = null;
		if (session != null) {
			if (sessions.containsKey(session)) {
				newSession = session;
			}
		}
		if (newSession == null) {
			newSession = (String) createSession(null).get(susThread);
		}
		final String sess = newSession;

		if (method.equals("Evolutionaerer Algorithmus")) {
			// set states
			Map<String, Object> state = new HashMap<String, Object>();

			// mapings
			List<Map.Entry<String, IAParameter>> mappings = new LinkedList<Map.Entry<String, IAParameter>>();
			for (Map.Entry<String, IAParameter> para : solution.getParameters()
					.entrySet()) {
				mappings.add(para);
			}

			double[] start = new double[mappings.size()];
			for (int i = 0; i < start.length; i++) {
				start[i] = (Double) mappings.get(i).getValue().getValue();
			}
			state.put("mappings", mappings);
			state.put("start", start);

			EvolutionaryAlgorithmSimModule evolutionaryAlgorithm = new EvolutionaryAlgorithmSimModule();
			evolutionaryAlgorithm.setGenerations(30);
			evolutionaryAlgorithm.setAlpha(20);
			evolutionaryAlgorithm.setLambda(7);
			evolutionaryAlgorithm.setCrossoverRate(0.4);
			evolutionaryAlgorithm.setMu(7);
			

			SimulationModule simulation = new SimulationModule();

			SimulationCompleterModule simulationCompleter = new SimulationCompleterModule();

			ViewerSimModule viewer = new ViewerSimModule();
			viewer.setCloseOnStop(false);

			Collection<Module> modules = new ArrayList<Module>();
			modules.add(evolutionaryAlgorithm);
			modules.add(simulation);
			modules.add(simulationCompleter);
			modules.add(viewer);

			state.put("modules", modules);
			state.put("iteration", new Integer(0));
			state.put("terminate", false);

			Opt4JTask task = new Opt4JTask(false);
			task.init(modules);
			task.open();
			state.put("task", task);

			sessionState.put(sess, state);
		}

		return new Future<String>(sess.toString());
	}

	@Override
	public IFuture getOptimum(String session) {
		return new Future((IAParameterEnsemble) sessionState.get(session).get(
				"optimum"));
	}

	@Override
	public IFuture getOptimumValue(String session) {
		return new Future((Double) sessionState.get(session)
				.get("optimumValue"));
	}

}
