package deco4mas.distributed.mechanism.service;

import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;
import deco4mas.distributed.coordinate.environment.CoordinationSpace;
import deco4mas.distributed.mechanism.CoordinationInfo;

/**
 * A Service which publishes received {@link CoordinationInfo}s to its local {@link CoordinationSpace}.
 * 
 * @author Thomas Preisler
 */
@Service
public class CoordinationService implements ICoordinationService {

	private CoordinationSpace space = null;
	
	//Used for distributed case: denotes the context of a distributed application.
	private String coordinationContextID;

	public CoordinationService(CoordinationSpace space, String coordinationContextID) {
		this.space = space;
		setCoordinationContextID(coordinationContextID);
	}

	@Override
	public IFuture<Void> publish(CoordinationInfo ci) {
		space.publishCoordinationEvent(ci);

		return IFuture.DONE;
	}

	@Override
	public void setCoordinationContextID(String id) {
		this.coordinationContextID = id;
		
	}

	@Override
	public String getCoordinationContextID() { 
		return this.coordinationContextID;
	}
}
