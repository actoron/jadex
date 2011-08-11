package jadex.simulation.analysis.application.jadex;

import jadex.bridge.IExternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.simulation.analysis.application.desmoJ.models.varncarrier.VancarrierModel;
import jadex.simulation.analysis.common.data.AExperimentBatch;
import jadex.simulation.analysis.common.data.IAExperiment;
import jadex.simulation.analysis.common.data.factories.AExperimentFactory;
import jadex.simulation.analysis.common.data.parameter.AParameterEnsemble;
import jadex.simulation.analysis.common.data.parameter.ASummaryParameter;
import jadex.simulation.analysis.common.data.parameter.IAParameter;
import jadex.simulation.analysis.common.data.parameter.IAParameterEnsemble;
import jadex.simulation.analysis.common.events.service.AServiceEvent;
import jadex.simulation.analysis.common.util.AConstants;
import jadex.simulation.analysis.service.basic.analysis.ABasicAnalysisSessionService;
import jadex.simulation.analysis.service.simulation.Modeltype;
import jadex.simulation.analysis.service.simulation.execution.IAExperimentAusfuehrenService;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import javax.swing.JTextArea;

import desmoj.core.report.Reporter;
import desmoj.core.simulator.Experiment;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.Reportable;
import desmoj.core.simulator.SimTime;
import desmoj.core.simulator.TimeInstant;

/**
 * Implementation of a DesmoJ service for (single) experiments.
 */
public class JadexExperimentAusfuehrenService extends ABasicAnalysisSessionService implements IAExperimentAusfuehrenService
{

	/**
	 * Create a new DesmoJ Simulation Service
	 * 
	 * @param comp
	 *            The active generalComp.
	 */
	public JadexExperimentAusfuehrenService(IExternalAccess access)
	{
		super(access, IAExperimentAusfuehrenService.class, true);

	}

	// -------- methods --------

	/**
	 * Simulate an experiment
	 */
	public IFuture executeExperiment(UUID session, IAExperiment exp)
	{
		final Future res = new Future();
		JadexSessionView view = (JadexSessionView) sessionViews.get(session);

		
		res.setResult(exp);
		return res;
	}

	@Override
	public Set<Modeltype> supportedModels()
	{
		Set<Modeltype> result = new HashSet<Modeltype>();
		result.add(Modeltype.DesmoJ);
		return result;
	}

	public IFuture createSession(IAParameterEnsemble configuration)
	{
		UUID id = UUID.randomUUID();
		if (configuration == null) configuration = new AParameterEnsemble("Session Konfiguration");
		sessions.put(id, configuration);
		configuration.setEditable(false);
		sessionViews.put(id, new JadexSessionView(this, id, configuration));
		serviceChanged(new AServiceEvent(this, AConstants.SERVICE_SESSION_START, id));
		return new Future(id);
	}

	public static void main(String[] args)
	{
		IAExperiment exp = AExperimentFactory.createTestAExperiment();
		new JadexExperimentAusfuehrenService(null).executeExperiment(null, exp);

	}
}
