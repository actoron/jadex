/**
 * 
 */
package deco4mas.distributed.jcc.service;

import jadex.bridge.service.IService;
import jadex.commons.future.Future;

import java.util.Collection;

import deco4mas.distributed.coordinate.service.ICoordinationSpaceService;
import deco4mas.distributed.jcc.viewer.CoordinationPanel;

/**
 * The coordination management service manages the handling of all found {@link ICoordinationSpaceService} for the {@link CoordinationPanel}.
 * 
 * @author Thomas Preisler
 */
public interface ICoordinationManagementService extends IService {

	/**
	 * Gets all the {@link ICoordinationSpaceService}s from the local platform.
	 * 
	 * @param refresh
	 *            specifies whether the cached results showed be returned or a new search should be performed. <code>false</code> returns the cached results, <code>true</code> performs a new search.
	 * @return all found {@link ICoordinationSpaceService}s
	 */
	public Future<Collection<ICoordinationSpaceService>> getCoordSpaceServices(boolean refresh);
}