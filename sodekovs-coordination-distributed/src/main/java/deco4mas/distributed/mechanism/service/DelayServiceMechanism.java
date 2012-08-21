/**
 * 
 */
package deco4mas.distributed.mechanism.service;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;

import java.util.Collection;

import deco4mas.distributed.coordinate.environment.CoordinationSpace;
import deco4mas.distributed.mechanism.CoordinationInfo;

/**
 * @author thomas
 * 
 */
public class DelayServiceMechanism extends ServiceMechanism {

	/**
	 * Constructor
	 * 
	 * @param space
	 *            the {@link CoordinationSpace}
	 */
	public DelayServiceMechanism(CoordinationSpace space) {
		super(space);
	}

	@Override
	public void perceiveCoordinationEvent(final Object obj) {
		Integer timeout = getMechanismConfiguration().getIntegerProperty("timeout");
		System.out.println("DelayServiceMechnism timeout is " + timeout);
		applicationInterpreter.waitForDelay(timeout, new IComponentStep<Void>() {

			@Override
			public IFuture<Void> execute(IInternalAccess ia) {
				final CoordinationInfo ci = (CoordinationInfo) obj;

				SServiceProvider.getServices(applicationInterpreter.getServiceProvider(), ICoordinationService.class, RequiredServiceInfo.SCOPE_GLOBAL).addResultListener(
						new DefaultResultListener<Collection<ICoordinationService>>() {

							@Override
							public void resultAvailable(Collection<ICoordinationService> result) {

								for (ICoordinationService service : result) {

									if (service.getCoordinationContextID().equalsIgnoreCase(coordinationContextID)) {
										service.publish(ci);
									} else {
										System.out.println("Service does not belong to context.");
									}
								}

							}
						});
				return IFuture.DONE;
			}
		});

	}
}
