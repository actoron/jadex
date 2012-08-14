/**
 * 
 */
package deco4mas.distributed.jcc.service;

import jadex.bridge.service.BasicService;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;

import java.util.Collection;
import java.util.Map;

import deco4mas.distributed.coordinate.service.ICoordinationSpaceService;

/**
 * Implementation of {@link ICoordinationManagementService}.
 * 
 * @author Thomas Preisler
 */
public class CoordinationManagementService extends BasicService implements ICoordinationManagementService {

	/** The local service provider */
	private IServiceProvider provider;

	/** The cached {@link ICoordinationSpaceService}s */
	private Collection<ICoordinationSpaceService> coordSpaceServices;

	/**
	 * Create a standalone CoordinationManagementService.
	 */
	public CoordinationManagementService(IServiceProvider provider) {
		this(provider, null);
	}

	/**
	 * Create a standalone CoordinationManagementService.
	 */
	public CoordinationManagementService(IServiceProvider provider, Map<String, Object> properties) {
		super(provider.getId(), ICoordinationManagementService.class, properties);

		this.provider = provider;
	}

	/**
	 * Gets all the {@link ICoordinationSpaceService}s from the local platform.
	 * 
	 * @param refresh
	 *            specifies whether the cached results showed be returned or a new search should be performed. <code>false</code> returns the cached results, <code>true</code> performs a new search.
	 * @return all found {@link ICoordinationSpaceService}s
	 */
	public Future<Collection<ICoordinationSpaceService>> getCoordSpaceServices(boolean refresh) {
		final Future<Collection<ICoordinationSpaceService>> fut = new Future<Collection<ICoordinationSpaceService>>();
		if (coordSpaceServices == null || coordSpaceServices.isEmpty() || refresh) {
			SServiceProvider.getServices(provider, ICoordinationSpaceService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(
					new ExceptionDelegationResultListener<Collection<ICoordinationSpaceService>, Collection<ICoordinationSpaceService>>(fut) {

						@Override
						public void customResultAvailable(Collection<ICoordinationSpaceService> result) {
							coordSpaceServices = result;
							fut.setResult(coordSpaceServices);

						}
					});
		} else {
			fut.setResult(coordSpaceServices);
		}

		return fut;
	}
}
