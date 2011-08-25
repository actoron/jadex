package jadex.simulation.analysis.application.commonsMath;

import jadex.bridge.IExternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.data.AExperimentBatch;
import jadex.simulation.analysis.common.data.IAExperiment;
import jadex.simulation.analysis.common.data.IAExperimentBatch;
import jadex.simulation.analysis.common.data.parameter.IAParameter;
import jadex.simulation.analysis.common.data.parameter.IAParameterEnsemble;
import jadex.simulation.analysis.service.basic.analysis.ABasicAnalysisSessionService;
import jadex.simulation.analysis.service.continuative.optimisation.IAOptimisationService;
import jadex.simulation.analysis.service.continuative.optimisation.IAObjectiveFunction;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.math.analysis.MultivariateRealFunction;
import org.apache.commons.math.exception.MathUserException;
import org.apache.commons.math.exception.MaxCountExceededException;
import org.apache.commons.math.optimization.ConvergenceChecker;
import org.apache.commons.math.optimization.RealPointValuePair;
import org.apache.commons.math.optimization.SimpleScalarValueChecker;
import org.apache.commons.math.optimization.direct.AbstractSimplex;
import org.apache.commons.math.optimization.direct.NelderMeadSimplex;
import org.apache.commons.math.util.Incrementor;
import org.apache.commons.math.util.MathUtils;

public class CommonsMathOptimierungsService extends ABasicAnalysisSessionService implements IAOptimisationService
{
	Set<String> methods = new HashSet<String>();
	Map<UUID, Map<String, Object>> sessionState = new HashMap<UUID, Map<String, Object>>();

	public CommonsMathOptimierungsService(IExternalAccess access)
	{
		super(access, IAOptimisationService.class, true);
		methods.add("Simplex Algorithmus");
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

		if (method.equals("Simplex Algorithmus"))
		{
			// set states
			Map<String, Object> state = new HashMap<String, Object>();

			// evaluations (counts numbers of evaluations)
			Incrementor evaluations = new Incrementor();
			evaluations.setMaximalCount(100);
			evaluations.resetCount();
			state.put("evaluations", evaluations);

			// iteration
			state.put("iteration", new Integer(0));

			// terminate
			Boolean terminate = false;
			state.put("terminate", terminate);

			// objective
			state.put("objective", objective);

			// comparator (to sort RealPointValuePair in simplex)
			final boolean isMinim = objective.MinGoal();
			final Comparator<RealPointValuePair> comparator = new Comparator<RealPointValuePair>()
			{
				public int compare(final RealPointValuePair o1,
								final RealPointValuePair o2)
			{
				final double v1 = o1.getValue();
				final double v2 = o2.getValue();
				return isMinim ? Double.compare(v1, v2) : Double.compare(v2, v1);
			}
			};
			state.put("comparator", comparator);

			// evaluator (just increment evaluations)
			final MultivariateRealFunction evalFunc = new MultivariateRealFunction()
			{
				public double value(double[] point) throws MathUserException
			{
				return 0.0;
			}
			};
			state.put("evaluator", evalFunc);

			// evaluator
			ConvergenceChecker<RealPointValuePair> checker = new SimpleScalarValueChecker(100 * MathUtils.EPSILON, 100 * MathUtils.SAFE_MIN);
			state.put("checker", checker);

			// start and mapping
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

			// simplex
			NelderMeadSimplex simplex = new NelderMeadSimplex(2);
			simplex.build(start);
			state.put("simplex", simplex);
			
			sessionState.put(sess, state);
		}

		return new Future(sess);
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
		if (methodName.equals("Simplex Algorithmus"))
		{
			Set<String> parameters = new HashSet<String>();
			// TODO
		}
		return result;
	}

