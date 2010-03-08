package deco4mas.coordinate.interpreter.agent_state;


import jadex.application.space.envsupport.environment.IEnvironmentSpace;
import deco4mas.coordinate.environment.CoordinationSpace;
import deco4mas.mechanism.CoordinationInformation;

/**
 * 
 * @author Ante Vilenica
 */
public class BDICoordinationEventPublication implements ICoordinationEventPublication {

	/**
	 * Default constructor.
	 */
	public BDICoordinationEventPublication() {
		super();
		// init();
	}

	public void publishEvent(CoordinationInformation event, IEnvironmentSpace abstractSpace) {
//		System.out.println("#BDICoordinationEventPublication# called publish event...");
		
//		ISpaceObject newObj = abstractSpace.createSpaceObject("coordinationPrototype", event.getProperties(), null);
//		System.out.println("#BDICoordinationEvent#: " + newObj);
//		((AbstractEnvironmentSpace) abstractSpace).fireEnvironmentEvent(new EnvironmentEvent(CoordinationEvent.COORDINATE_START, abstractSpace, newObj, new String("Coordinate Event Nr. ...")));

		
//		((CoordinationSpace) abstractSpace).perceiveCoordinationEvent(newObj);
		
//		System.out.println("#BDICoordinationEventPublication# toString: " + newObj.toString());
//		CoordinationInfo coordInfo = new CoordinationInfo();
//		coordInfo.setName("Test-Coord-Info");
//		coordInfo.setType(CoordinationSpaceObject.COORDINATION_INFORMATION_TYPE);
//		coordInfo.addValue(event.getProperty("Counter").toString(),newObj);
		((CoordinationSpace) abstractSpace).perceiveCoordinationEvent(event);

		
		
		
		//		PerceptType perceptType = ((Grid2D) abstractSpace).getPerceptType("coordination_test");
//		((Grid2D) abstractSpace).
//		Set agentTypes = perceptType.getAgentTypes();
//		Iterator agentIter = agentTypes.iterator();
//		
//		while(agentIter.hasNext()){
//			System.out.println("#BDICoordinationEventPublication# Got Percept:" + perceptType.getName() + " - " + agentIter.next());	
//		}
//		
//		agentTypes.add(new String("Sender"));
//		perceptType.setAgentTypes(agentTypes);
		
	}
	//
	// /**
	// * Init the listeners of the agent
	// */
	// private void init(){
	//		
	// //Belief listener
	//		
	//		
	// }
}
