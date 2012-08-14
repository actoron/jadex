package deco4mas.distributed.coordinate.service;

import jadex.bridge.service.annotation.Service;
import jadex.commons.future.Future;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import deco4mas.distributed.coordinate.environment.CoordinationSpace;
import deco4mas.distributed.mechanism.CoordinationMechanism;

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

	/**
	 * Gets all currently active {@link CoordinationMechanism}s of the according {@link CoordinationSpace}.
	 * 
	 * @return all active coordination mechanisms
	 */
	public Future<Collection<CoordinationMechanism>> getActiveCoordinationMechanisms() {
		Future<Collection<CoordinationMechanism>> ret = new Future<Collection<CoordinationMechanism>>();
		ret.setResult(space.getActiveCoordinationMechanisms());

		return ret;
	}

	/**
	 * Gets all currently inactive {@link CoordinationMechanism}s of the according {@link CoordinationSpace}.
	 * 
	 * @return all inactive coordination mechanisms
	 */
	public Future<Collection<CoordinationMechanism>> getInactiveCoordinationMechanisms() {
		Future<Collection<CoordinationMechanism>> ret = new Future<Collection<CoordinationMechanism>>();
		ret.setResult(space.getInactiveCoordinationMechanisms());

		return ret;
	}

	/**
	 * Gets all {@link CoordinationMechanism}s as a {@link Map} with the {@link CoordinationMechanism} as key and a {@link Boolean} value indicating whether the mechanism is active or not as value.
	 * 
	 * @return all coordination mechanisms
	 */
	public Future<Map<CoordinationMechanism, Boolean>> getCoordinationMechanisms() {
		Future<Map<CoordinationMechanism, Boolean>> ret = new Future<Map<CoordinationMechanism, Boolean>>();

		Map<CoordinationMechanism, Boolean> mechanisms = new HashMap<CoordinationMechanism, Boolean>();
		for (CoordinationMechanism mechanism : space.getActiveCoordinationMechanisms()) {
			mechanisms.put(mechanism, Boolean.TRUE);
		}
		for (CoordinationMechanism mechanism : space.getInactiveCoordinationMechanisms()) {
			mechanisms.put(mechanism, Boolean.FALSE);
		}

		ret.setResult(mechanisms);
		return ret;
	}
}
