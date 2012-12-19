/**
 * 
 */
package deco4mas.distributed.convergence;

import jadex.micro.MicroAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract super class for {@link MicroAgent}s with constraints which should be observed by {@link IMicroAgentConvergenceListener}.
 * 
 * @author Thomas Preisler
 */
public abstract class ConvergenceMicroAgent extends MicroAgent {

	/** The constraint values important for the convergence */
	private Map<String, Object> constraints = new HashMap<String, Object>();

	/** Map of listeners which should be notified if the constraint value changes, with the constraint keys as keys. */
	protected Map<String, List<IMicroAgentConvergenceListener>> eventListener = new HashMap<String, List<IMicroAgentConvergenceListener>>();

	/**
	 * Add an event listener.
	 * 
	 * @param listener
	 *            the given listener
	 */
	public void addEventListener(String constraintKey, IMicroAgentConvergenceListener listener) {
		if (eventListener.containsKey(constraintKey)) {
			eventListener.get(constraintKey).add(listener);
		} else {
			List<IMicroAgentConvergenceListener> listeners = new ArrayList<IMicroAgentConvergenceListener>();
			listeners.add(listener);
			eventListener.put(constraintKey, listeners);
		}
	}

	/**
	 * Remove an event listener.
	 * 
	 * @param listener
	 *            the listener to be removed
	 */
	public void removeEventListener(IMicroAgentConvergenceListener listener) {
		this.eventListener.remove(listener);
	}

	/**
	 * Returns the constraint value for the given constraint key.
	 * 
	 * @param constraintKey
	 *            the given constraint key
	 * @return the constraint value or <code>null</code> if no value for the given key exists.
	 */
	public Object getConstraintValue(String constraintKey) {
		return constraints.get(constraintKey);
	}

	public void setConstraintValue(String constraintKey, Object constraintValue) {
		this.constraints.put(constraintKey, constraintValue);
		List<IMicroAgentConvergenceListener> eventListener = this.eventListener.get(constraintKey);
		if (eventListener != null) {
			for (IMicroAgentConvergenceListener listener : eventListener) {
				listener.constraintChangend(new ConstraintChangeEvent(constraintKey, constraintValue, this));
			}
		}
	}
}