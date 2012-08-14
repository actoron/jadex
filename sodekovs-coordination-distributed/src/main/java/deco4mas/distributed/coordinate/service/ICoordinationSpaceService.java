package deco4mas.distributed.coordinate.service;

import jadex.commons.future.Future;

import java.util.Collection;
import java.util.Map;

import deco4mas.distributed.coordinate.environment.CoordinationSpace;
import deco4mas.distributed.mechanism.CoordinationMechanism;

/**
 * A service the {@link CoordinationSpace} offers to view and manipulate its properties and coordination mechanisms.
 * 
 * @author Thomas Preisler
 */
public interface ICoordinationSpaceService {

	/**
	 * Gets all currently active {@link CoordinationMechanism}s of the according {@link CoordinationSpace}.
	 * 
	 * @return all active coordination mechanisms
	 */
	public Future<Collection<CoordinationMechanism>> getActiveCoordinationMechanisms();

	/**
	 * Gets all currently inactive {@link CoordinationMechanism}s of the according {@link CoordinationSpace}.
	 * 
	 * @return all inactive coordination mechanisms
	 */
	public Future<Collection<CoordinationMechanism>> getInactiveCoordinationMechanisms();

	/**
	 * Gets all {@link CoordinationMechanism}s as a {@link Map} with the {@link CoordinationMechanism} as key and a {@link Boolean} value indicating whether the mechanism is active or not as value.
	 * 
	 * @return all coordination mechanisms
	 */
	public Future<Map<CoordinationMechanism, Boolean>> getCoordinationMechanisms();
}
