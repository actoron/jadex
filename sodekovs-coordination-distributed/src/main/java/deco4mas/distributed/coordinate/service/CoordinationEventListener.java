/**
 * 
 */
package deco4mas.distributed.coordinate.service;

import java.util.EventListener;

import deco4mas.distributed.coordinate.environment.CoordinationSpace;
import deco4mas.distributed.mechanism.CoordinationMechanism;

/**
 * Event Listener interface for listening on changes of the coordination process of the according {@link CoordinationSpace}.
 * 
 * @author Thomas Preisler
 */
public interface CoordinationEventListener extends EventListener {

	/**
	 * Listener method which is called if a {@link CoordinationMechanism} was activated.
	 * 
	 * @param realization
	 *            the mechanisms realization name
	 */
	public void mechanismActivated(String realization);

	/**
	 * Listener method which is called if a {@link CoordinationMechanism} was deactivated.
	 * 
	 * @param realization
	 *            the mechanisms realization name
	 */
	public void mechanismDeactivated(String realization);
}