package sodekovs.benchmarking.adaptationAnalyzer;

import jadex.bdi.runtime.Plan;
import jadex.bdi.runtime.impl.flyweights.ElementFlyweight;
import jadex.bdi.runtime.interpreter.OAVBDIFetcher;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.IFuture;
import jadex.extension.envsupport.environment.AbstractEnvironmentSpace;
import jadex.rules.state.IOAVState;
import sodekovs.benchmarking.helper.Constants;
import sodekovs.benchmarking.model.ObjectSource;
import sodekovs.benchmarking.model.SuTinfo;
import sodekovs.benchmarking.model.TargetDefinition;
import sodekovs.util.misc.AgentMethods;
import sodekovs.util.misc.EvaluateExpression;
import sodekovs.util.misc.GlobalConstants;

/**
 * Responsible for managing the observation process of a single adaptation. IncidentEvent has been already observed by the calling component. This plan observes the remaining two steps of the process:
 * 1.)AdaptationEvent and 2.)SystemReadyEvent.
 */
public class MonitorSingleAdaptationPlan extends Plan {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6975083115693621387L;

	// -------- variables --------
	private IComponentManagementService cms = null;
	private IClockService clockservice = null;
	// Space of System Under Test
	private AbstractEnvironmentSpace sutSpace = null;
	// Definition of the Adaptation to be monitored
	private TargetDefinition targetDefinition = null;

	// -------- methods --------

	@Override
	public void body() {
		init((SuTinfo) getBeliefbase().getBelief(Constants.SUT_INFO).getFact());
		System.out.println("Starting monitoring of single Adaptation Process for: " + targetDefinition.getName());

		// Start monitoring of the AdaptationEvent
		startMonitoring(targetDefinition.getAdaptationEvent().getCondition(), targetDefinition.getAdaptationEvent().getObjectSource());
		System.out.println("#MonitorSingleAdaptation-" + targetDefinition.getName() + "# Detected AdaptationEvent for AdaptationProcess.  ->" + sutSpace.getSpaceObjectsByType("homebase")[0].getProperty("ore"));

		// Start monitoring of the SystemReadyEvent
		startMonitoring(targetDefinition.getSystemReadyEvent().getCondition(), targetDefinition.getSystemReadyEvent().getObjectSource());
		System.out.println("#MonitorSingleAdaptation-" + targetDefinition.getName() + "# Detected SystemReadyEvent for AdaptationProcess. Terminating monitoring for this Adaptation.  ->" + sutSpace.getSpaceObjectsByType("homebase")[0].getProperty("ore"));
	}

	/**
	 * Monitor AdaptationEvents and SystemReadyEvents
	 */
	private void startMonitoring(String condition, ObjectSource objSource) {

		boolean targetCond = false;

		while (!targetCond) {
			waitFor(1000);

			// Hack/Limitations: Works right now only for single objects but not for all of that type...
			// Additionally: only one part of the equation can be an object...
			if (objSource.getType().equalsIgnoreCase(GlobalConstants.ISPACE_OBJECT)) {
				targetCond = EvaluateExpression.evaluate(sutSpace, condition, objSource.getName(), objSource.getType());
			} else {
				IComponentIdentifier agentIdentifier = AgentMethods.getIComponentIdentifier(sutSpace, objSource.getName());
				IFuture fut = cms.getExternalAccess(agentIdentifier);
				IExternalAccess exta = (IExternalAccess) fut.get(this);

				IOAVState state = ((ElementFlyweight) exta).getState();
				Object rCapability = ((ElementFlyweight) exta).getScope();

				// Evaluate condition/expression
				OAVBDIFetcher fetcher = new OAVBDIFetcher(state, rCapability);
				targetCond = EvaluateExpression.evaluateExpression(fetcher, condition);
			}
		}
	}

	/**
	 * 
	 */
	protected void init(SuTinfo sut) {		
		cms = (IComponentManagementService) SServiceProvider.getService(getScope().getServiceContainer(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM).get(this);
		clockservice = (IClockService) getScope().getServiceContainer().getRequiredService("clockservice").get(this);
		// sortedSequenceList = sut.getSortedSequenceList();
		targetDefinition = (TargetDefinition) getParameter("target").getValue();
		// sutCID = sut.getSutCID();
		// sutExta = sut.getSutExta();
		sutSpace = sut.getSutSpace();
	}

	/**
	 * Get time stamp relative to start of benchmark (without warm up phase)
	 * 
	 * @return
	 */
	protected long getTimestamp() {
		long starttime = ((Long) sutSpace.getProperty("REAL_START_TIME_OF_SIMULATION")).longValue();
		return clockservice.getTime() - starttime;
	}
}
