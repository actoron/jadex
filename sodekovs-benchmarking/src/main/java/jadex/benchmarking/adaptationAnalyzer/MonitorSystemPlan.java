package jadex.benchmarking.adaptationAnalyzer;

import jadex.bdi.runtime.Plan;
import jadex.benchmarking.helper.Constants;
import jadex.benchmarking.model.AdaptationAnalysis;
import jadex.benchmarking.model.SuTinfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.extension.envsupport.environment.AbstractEnvironmentSpace;

/**
 * Responsible for managing the start and execution of the monitoring process. It an "IncidentEvent" is observed is starts a separate goal (MonitorSingleAdaptationGoal) to handle this single adaptation.
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
	private IComponentIdentifier sutCID = null;
	// Space of System Under Test
	private AbstractEnvironmentSpace sutSpace = null;
	// Exta of System Under Test
	private IExternalAccess sutExta = null;
	// Definition of the AdaptationAnalysis to be performed
	private AdaptationAnalysis adaptationAnalysis = null;
	// Logs events of the schedule
//	protected ScheduleLogger scheduleLogger = null;

	// -------- methods --------
	
	
	@Override
	public void body() {
		init((SuTinfo) getBeliefbase().getBelief(Constants.SUT_INFO).getFact());
		System.out.println("HEHEHEHEHE\\n\n\n\n\n" + adaptationAnalysis.getTargetDefinition().get(0).getIncidentEvent().toString());
		
	}

//	/*
//	 * Create and start a component.
//	 */
//	protected void createComponent(final Action action) {
//		HashMap<String, Object> componentProperties = Methods.propertyListToHashMapforObject(action.getProperties().getProperty());
//
//		if (action.getComponenttype() == null) {
//			System.out.println("Error: ComponentType not set!");
//		} else if (action.getComponenttype().equalsIgnoreCase(GlobalConstants.BDI_AGENT)) {
//			for (int i = 0; i < action.getNumberOfComponents(); i++) {
//				// Get random required in order to avoid creating components with the same name/id.
//				cms.createComponent(action.getComponentname() + "-" + GetRandom.getRandom(100000), action.getComponentmodel(), new CreationInfo(null, componentProperties, sutCID, false, false), null)
//						.addResultListener(new DefaultResultListener() {
//							public void resultAvailable(Object result) {
//								System.out.println("Created Component : " + " -> " + getTimestamp());
//								scheduleLogger.log("C: " + action.getComponentname());
//							}
//						});
//			}
//		} else if (action.getComponenttype().equalsIgnoreCase(GlobalConstants.ISPACE_OBJECT)) {
//			// Schedule step??
//			for (int i = 0; i < action.getNumberOfComponents(); i++) {
//				sutSpace.createSpaceObject(action.getComponentmodel(), componentProperties, null);
//				System.out.println("Created Component : " + action.getComponentmodel() + " -> " + getTimestamp());
//				scheduleLogger.log("C: " + action.getComponentname());
//			}
//		} else {
//			System.out.println("Error: ComponentType not supported! : " + action.getComponenttype());
//		}
//	}
//
//	/*
//	 * Delete (kill) and a component.
//	 */
//	protected void deleteComponent(final Action action) {
//		HashMap<String, Object> componentProperties = Methods.propertyListToHashMapforObject(action.getProperties().getProperty());
//
//		if (action.getComponenttype() == null) {
//			System.out.println("Error: ComponentType not set!");
//		} else if (action.getComponenttype().equalsIgnoreCase(GlobalConstants.BDI_AGENT)) {
//			// Search for component type
//			cms.getComponentDescriptions().addResultListener(new DefaultResultListener() {
//				public void resultAvailable(Object result) {
//					IComponentDescription[] descriptions = (IComponentDescription[]) result;
//					if (descriptions.length > 0) {
//						int destroyConunter = 0;
//
//						for (int i = 0; i < descriptions.length; i++) {
//							if (descriptions[i].getModelName().equalsIgnoreCase(action.getComponentmodel())) {
//								cms.destroyComponent(descriptions[i].getName()).addResultListener(new DefaultResultListener() {
//									public void resultAvailable(Object result) {
//										System.out.println("Destroyed Component : " + action.getComponentmodel() + " -> " + getTimestamp());
//										scheduleLogger.log("D: " + action.getComponentname());
//									}
//								});
//								destroyConunter++;
//								// check whether all components have been destroyed
//								if (destroyConunter == action.getNumberOfComponents()) {
//									break;
//								}
//							}
//						}
//					}
//				}
//			});
//		} else if (action.getComponenttype().equalsIgnoreCase(GlobalConstants.ISPACE_OBJECT)) {
//			// Schedule step??
//			ISpaceObject[] objects = sutSpace.getSpaceObjectsByType(action.getComponentmodel());
//			int destroyConunter = 0;
//
//			for (int i = 0; i < objects.length; i++) {
//				if (objects[i].getType().equalsIgnoreCase(action.getComponentmodel())) {
//					sutSpace.destroySpaceObject(objects[i].getId());
//					System.out.println("Destroyed Component : " + action.getComponentmodel() + " -> " + getTimestamp());
//					scheduleLogger.log("D: " + action.getComponentname());
//
//					destroyConunter++;
//					// check whether all components have been destroyed
//					if (destroyConunter == action.getNumberOfComponents()) {
//						break;
//					}
//				}
//			}
//		} else {
//			System.out.println("Error: ComponentType not supported! : " + action.getComponenttype());
//		}
//	}
//
//	/**
//	 * Compute start time of a component: relative to start time of benchmark
//	 * 
//	 * @param sequenceCounter
//	 * @return
//	 */
//	protected long computeStartTime(int sequenceCounter) {
//		if (sequenceCounter == 0) {
//			return sortedSequenceList.get(sequenceCounter).getStarttime();
//		} else {
//			return (sortedSequenceList.get(sequenceCounter).getStarttime() - sortedSequenceList.get(sequenceCounter - 1).getStarttime());
//		}
//	}

	/**
	 * Get time stamp relative to start of benchmark (without warm up phase)
	 * 
	 * @return
	 */
	protected long getTimestamp() {
		long starttime = ((Long) sutSpace.getProperty("REAL_START_TIME_OF_SIMULATION")).longValue();
		return clockservice.getTime() - starttime;
	}
	
	/**
	 * 
	 */
	private void init(SuTinfo sut) {
		cms = (IComponentManagementService) SServiceProvider.getService(getScope().getServiceContainer(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM).get(this);
		clockservice = (IClockService) getScope().getServiceContainer().getRequiredService("clockservice").get(this);
		adaptationAnalysis = sut.getAdaptationAnalysis();
		sutCID = sut.getSutCID();
		sutExta = sut.getSutExta();
		sutSpace = sut.getSutSpace();
	}

}
