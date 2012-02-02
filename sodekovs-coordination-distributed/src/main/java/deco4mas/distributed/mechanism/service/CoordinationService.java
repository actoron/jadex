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

	public CoordinationService(CoordinationSpace space) {
		this.space = space;
	}

	@Override
	public IFuture<Void> publish(CoordinationInfo ci) {
		space.publishCoordinationEvent(ci);

		return IFuture.DONE;
	}
}
