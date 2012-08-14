package deco4mas.distributed.coordinate.service;

import jadex.bridge.service.annotation.Service;
import deco4mas.distributed.coordinate.environment.CoordinationSpace;

/**
 * Implementation of {@link ICoordinationSpaceService}.
 * 
 * @author Thomas Preisler
 */
@Service
public class CoordinationSpaceService implements ICoordinationSpaceService {

	private CoordinationSpace space = null;

	public CoordinationSpaceService(CoordinationSpace space) {
		this.space = space;
	}
}
