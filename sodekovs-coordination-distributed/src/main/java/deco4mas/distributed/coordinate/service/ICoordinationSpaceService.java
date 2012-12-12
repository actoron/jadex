package deco4mas.distributed.coordinate.service;

import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;

import java.util.Collection;
import java.util.Map;

import deco.distributed.lang.dynamics.mechanism.MechanismConfiguration;
import deco4mas.distributed.coordinate.environment.CoordinationSpace;
import deco4mas.distributed.mechanism.CoordinationMechanism;

/**
 * A service the {@link CoordinationSpace} offers to view and manipulate its properties and coordination mechanisms.
 * 
 * @author Thomas Preisler
 */
public interface ICoordinationSpaceService extends CoordinationEventListener {

	/**
	 * Gets all currently active {@link CoordinationMechanism}s of the according {@link CoordinationSpace}.
	 * 
	 * @return all active coordination mechanisms
	 */
	public IFuture<Collection<CoordinationMechanism>> getActiveCoordinationMechanisms();

	/**
	 * Gets all currently inactive {@link CoordinationMechanism}s of the according {@link CoordinationSpace}.
	 * 
	 * @return all inactive coordination mechanisms
	 */
	public IFuture<Collection<CoordinationMechanism>> getInactiveCoordinationMechanisms();

	/**
	 * Gets all {@link CoordinationMechanism}s as a {@link Map} with the {@link CoordinationMechanism} as key and a {@link Boolean} value indicating whether the mechanism is active or not as value.
	 * 
	 * @return all coordination mechanisms
	 */
	public IFuture<Map<CoordinationMechanism, Boolean>> getCoordinationMechanisms();

	/**
	 * Activates the {@link CoordinationMechanism} given by its realization name by removing it from the list of inactive mechanisms from the services space
	 * {@link CoordinationSpace#inactiveCoordinationMechanisms} and adding it to the list of active mechanisms {@link CoordinationSpace#activeCoordinationMechanisms}. Also the
	 * {@link CoordinationMechanism#start()} method is called to start the mechanism.
	 * 
	 * @param mechanism
	 *            the given {@link CoordinationMechanism} to activate
	 */
	public IFuture<Void> activateCoordinationMechanism(String realization);

	/**
	 * Deactivates the {@link CoordinationMechanism} given by its realization name by removing it from the list of active mechanisms from the services space
	 * {@link CoordinationSpace#activeCoordinationMechanisms} and adding it to the list of inactive mechanisms {@link CoordinationSpace#inactiveCoordinationMechanisms}. Also the
	 * {@link CoordinationMechanism#stop()} method is called to stop the mechanism.
	 * 
	 * @param mechanism
	 *            the given {@link CoordinationMechanism} to deactivate
	 */
	public IFuture<Void> deactivateCoordinationMechanism(String realization);

	/**
	 * Allows other to subscribe for {@link CoordinationChangeEvent}. The subscribers are called if any change happens on the spaces {@link CoordinationMechanism}s.
	 * 
	 * @return an {@link ISubscriptionIntermediateFuture} holding the {@link CoordinationChangeEvent}
	 */
	public ISubscriptionIntermediateFuture<CoordinationChangeEvent> subscribe();

	/**
	 * Returns the {@link MechanismConfiguration} of the {@link CoordinationMechanism} given by the realization name.
	 * 
	 * @param realization
	 *            the given realization name
	 * @return the {@link MechanismConfiguration}
	 */
	public IFuture<MechanismConfiguration> getCoordinationMechanismConfiguration(String realization);

	/**
	 * Changes the {@link MechanismConfiguration} for {@link CoordinationMechanism} given by the realization name. By changing the properties for the given key value pair.
	 * 
	 * @param realization
	 *            the realization name
	 * @param key
	 *            the given key
	 * @param value
	 *            the given value
	 * @return
	 */
	public IFuture<Void> changeCoordinationMechanismConfiguration(String realization, String key, String value);
	
	/**
	 * Used for distributed case: denotes the context of a distributed application. In order to recognize, all instances of a distributed application have to use the same CoordinationCotextID, which
	 * is defined in the *.application.xml.
	 * 
	 * @return
	 */
	public String getCoordinationContextID();
}
