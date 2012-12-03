package deco4mas.distributed.mechanism.service;

import jadex.bridge.service.IInternalService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.BasicServiceInvocationHandler;
import jadex.bridge.service.search.SServiceProvider;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import jadex.kernelbase.StatelessAbstractInterpreter;

import java.util.Collection;
import java.util.HashMap;

import deco4mas.distributed.coordinate.environment.CoordinationSpace;
import deco4mas.distributed.mechanism.CoordinationInfo;
import deco4mas.distributed.mechanism.CoordinationMechanism;

/**
 * Jadex Service based Coordination Medium which allows the coordination of distributed Jadex applications by distributing the {@link CoordinationInfo}s via Jadex Services.
 * 
 * @author Thomas Preisler
 */
public class ServiceMechanism extends CoordinationMechanism {

	/** The applications interpreter */
	protected StatelessAbstractInterpreter applicationInterpreter = null;

	/** The identifier of the offered coordination service */
	protected IServiceIdentifier serviceIdentifier = null;

	/**
	 * Default Constructor.
	 * 
	 * @param space
	 *            the {@link CoordinationSpace}
	 */
	public ServiceMechanism(CoordinationSpace space) {
		super(space);

		// TODO Der Cast ist ein Hack bis Lars und Alex die Schnittstellen von Jadex anpassen
		this.applicationInterpreter = (StatelessAbstractInterpreter) space.getApplicationInternalAccess();

		// If it's a distributed application, then it has a contextID.
		HashMap<String, Object> appArgs = (HashMap<String, Object>) this.applicationInterpreter.getArguments();
		this.coordinationContextID = (String) appArgs.get("CoordinationContextID");
	}

	@Override
	public void start() {
		String name = "CoordinationService@" + applicationInterpreter.getComponentIdentifier().toString();
		addService(name, ICoordinationService.class, new CoordinationService(space, this.coordinationContextID));
	}

	@Override
	public void perceiveCoordinationEvent(Object obj) {
		final CoordinationInfo ci = (CoordinationInfo) obj;

		SServiceProvider.getServices(applicationInterpreter.getServiceProvider(), ICoordinationService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(
				new DefaultResultListener<Collection<ICoordinationService>>() {

					@Override
					public void resultAvailable(Collection<ICoordinationService> result) {
						for (ICoordinationService service : result) {

							if (service.getCoordinationContextID().equalsIgnoreCase(coordinationContextID)) {
								System.out.println("#ServiceMechanism# Publishing CoordinationInformation " + ci.toString() + " using following mechanism:  " + service.toString());
								service.publish(ci);
							} else {
								System.out.println("Service does not belong to context.");
							}
						}
					}
					
					@Override
					public void exceptionOccurred(Exception exception) {
						exception.printStackTrace();
					}
				});
	}

	/**
	 * Adds the given Service to the application.
	 * 
	 * @param name
	 *            the service name
	 * @param type
	 *            the service type
	 * @param service
	 *            the actual service instance
	 */
	private void addService(String name, Class<?> type, Object service) {
		IFuture<IInternalService> result = applicationInterpreter.addService(name, type, BasicServiceInvocationHandler.PROXYTYPE_DECOUPLED, null, service, null);
		result.addResultListener(new DefaultResultListener<IInternalService>() {

			@Override
			public void resultAvailable(IInternalService is) {
				serviceIdentifier = is.getServiceIdentifier();
			}
		});
	}

	@Override
	public void stop() {
		if (serviceIdentifier != null)
			applicationInterpreter.getServiceContainer().removeService(serviceIdentifier);
	}
}