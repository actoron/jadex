package jadex.simulation.analysis.application.opt4j;

import jadex.bridge.IExternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.data.AExperimentBatch;
import jadex.simulation.analysis.common.data.IAExperiment;
import jadex.simulation.analysis.common.data.IAExperimentBatch;
import jadex.simulation.analysis.common.data.parameter.ABasicParameter;
import jadex.simulation.analysis.common.data.parameter.AParameterEnsemble;
import jadex.simulation.analysis.common.data.parameter.IAParameter;
import jadex.simulation.analysis.common.data.parameter.IAParameterEnsemble;
import jadex.simulation.analysis.common.events.service.AServiceEvent;
import jadex.simulation.analysis.common.util.AConstants;
import jadex.simulation.analysis.process.basicTasks.IATaskView;
import jadex.simulation.analysis.process.basicTasks.user.AServiceCallTaskView;
import jadex.simulation.analysis.service.basic.analysis.ABasicAnalysisSessionService;
import jadex.simulation.analysis.service.continuative.optimisation.IAOptimisationService;
import jadex.simulation.analysis.service.continuative.optimisation.IAObjectiveFunction;
import jadex.simulation.analysis.service.simulation.Modeltype;
import jadex.simulation.analysis.service.simulation.execution.IAExecuteExperimentsService;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.nlogo.headless.HeadlessWorkspace;
import org.nlogo.lite.InterfaceComponent;

import com.google.inject.Module;

/**
 * Opt4J implementation of {@link IAOptimisationService}vice
 */
public class Opt4JOptimisationService extends ABasicAnalysisSessionService implements IAOptimisationService
{
	Set<String> methods = new HashSet<String>();
	Map<UUID, Map<String, Object>> sessionState = new HashMap<UUID, Map<String, Object>>();

	public Opt4JOptimisationService(IExternalAccess access)
	{
		super(access, IAOptimisationService.class, true);
		methods.add("Evolutionaerer Algorithmus");
	}

	@Override
	public IFuture supportedMethods()
	{
		return new Future(methods);
	}

	@Override
	public IFuture getMethodParameter(String methodName)
	{
		Future result = new Future();
		if (methodName.equals("Evolutionaerer Algorithmus"))
		{
			AParameterEnsemble ens = new AParameterEnsemble("methodParameter");
			ens.addParameter(new ABasicParameter("generations", Integer.class, 10));
			ens.addParameter(new ABasicParameter("alpha", Integer.class, 10));
			ens.addParameter(new ABasicParameter("lambda", Integer.class, 4));
			ens.addParameter(new ABasicParameter("crossover", Double.class, 0.5));
			ens.addParameter(new ABasicParameter("mu", Double.class, 4));
			result.setResult(ens);
		}
		return result;
	}

	@Override
	public IFuture nextSolutions(UUID session, IAExperimentBatch previousSolutions)
	{
		
		//FIRST: set Experiment results
		
		
		// set next;
		IAExperimentBatch newExperiments = new AExperimentBatch("solutions");
		
		
		
		return new Future(newExperiments);
	}

	@Override
	public Boolean checkEndofOptimisation(UUID session)
	{
		return (Boolean) sessionState.get(session).get("terminate");
	}

	@Override
	public IFuture configurateOptimisation(UUID session, String method, IAParameterEnsemble methodParameter, IAParameterEnsemble solution, IAObjectiveFunction objective, IAParameterEnsemble config)
	{
		// session erstellen
		UUID newSession = null;
		if (session != null)
		{
			if (sessions.containsKey(session))
			{
				newSession = session;
			}
		}
		if (newSession == null)
		{
			newSession = (UUID) createSession(null).get(susThread);
		}
		final UUID sess = newSession;
		
		if (method.equals("Evolutionaerer Algorithmus"))
		{
			// set states
			Map<String, Object> state = new HashMap<String, Object>();

			//mapings
			List<Map.Entry<String, IAParameter>> mappings = new LinkedList<Map.Entry<String, IAParameter>>();
			for (Map.Entry<String, IAParameter> para : solution.getParameters().entrySet())
			{
				mappings.add(para);
			}
			
			double[] start = new double[mappings.size()];
			for (int i = 0; i < start.length; i++)
			{
				start[i] = (Double) mappings.get(i).getValue().getValue();
			}
			state.put("mappings", mappings);
			state.put("start", start);
			
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
			
			state.put("modules", modules);
			sessionState.put(sess, state);
		}
		
		
		return new Future(sess);
	}

	@Override
	public IAParameterEnsemble getOptimum(UUID session)
	{
		return (IAParameterEnsemble) sessionState.get(session).get("optimum");
	}
}
