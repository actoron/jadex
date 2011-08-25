package jadex.simulation.analysis.application.jadex;

import jadex.bridge.CreationInfo;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.SServiceProvider;
import jadex.commons.future.DefaultResultListener;
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
import jadex.simulation.analysis.service.simulation.execution.IAExecuteExperimentsService;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import javax.swing.JTextArea;

import EDU.oswego.cs.dl.util.concurrent.WaitFreeQueue;

import desmoj.core.report.Reporter;
import desmoj.core.simulator.Experiment;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.Reportable;
import desmoj.core.simulator.SimTime;
import desmoj.core.simulator.TimeInstant;

/**
 * Implementation of a DesmoJ service for (single) experiments.
 */
public class JadexExperimentAusfuehrenService extends ABasicAnalysisSessionService implements IAExecuteExperimentsService
{

	/**
	 * Create a new DesmoJ Simulation Service
	 * 
	 * @param comp
	 *            The active generalComp.
	 */
	public JadexExperimentAusfuehrenService(IExternalAccess access)
	{
		super(access, IAExecuteExperimentsService.class, true);

	}

	// -------- methods --------

	/**
	 * Simulate an experiment
	 */
	public IFuture executeExperiment(UUID session, IAExperiment exp)
	{
		final Future res = new Future();
		// if (session==null) session = (UUID) createSession(null).get(susThread);

		// not integrated yet
		// JadexSessionView view = (JadexSessionView) sessionViews.get(session);
		// Integer executions = 0;
		// Integer replicationen = (Integer) exp.getExperimentParameter("Wiederholungen").getValue()-1;

		
		IComponentManagementService cms = (IComponentManagementService) SServiceProvider.getService(access.getServiceProvider(), IComponentManagementService.class).get(susThread);
		cms.createComponent("dm", "jadex/simulation/analysis/application/jadex/model/disastermanagement/DisasterManagement.application.xml",
				new CreationInfo(null, null, access.getComponentIdentifier(),
						false, false, false, false, access.getModel().getAllImports(), null), null).get(susThread);
		
//		res.setResult(exp);
		return res;
	}

	@Override
	public Set<Modeltype> supportedModels()
	{
		Set<Modeltype> result = new HashSet<Modeltype>();
		result.add(Modeltype.Jadex);
		return result;
	}
}
