package deco4mas.distributed.coordinate.service;

import jadex.bridge.service.annotation.Service;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

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
	public IFuture<Collection<CoordinationMechanism>> getActiveCoordinationMechanisms() {
		Future<Collection<CoordinationMechanism>> ret = new Future<Collection<CoordinationMechanism>>();
		ret.setResult(space.getActiveCoordinationMechanisms().values());

		return ret;
	}

	/**
	 * Gets all currently inactive {@link CoordinationMechanism}s of the according {@link CoordinationSpace}.
	 * 
	 * @return all inactive coordination mechanisms
	 */
	public IFuture<Collection<CoordinationMechanism>> getInactiveCoordinationMechanisms() {
		Future<Collection<CoordinationMechanism>> ret = new Future<Collection<CoordinationMechanism>>();
		ret.setResult(space.getInactiveCoordinationMechanisms().values());

		return ret;
	}

	/**
	 * Gets all {@link CoordinationMechanism}s as a {@link Map} with the {@link CoordinationMechanism} as key and a {@link Boolean} value indicating whether the mechanism is active or not as value.
	 * 
	 * @return all coordination mechanisms
	 */
	public IFuture<Map<CoordinationMechanism, Boolean>> getCoordinationMechanisms() {
		Future<Map<CoordinationMechanism, Boolean>> ret = new Future<Map<CoordinationMechanism, Boolean>>();

		Map<CoordinationMechanism, Boolean> mechanisms = new HashMap<CoordinationMechanism, Boolean>();
		for (CoordinationMechanism mechanism : space.getActiveCoordinationMechanisms().values()) {
			mechanisms.put(mechanism, Boolean.TRUE);
		}
		for (CoordinationMechanism mechanism : space.getInactiveCoordinationMechanisms().values()) {
			mechanisms.put(mechanism, Boolean.FALSE);
		}

		ret.setResult(mechanisms);
		return ret;
	}

	/**
	 * Activates the {@link CoordinationMechanism} given by its realization name by removing it from the list of inactive mechanisms from the services space
	 * {@link CoordinationSpace#inactiveCoordinationMechanisms} and adding it to the list of active mechanisms {@link CoordinationSpace#activeCoordinationMechanisms}. Also the
	 * {@link CoordinationMechanism#start()} method is called to start the mechanism.
	 * 
	 * @param mechanism
	 *            the given {@link CoordinationMechanism} to activate
	 */
	public IFuture<Void> activateCoordinationMechanism(String realization) {
		space.activateCoordinationMechanism(realization);
		return IFuture.DONE;
	}

	/**
	 * Deactivates the {@link CoordinationMechanism} given by its realization name by removing it from the list of active mechanisms from the services space
	 * {@link CoordinationSpace#activeCoordinationMechanisms} and adding it to the list of inactive mechanisms {@link CoordinationSpace#inactiveCoordinationMechanisms}. Also the
	 * {@link CoordinationMechanism#stop()} method is called to stop the mechanism.
	 * 
	 * @param mechanism
	 *            the given {@link CoordinationMechanism} to deactivate
	 */
	public IFuture<Void> deactivateCoordinationMechanism(String realization) {
		space.deactivateCoordinationMechanism(realization);
		return IFuture.DONE;
	}
}
