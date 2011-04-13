package jadex.benchmarking.scheduler;

import jadex.application.runtime.IApplicationExternalAccess;
import jadex.application.space.envsupport.environment.AbstractEnvironmentSpace;
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

public class SequenceRepeaterPlan extends Plan {

	private IComponentManagementService cms = null;
	private IClockService clockservice = null;
	private ArrayList<Sequence> sortedSequenceList = null;
	// Component Identifier of System Under Test
	private IComponentIdentifier sutCID = null;
	// Exta of System Under Test
	private IApplicationExternalAccess sutExta = null;
	// Space of System Under Test
	private AbstractEnvironmentSpace sutSpace = null;
	// Sequence to be executed
	private Sequence sequence = null;

	public void body() {
		System.out.println("Started repeater seq goal...");

		init((SuTinfo) getParameter("args").getValue(), (Sequence) getParameter("sequence").getValue());

		// execute first run, since it does not depend on the repeater type
		executeSequence();

		// Next executions of sequence depend on the repeater (type)
		if (sequence.getRepeatConfiguration().getType().equalsIgnoreCase(Constants.SPACE)) {
			startSpaceSequenceRepeater();
		} else if (sequence.getRepeatConfiguration().getType().equalsIgnoreCase(Constants.LIST)) {
			startListSequenceRepeater();
		} else {
			System.out.println("SequenceRepeaterPlan# : Error: RepeatConfiguration -> type unknown!");
		}
	}

	/**
	 * Responsible for starting a sequence, defined as a space of values
	 */
	private void startSpaceSequenceRepeater() {
		long currentValue = sequence.getStarttime();
		long stepSize = Long.valueOf(sequence.getRepeatConfiguration().getStep()).longValue();
		boolean end = sequence.getRepeatConfiguration().getEnd() == null ? true : false;

		do {
			// wait for next step
			waitFor(stepSize);
			// execute sequence again
			executeSequence();
			currentValue += stepSize;
			System.out.println("SpaceRepeater: Executed -> " + currentValue);
			// execute till "end condition" has been reached. if no end is specified, continue till benchmark terminates.
		} while (end || (currentValue < Long.valueOf(sequence.getRepeatConfiguration().getEnd()).longValue()));
	}

	/**
	 * Responsible for starting a sequence, defined as a list of values
	 */
	private void startListSequenceRepeater() {
		ArrayList<String> values = Methods.getValuesAsList(sequence.getRepeatConfiguration().getValues());
		long currentValue = sequence.getStarttime();

		for (int i = 0; i < values.size(); i++) {
			// wait for next step
			waitFor(Long.valueOf(values.get(i)).longValue() - currentValue);
			// execute sequence again
			executeSequence();
			currentValue = Long.valueOf(values.get(i)).longValue();
			System.out.println("ListRepeater: Executed -> " + currentValue);
		}
	}

	private void executeSequence() {
		for (Action nextAction : sequence.getActions().getAction()) {
			if (sequence.getActiontype().equalsIgnoreCase(Constants.CREATE)) {
				createComponent(nextAction);
			} else if (sequence.getActiontype().equalsIgnoreCase(Constants.DELETE)) {
				// TODO:
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
				cms.createComponent(action.getComponentname() + "-" + GetRandom.getRandom(100000) + i, action.getComponentmodel(), new CreationInfo("", componentProperties, sutCID, false, false),
						null).addResultListener(new DefaultResultListener() {
					public void resultAvailable(Object result) {
						System.out.println("Created Component : " + " -> " + getTimestamp());
					}
				});
			}

		} else if (action.getComponenttype().equalsIgnoreCase(GlobalConstants.ISPACE_OBJECT)) {

			for (int i = 0; i < action.getNumberOfComponents(); i++) {
				sutSpace.createSpaceObject(action.getComponentmodel(), componentProperties, null);
				System.out.println("Created Component : " + action.getComponentmodel() + " -> " + getTimestamp());
			}

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
	private void init(SuTinfo sut, Sequence sequence) {
		cms = (IComponentManagementService) SServiceProvider.getService(getScope().getServiceProvider(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM).get(this);
		clockservice = (IClockService) SServiceProvider.getService(getScope().getServiceProvider(), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM).get(this);
		sortedSequenceList = sut.getSortedSequenceList();
		sutCID = sut.getSutCID();
		sutExta = sut.getSutExta();
		sutSpace = sut.getSutSpace();
		this.sequence = sequence;
	}
}
