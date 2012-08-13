/**
 * 
 */
package deco4mas.distributed.mechanism.subscription;

import jadex.commons.future.ISubscriptionIntermediateFuture;
import deco4mas.distributed.mechanism.CoordinationInfo;

/**
 * Utility class encapsulating a service id and the according {@link ISubscriptionIntermediateFuture} (subscription).
 * 
 * @author Thomas Preisler
 */
public class SubscribedService {

	private String serviceId = null;

	private ISubscriptionIntermediateFuture<CoordinationInfo> subscription = null;

	/**
	 * @param serviceId
	 * @param subscription
	 */
	public SubscribedService(String serviceId, ISubscriptionIntermediateFuture<CoordinationInfo> subscription) {
		super();
		this.serviceId = serviceId;
		this.subscription = subscription;
	}

	/**
	 * @return the serviceId
	 */
	public String getServiceId() {
		return serviceId;
	}

	/**
	 * @param serviceId
	 *            the serviceId to set
	 */
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	/**
	 * @return the subscription
	 */
	public ISubscriptionIntermediateFuture<CoordinationInfo> getSubscription() {
		return subscription;
	}

	/**
	 * @param subscription
	 *            the subscription to set
	 */
	public void setSubscription(ISubscriptionIntermediateFuture<CoordinationInfo> subscription) {
		this.subscription = subscription;
	}
}