	@Override
	public IFuture nextSolutions(UUID session, IAExperimentBatch previousSolutions)
	{
		Map<String, Object> state = sessionState.get(session);
		AbstractSimplex simplex = (AbstractSimplex) state.get("simplex");
		Comparator<RealPointValuePair> comparator = (Comparator<RealPointValuePair>) state.get("comparator");
		Incrementor evaluations = (Incrementor) state.get("evaluations");
		ConvergenceChecker<RealPointValuePair> checker = (ConvergenceChecker<RealPointValuePair>) state.get("checker");
		MultivariateRealFunction evalFunc = (MultivariateRealFunction) state.get("evaluator");
		Boolean terminate = (Boolean) sessionState.get(session).get("terminate");
		List<Map.Entry<String, IAParameter>> mappings = (List<Map.Entry<String, IAParameter>>) sessionState.get(session).get("mappings");
		Integer iteration = (Integer) sessionState.get(session).get("iteration");
		IAObjectiveFunction objective = (IAObjectiveFunction) sessionState.get(session).get("objective");
		
		
		
		if (iteration > 0)
		{
			//set Experiment results
			RealPointValuePair[] lastsimplex = simplex.getPoints();
			for (IAExperiment exp : previousSolutions.getExperiments().values())
			{
				for (RealPointValuePair realPointValuePair : lastsimplex)
				{
					Boolean found = true;
					double[] point = realPointValuePair.getPoint();
					for (int i = 0; i < point.length; i++)
					{
						if (point[i] == (Double)exp.getInputParameter(mappings.get(i).getKey()).getValue()) found = false;
					}
					if (found) 
						{
						lastsimplex[mappings.indexOf(exp.getName())] = new RealPointValuePair(getPointsOfExperiment(exp, mappings), (Double) objective.benchmark(exp.getOutputParameters()).get(susThread), true);
						}
				}
			}

			//sort simplex
			Arrays.sort(lastsimplex, comparator);
			
			//check converged
			boolean converged = true;
			for (int i = 0; i < simplex.getSize(); i++)
			{
				RealPointValuePair last = lastsimplex[i];
				System.out.println(last);
				converged &= checker.converged(evaluations.getCount(), last, simplex.getPoint(i));
			}
			if (converged)
			{
				// We have found an optimum.
				terminate = true;
				System.out.println("OPTIMUM!");
//				state.put("optimum",);
			}
		} else
		{
			state.put("baseExperiment", (IAExperiment) previousSolutions.getExperiments().values().iterator().next());
		}
		
		
		
		// next iteration
		IAExperimentBatch newExperiments = new AExperimentBatch("solutions");
		if (!terminate)
		{
			iteration++;
			simplex.iterate(evalFunc, comparator);
			IAExperiment startExp = (IAExperiment) state.get("startExperiment");
			IAExperimentBatch nextSolutions = new AExperimentBatch("solutions");
			RealPointValuePair[] nextsimplex = simplex.getPoints();
			for (int i = 0; i < nextsimplex.length; i++)
			{
				final RealPointValuePair vertex = nextsimplex[i];
				final double[] point = vertex.getPointRef();

				if (Double.isNaN(vertex.getValue()))
				{
					IAExperiment experiment = setNewExperiment(startExp, point, mappings);
					newExperiments.addExperiment(experiment);
					try
					{
						((Incrementor) sessionState.get(session).get("evaluations")).incrementCount();
					}
					catch (MaxCountExceededException e)
					{
						sessionState.get(session).put("terminate", true);
					}
				}
			}

		}
		return new Future(newExperiments);

	}

	private double[] getPointsOfExperiment(IAExperiment exp, List<Entry<String, IAParameter>> mappings)
	{
		double[] points = new double[mappings.size()];
		int i = 0;
		for (Iterator iterator = mappings.iterator(); iterator.hasNext();)
		{
			Entry<String, IAParameter> entry = (Entry<String, IAParameter>) iterator.next();
			points[i] = (Double) exp.getOutputParameter(entry.getKey()).getValue();
			i++;
		}
		return points;
	}

	private IAExperiment setNewExperiment(IAExperiment baseExp, double[] points, List<Entry<String, IAParameter>> mappings)
	{
		IAExperiment exp = (IAExperiment) baseExp.clonen();
		for (int j = 0; j < points.length; j++)
		{
			exp.removeInputParamter(mappings.get(j).getKey());
			exp.addInputParamter(mappings.get(j).getValue());
		}
		return exp;
	}

	@Override
	public Boolean checkEndofOptimisation(UUID session)
	{
		return (Boolean) sessionState.get(session).get("terminate");
	}

	@Override
	public IAParameterEnsemble getOptimum(UUID session)
	{
		return (IAParameterEnsemble) sessionState.get(session).get("optimum");
	}
}
