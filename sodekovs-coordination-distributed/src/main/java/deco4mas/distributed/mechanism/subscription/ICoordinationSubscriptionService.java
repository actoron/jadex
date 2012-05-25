package deco4mas.distributed.mechanism.subscription;

import jadex.bridge.service.annotation.Timeout;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import deco4mas.distributed.mechanism.CoordinationInfo;

/**
 * A subscription-based service interface for coordination. Local mechanism implementation can subscribe this interface to get published {@link CoordinationInfo} for a specified coordination context.
 * 
 * @author Thomas Preisler
 */
public interface ICoordinationSubscriptionService {

	/**
	 * Subscribe to a specific coordination context. New {@link CoordinationInfo} that fit to the context are forwarded to all subscribers as intermediate results. A subscribe can unsubscribe by
	 * terminating the future.
	 * 
	 * @param coordinationContextId
	 *            The coordination context id.
	 * @return The coordination information.
	 */
	@Timeout(Timeout.NONE)
	public ISubscriptionIntermediateFuture<CoordinationInfo> subscribe(String coordinationContextId);

	/**
	 * Publish a new coordination info for a given coordination context.
	 * 
	 * @param coordinationContextId
	 *            The coordination context id.
	 * @param ci
	 *            The coordination information
	 */
	public IFuture<Void> publish(String coordinationContextId, CoordinationInfo ci);

	/**
	 * Get the service Id.
	 * 
	 * @return the service id
	 */
	public String getServiceId();
}
