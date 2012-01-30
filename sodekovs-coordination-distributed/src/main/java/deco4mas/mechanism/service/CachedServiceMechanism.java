/**
 * 
 */
package deco4mas.mechanism.service;

import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.commons.future.ThreadSuspendable;

import java.util.ArrayList;
import java.util.Collection;

import deco4mas.coordinate.environment.CoordinationSpace;
import deco4mas.mechanism.CoordinationInfo;

/**
 * @author thomas
 * 
 */
public class CachedServiceMechanism extends ServiceMechanism {

	private int interval = 0;

	private int perceiveCount = 0;

	private Collection<ICoordinationService> services = null;

	/**
	 * @param space
	 */
	public CachedServiceMechanism(CoordinationSpace space) {
		super(space);

		this.services = new ArrayList<ICoordinationService>();
	}

	@Override
	public void start() {
		super.start();

		this.interval = getMechanismConfiguration().getIntegerProperty("interval");
		if (this.interval <= 0) {
			this.interval = 1;
		}
	}

	@Override
	public void perceiveCoordinationEvent(Object obj) {
		CoordinationInfo ci = (CoordinationInfo) obj;

		if (perceiveCount <= 0 || perceiveCount == interval) {
			services = SServiceProvider.getServices(applicationInterpreter.getServiceProvider(), ICoordinationService.class, RequiredServiceInfo.SCOPE_GLOBAL).get(new ThreadSuspendable(this));
			perceiveCount = 0;

			System.out.println("Updated service cache.");
		}

		for (ICoordinationService service : services) {
			service.publish(ci);
		}

		perceiveCount++;
	}
}
