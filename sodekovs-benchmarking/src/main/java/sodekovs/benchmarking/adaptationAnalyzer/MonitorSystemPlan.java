package sodekovs.benchmarking.adaptationAnalyzer;

import jadex.bdi.runtime.IGoal;
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

import java.util.ArrayList;

import sodekovs.benchmarking.helper.Constants;
import sodekovs.benchmarking.model.AdaptationAnalysis;
import sodekovs.benchmarking.model.SuTinfo;
import sodekovs.benchmarking.model.TargetDefinition;
import sodekovs.util.misc.AgentMethods;
import sodekovs.util.misc.EvaluateExpression;
import sodekovs.util.misc.GlobalConstants;

/**
 * Responsible for managing the start and execution of the monitoring process. If an "IncidentEvent" is observed is starts a separate goal (MonitorSingleAdaptationGoal) to handle this single adaptation.
 */
public class MonitorSystemPlan extends Plan {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6975083111569362187L;
	// -------- constants --------
	private IComponentManagementService cms = null;
	private IClockService clockservice = null;
	// Component Identifier of System Under Test
//	private IComponentIdentifier sutCID = null;
	// Space of System Under Test
	private AbstractEnvironmentSpace sutSpace = null;
	// Exta of System Under Test
//	private IExternalAccess sutExta = null;
	// Definition of the AdaptationAnalysis to be performed
	private AdaptationAnalysis adaptationAnalysis = null;
	// Logs events of the schedule
//	protected ScheduleLogger scheduleLogger = null;

	// -------- methods --------
	
	
	@Override
	public void body() {
		init((SuTinfo) getBeliefbase().getBelief(Constants.SUT_INFO).getFact());
		System.out.println("#MonitorSystemPlan# Starting init.");
		startMonitoring();
		
	}
	
	private void startMonitoring(){
		ArrayList<TargetDefinition> allTargets = (ArrayList<TargetDefinition>) adaptationAnalysis.getTargetDefinition();
		
		while (allTargets.size() > 0) {
			waitFor(1000);
			ArrayList<TargetDefinition> removeTargetsList = new ArrayList<TargetDefinition>();
				for(TargetDefinition target : allTargets){					
					//Check start condition for adaptation, i.e. IncidentEvent.					
					boolean targetCond = false;
					
					// Hack/Limitations: Works right now only for single objects but not for all of that type...
					// Additionally: only one part of the equation can be an object...
					if (target.getIncidentEvent().getObjectSource().getType().equalsIgnoreCase(GlobalConstants.ISPACE_OBJECT)) {
						targetCond = EvaluateExpression.evaluate(sutSpace, target.getIncidentEvent().getCondition(), target.getIncidentEvent().getObjectSource().getName(), target.getIncidentEvent().getObjectSource().getType());
					} else {
						IComponentIdentifier agentIdentifier = AgentMethods.getIComponentIdentifier(sutSpace,  target.getIncidentEvent().getObjectSource().getName());
						IFuture fut = cms.getExternalAccess(agentIdentifier);
						IExternalAccess exta = (IExternalAccess) fut.get(this);

						IOAVState state = ((ElementFlyweight) exta).getState();
						Object rCapability = ((ElementFlyweight) exta).getScope();

						// Evaluate condition/expression
						OAVBDIFetcher fetcher = new OAVBDIFetcher(state, rCapability);
						targetCond = EvaluateExpression.evaluateExpression(fetcher, target.getIncidentEvent().getCondition());
					}
					
					// System has experienced the IncidentEvent, i.e. the first phase of the adaptation starts
					if (targetCond) {
						//Start separate goal that handles the following steps of the adaptation process. 
						System.out.println("#MonitorSystemPlan-" + target.getName() + "# Detected IncidentEvent for new AdaptationProcess. Starting separate goal to handle this. ->" + sutSpace.getSpaceObjectsByType("homebase")[0].getProperty("ore"));
						IGoal eval = (IGoal) getGoalbase().createGoal("MonitorSingleAdaptationGoal");
	
						eval.getParameter("target").setValue(target);				
//						eval.getParameter("scheduleLogger").setValue(scheduleLogger);
						getGoalbase().dispatchTopLevelGoal(eval);		

						removeTargetsList.add(target);
					}
				}
				//Purpose: try only those targets next round that have not already been activated.
				for(int i=0; i<removeTargetsList.size();i++){
					allTargets.remove(removeTargetsList.get(i));
				}
		}
	}

	/**
	 * 
	 */
	private void init(SuTinfo sut) {
		cms = (IComponentManagementService) SServiceProvider.getService(getScope().getServiceContainer(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM).get(this);
		clockservice = (IClockService) getScope().getServiceContainer().getRequiredService("clockservice").get(this);
		adaptationAnalysis = sut.getAdaptationAnalysis();
//		sutCID = sut.getSutCID();
//		sutExta = sut.getSutExta();
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
