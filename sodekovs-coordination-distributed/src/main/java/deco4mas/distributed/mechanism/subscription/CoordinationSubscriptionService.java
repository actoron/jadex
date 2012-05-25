package deco4mas.distributed.mechanism.subscription;

import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import deco4mas.distributed.mechanism.CoordinationInfo;

/**
 * Implementation of the {@link ICoordinationSubscriptionService} interface.
 * 
 * @author Thomas Preisler
 */
@Service
public class CoordinationSubscriptionService implements ICoordinationSubscriptionService {

	/** The map of subscribers. */
	protected Map<String, List<SubscriptionIntermediateFuture<CoordinationInfo>>> subscribers;

	/** The service Id. */
	protected String serviceId;

	/**
	 * Constructor with the given service id.
	 * 
	 * @param serviceID
	 *            the service id.
	 */
	public CoordinationSubscriptionService(String serviceID) {
		this.subscribers = new HashMap<String, List<SubscriptionIntermediateFuture<CoordinationInfo>>>();
		this.serviceId = serviceID;
	}

	/**
	 * Subscribe to a specific coordination context. New {@link CoordinationInfo} that fit to the context are forwarded to all subscribers as intermediate results. A subscribe can unsubscribe by
	 * terminating the future.
	 * 
	 * @param coordinationContextId
	 *            The coordination context id.
	 * @return The coordination information.
	 */
	public ISubscriptionIntermediateFuture<CoordinationInfo> subscribe(String coordinationContextId) {
		SubscriptionIntermediateFuture<CoordinationInfo> ret = new SubscriptionIntermediateFuture<CoordinationInfo>();

		List<SubscriptionIntermediateFuture<CoordinationInfo>> subs = subscribers.get(coordinationContextId);
		if (subs == null) {
			subs = new ArrayList<SubscriptionIntermediateFuture<CoordinationInfo>>();
			subscribers.put(coordinationContextId, subs);
		}
		subs.add(ret);

		return ret;
	}

	/**
	 * Publish a new coordination info for a given coordination context.
	 * 
	 * @param coordinationContextId
	 *            The coordination context id.
	 * @param ci
	 *            The coordination information
	 */
	public IFuture<Void> publish(String coordinationContextId, CoordinationInfo ci) {
		List<SubscriptionIntermediateFuture<CoordinationInfo>> subs = subscribers.get(coordinationContextId);
		if (subs != null) {
			for (Iterator<SubscriptionIntermediateFuture<CoordinationInfo>> it = subs.iterator(); it.hasNext();) {
				SubscriptionIntermediateFuture<CoordinationInfo> sub = it.next();
				if (!sub.addIntermediateResultIfUndone(ci)) {
					System.out.println("Removed: " + sub);
					it.remove();
				}
			}
			if (subs.isEmpty())
				subscribers.remove(coordinationContextId);
		}

		return IFuture.DONE;
	}

	/**
	 * Get the service Id.
	 * 
	 * @return the service id
	 */
	public String getServiceId() {
		return this.serviceId;
	}
}