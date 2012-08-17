package deco4mas.distributed.coordinate.service;

import jadex.bridge.service.annotation.Service;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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

	private List<SubscriptionIntermediateFuture<CoordinationChangeEvent>> subscribers = null;

	public CoordinationSpaceService(CoordinationSpace space) {
		this.space = space;
		this.subscribers = new ArrayList<SubscriptionIntermediateFuture<CoordinationChangeEvent>>();
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

	/**
	 * Listener method which is called if a {@link CoordinationMechanism} was activated.
	 * 
	 * @param realization
	 *            the mechanisms realization name
	 */
	public void mechanismActivated(String realization) {
		CoordinationChangeEvent event = new CoordinationChangeEvent(realization, Boolean.TRUE);
		publish(event);
	}

	/**
	 * Informs all the subscribers if a {@link CoordinationChangeEvent} occurs.
	 * 
	 * @param event
	 *            the {@link CoordinationChangeEvent}
	 */
	private void publish(CoordinationChangeEvent event) {
		for (Iterator<SubscriptionIntermediateFuture<CoordinationChangeEvent>> it = subscribers.iterator(); it.hasNext();) {
			SubscriptionIntermediateFuture<CoordinationChangeEvent> sub = it.next();
			if (!sub.addIntermediateResultIfUndone(event)) {
				it.remove();
			}
		}
	}

	/**
	 * Listener method which is called if a {@link CoordinationMechanism} was deactivated.
	 * 
	 * @param realization
	 *            the mechanisms realization name
	 */
	public void mechanismDeactivated(String realization) {
		CoordinationChangeEvent event = new CoordinationChangeEvent(realization, Boolean.FALSE);
		publish(event);
	}

	/**
	 * Allows other to subscribe for {@link CoordinationChangeEvent}. The subscribers are called if any change happens on the spaces {@link CoordinationMechanism}s.
	 * 
	 * @return an {@link ISubscriptionIntermediateFuture} holding the {@link CoordinationChangeEvent}
	 */
	public ISubscriptionIntermediateFuture<CoordinationChangeEvent> subscribe() {
		SubscriptionIntermediateFuture<CoordinationChangeEvent> ret = new SubscriptionIntermediateFuture<CoordinationChangeEvent>();

		subscribers.add(ret);
		return ret;
	}
}
