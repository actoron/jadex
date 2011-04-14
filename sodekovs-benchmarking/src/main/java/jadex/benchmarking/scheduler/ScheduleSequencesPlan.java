package jadex.benchmarking.scheduler;

import jadex.application.runtime.IApplicationExternalAccess;
import jadex.application.space.envsupport.environment.AbstractEnvironmentSpace;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.benchmarking.helper.Constants;
import jadex.benchmarking.helper.Methods;
import jadex.benchmarking.model.Action;
import jadex.benchmarking.model.Sequence;
import jadex.benchmarking.model.SuTinfo;
import jadex.bridge.CreationInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.SServiceProvider;
import jadex.bridge.service.clock.IClockService;
import jadex.commons.future.DefaultResultListener;

import java.util.ArrayList;
import java.util.HashMap;

import sodekovs.util.math.GetRandom;
import sodekovs.util.misc.GlobalConstants;

public class ScheduleSequencesPlan extends Plan {

	private IComponentManagementService cms = null;
	private IClockService clockservice = null;
	private ArrayList<Sequence> sortedSequenceList = null;
	// Component Identifier of System Under Test
	private IComponentIdentifier sutCID = null;
	// Exta of System Under Test
	private IApplicationExternalAccess sutExta = null;
	// Space of System Under Test
	private AbstractEnvironmentSpace sutSpace = null;

	public void body() {

		init((SuTinfo) getBeliefbase().getBelief(Constants.SUT_INFO).getFact());

		// responsible to start the components
		startScheduler();
	}

	/**
	 * Responsible for starting sequences in time-order according to benchmark
	 */
	private void startScheduler() {
		// sequenceCounter = denotes current sequence being executed
		for (int sequenceCounter = 0; sequenceCounter < sortedSequenceList.size(); sequenceCounter++) {
			Sequence nextSequence = sortedSequenceList.get(sequenceCounter);
			// wait till next sequence has to be performed.
			waitFor(computeStartTime(sequenceCounter));

			// If this sequence has to be repeated, start a goal that handles this sequences separately.
			if (nextSequence.getRepeatConfiguration() != null) {
				// Dispatch separate goal to handle sequence 
				IGoal eval = (IGoal) getGoalbase().createGoal("SequenceRepeaterGoal");
				eval.getParameter("args").setValue(new SuTinfo(sortedSequenceList, sutCID, sutExta, sutSpace));		
				eval.getParameter("sequence").setValue(nextSequence);				
				getGoalbase().dispatchTopLevelGoal(eval);		
			} else {
				for (Action nextAction : nextSequence.getActions().getAction()) {

					if (nextSequence.getActiontype().equalsIgnoreCase(Constants.CREATE)) {
						createComponent(nextAction);
					} else if (nextSequence.getActiontype().equalsIgnoreCase(Constants.DELETE)) {
						// TODO:
					}
				}
			}
		}
	}

	/*
	 * Create and start a component.
	 */
	private void createComponent(Action action) {
		HashMap<String, String> componentProperties = Methods.propertyListToHashMap(action.getProperties().getProperty());

		if (action.getComponenttype() == null) {
			System.out.println("Error: ComponentType not set!");
		} else if (action.getComponenttype().equalsIgnoreCase(GlobalConstants.BDI_AGENT)) {
			for (int i = 0; i < action.getNumberOfComponents(); i++) {
				//Get random required in order to avoid creating components with the same name/id.
				cms.createComponent(action.getComponentname() + "-" + GetRandom.getRandom(100000), action.getComponentmodel(), new CreationInfo("", componentProperties, sutCID, false, false), null).addResultListener(
						new DefaultResultListener() {
							public void resultAvailable(Object result) {
								System.out.println("Created Component : " + " -> " + getTimestamp());
							}
						});
			}
			// IComponentIdentifier cid = (IComponentIdentifier) fut.get(this);
			// IBDIExternalAccess ie = (IBDIExternalAccess) cms.getExternalAccess(cid).get(this);
		} else if (action.getComponenttype().equalsIgnoreCase(GlobalConstants.ISPACE_OBJECT)) {
			// Schedule step??
			// Map props = new HashMap();
			// Vector2Double pos = new Vector2Double(0.8, 0.8);
			// props.put("position", pos);
			for (int i = 0; i < action.getNumberOfComponents(); i++) {
				sutSpace.createSpaceObject(action.getComponentmodel(), componentProperties, null);
				System.out.println("Created Component : " + action.getComponentmodel() + " -> " + getTimestamp());
			}

			//
			// waitFor(3000);
			//
			// props = new HashMap();
			// pos = new Vector2Double(0.6, 0.6);
			// props.put("position", pos);
			// ISpaceObject objI = sutSpace.createSpaceObject("sentry", props, null);
		} else {
			System.out.println("Error: ComponentType not supported! : " + action.getComponenttype());
		}
	}

	/**
	 * Compute start time of a component: relative to start time of benchmark
	 * 
	 * @param sequenceCounter
	 * @return
	 */
	private long computeStartTime(int sequenceCounter) {
		if (sequenceCounter == 0) {
			return sortedSequenceList.get(sequenceCounter).getStarttime();
		} else {
			return (sortedSequenceList.get(sequenceCounter).getStarttime() - sortedSequenceList.get(sequenceCounter - 1).getStarttime());
		}
	}

	/**
	 * Get time stamp relative to start of benchmark (without warm up phase)
	 * 
	 * @return
	 */
	private long getTimestamp() {
		long starttime = ((Long) sutSpace.getProperty("BENCHMARK_REAL_START_TIME_OF_SIMULATION")).longValue();
		return clockservice.getTime() - starttime;
	}

	/**
	 * 
	 */
	private void init(SuTinfo sut) {
		cms = (IComponentManagementService) SServiceProvider.getService(getScope().getServiceContainer(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM).get(this);
		clockservice = (IClockService) SServiceProvider.getService(getScope().getServiceContainer(), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM).get(this);
		sortedSequenceList = sut.getSortedSequenceList();
		sutCID = sut.getSutCID();
		sutExta = sut.getSutExta();
		sutSpace = sut.getSutSpace();
	}
}
