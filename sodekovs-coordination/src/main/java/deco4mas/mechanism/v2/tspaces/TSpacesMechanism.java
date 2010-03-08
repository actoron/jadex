package deco4mas.mechanism.v2.tspaces;

import jadex.application.space.envsupport.environment.ISpaceObject;
import deco4mas.coordinate.environment.CoordinationSpace;
import deco4mas.mechanism.CoordinationInformation;
import deco4mas.mechanism.ICoordinationMechanism;

/**
 * 
 * Implements the "TSpaces" coordination mechanism.
 * 
 * @author Ante Vilenica
 * 
 */
public class TSpacesMechanism extends ICoordinationMechanism {

	private TSpacesClient tsClient;

	public TSpacesMechanism(CoordinationSpace space) {
		super(space);
	}

	public void start() {
		// Init TSpacesServer
		StartTSpacesServer.startServer("cooordination_tspace");

		tsClient = new TSpacesClient(space);
		System.out.println("#TSpacesMechanism# connected ? : " + tsClient.connect());
		tsClient.registerCallback();
		System.out.println("#TSpacesMechanism# initialised...");
	}

	public void stop() {
		// TODO Auto-generated method stub

	}

	public void suspend() {
		// TODO Auto-generated method stub

	}

	public void restart() {
		// TODO Auto-generated method stub
	}

	public void perceiveCoordinationEvent(Object obj) {
		if (obj instanceof CoordinationInformation) {
			tsClient.publish((CoordinationInformation) obj);
		} else {
			tsClient.publish((ISpaceObject) obj);
		}
	}
}
