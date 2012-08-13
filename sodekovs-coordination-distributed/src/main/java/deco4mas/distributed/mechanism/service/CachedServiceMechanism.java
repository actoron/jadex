/**
 * 
 */
package deco4mas.distributed.mechanism.service;

import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.commons.future.ThreadSuspendable;

import java.util.ArrayList;
import java.util.Collection;

import deco4mas.distributed.coordinate.environment.CoordinationSpace;
import deco4mas.distributed.mechanism.CoordinationInfo;

/**
 * Jadex Service based Coordination Medium which allows the coordination of distributed Jadex applications by distributing the {@link CoordinationInfo}s via Jadex Services. This Mechanism caches the
 * Coordination Services and only looks for new Services after a specified number of perceived Coordination Events.
 * 
 * @author Thomas Preisler
 */
public class CachedServiceMechanism extends ServiceMechanism {

	/**
	 * The number of perceived Coordination Events after which the Mechanism looks for new Coordination services.
	 */
	private int interval = 0;

	/**
	 * The number of perceived Coordination Events after the last caching.
	 */
	private int perceiveCount = 0;

	/**
	 * The cached {@link ICoordinationService}s
	 */
	private Collection<ICoordinationService> services = null;

	/**
	 * Default Constructor.
	 * 
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
		if (this.interval < 0) {
			this.interval = 0;
		}
	}

	@Override
	public void stop() {
		super.stop();
		perceiveCount = 0;
		this.services = new ArrayList<ICoordinationService>();
	}

	@Override
	public void perceiveCoordinationEvent(Object obj) {
		CoordinationInfo ci = (CoordinationInfo) obj;

		// only search for new services if it is the initial search or if perceiveCount == interval and interval != 0.
		// if a interval of 0 is specified the mechanism searches for the services only once at the initial perceived coordination event.
		if (perceiveCount <= 0 || (perceiveCount == interval && interval != 0)) {
			services = SServiceProvider.getServices(applicationInterpreter.getServiceProvider(), ICoordinationService.class, RequiredServiceInfo.SCOPE_GLOBAL).get(new ThreadSuspendable(this));
			perceiveCount = 0;

			for (ICoordinationService service : services) {
				if (!service.getCoordinationContextID().equalsIgnoreCase(coordinationContextID)) {
					services.remove(service);
				}
			}

			System.out.println("Updated service cache.");
		}

		for (ICoordinationService service : services) {
			service.publish(ci);
		}

		perceiveCount++;
	}
}
