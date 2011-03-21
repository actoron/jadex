package jadex.benchmarking.manager;

import jadex.application.runtime.IApplicationExternalAccess;
import jadex.application.space.envsupport.environment.AbstractEnvironmentSpace;
import jadex.application.space.envsupport.math.Vector2Double;
import jadex.bdi.runtime.Plan;
import jadex.benchmarking.helper.Constants;
import jadex.benchmarking.helper.Methods;
import jadex.benchmarking.model.Action;
import jadex.benchmarking.model.Schedule;
import jadex.benchmarking.model.Sequence;
import jadex.bridge.CreationInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.SServiceProvider;
import jadex.bridge.service.clock.IClockService;
import jadex.commons.future.IFuture;
import jadex.simulation.helper.GetRandom;
import jadex.simulation.helper.XMLHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class InitBenchmarkingPlan extends Plan {

	private IComponentManagementService cms = null;
	private IClockService clockservice = null;
	private ArrayList<Sequence> sortedSequenceList = null;
	//Component Identifier of System Under Test
	private IComponentIdentifier sutCID = null;
	//Exta of System Under Test
	private IApplicationExternalAccess sutExta = null;
	//Space of System Under Test
	private AbstractEnvironmentSpace sutSpace = null;

	public void body() {

		cms = (IComponentManagementService) SServiceProvider.getService(getScope().getServiceProvider(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM).get(this);
		clockservice = (IClockService) SServiceProvider.getService(getScope().getServiceProvider(), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM).get(this);

		String benchmarkDescription = (String) getBeliefbase().getBelief("scheduleDescriptionFile").getFact();
		System.out.println("#InitBench# Init Benchmark Agent with configuration file: " + benchmarkDescription);
		Schedule benchConf = (Schedule) XMLHandler.parseXMLFromXMLFile(benchmarkDescription, Schedule.class);
		
		// Create list of sequences, ordered by their start time
		sortedSequenceList = (ArrayList<Sequence>) benchConf.getSequences().getSequence();
		createOrderedSequenceList(sortedSequenceList);
		
		// Start System under Test (SuT) if required, i.e. has not been started by another component yet.
		//SUT is started in suspend mode.
		if (benchConf.getSytemUnderTest() != null) {
			startSuT(benchConf);
		}
		
		// resume system under test
		cms.resumeComponent(sutCID);

		// Warm up phase
		if (benchConf.getWarmUpTime() != null) {
			System.out.println("#InitBench# Started warm up phase of : " + benchConf.getWarmUpTime() + " msec.");
			waitFor(benchConf.getWarmUpTime());
		}

		//start first component
		startComponent(sortedSequenceList.get(0).getActions().getAction().get(0));
	}

	/*
	 * Start System under Test. Required if system has not been started yet.
	 */
	private void startSuT(Schedule schedule) {

		HashMap<String, String> sutProperties = Methods.propertyListToHashMap(schedule.getSytemUnderTest().getProperties().getProperty());

		// create SuT in suspended modus
		IFuture fut = cms.createComponent(schedule.getName() + GetRandom.getRandom(100000), sutProperties.get(Constants.APPLICATION_FILE_PATH),
				new CreationInfo(sutProperties.get(Constants.APPLICATION_COONFIGURATION), new HashMap(), null, true, false), null);
		sutCID = (IComponentIdentifier) fut.get(this);
		sutExta = (IApplicationExternalAccess) cms.getExternalAccess(sutCID).get(this);

		// TODO: Hack: Synchronize start time!
		long startTime = clockservice.getTime();
		sutSpace = ((AbstractEnvironmentSpace) (sutExta).getSpace(sutProperties.get(Constants.SPACE_NAME)));
		sutSpace.setProperty("BENCHMARK_REAL_START_TIME_OF_SIMULATION", startTime);
		
	}
	
	/*
	 * Create and start a component.
	 */
	private void startComponent(Action action) {
			
		// create component
//		IFuture fut = cms.createComponent(action.getComponentname(), action.getComponentmodel(),
//				new CreationInfo("", new HashMap(), null, false, false), null);
//		sutCID = (IComponentIdentifier) fut.get(this);
//		sutExta = (IApplicationExternalAccess) cms.getExternalAccess(sutCID).get(this);
		
//		space.createSpaceObject(typename, properties, tasks)
		//Schedule step??
		Map props = new HashMap();
		Vector2Double pos = new Vector2Double(0.8, 0.8);
		props.put("position", pos);		
		sutSpace.createSpaceObject("homebase", props, null);
		
		props = new HashMap();
		pos = new Vector2Double(0.6, 0.6);
		props.put("position", pos);		
		sutSpace.createSpaceObject("sentry", props, null);
		
		

	}
	

	
	/**
	 * Returns the list of sequences events ascendingly ordered by relative start time.
	 */
	private void createOrderedSequenceList(ArrayList<Sequence> list) {
		Collections.sort(list, new Comparator() {
			public int compare(Object arg0, Object arg1) {
				return Long.valueOf(((Sequence) arg0).getStarttime()).compareTo(Long.valueOf(((Sequence) arg1).getStarttime()));
			}
		});
	}
}
