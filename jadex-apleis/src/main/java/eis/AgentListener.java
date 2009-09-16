package eis;

import eis.iilang.Percept;

/**
 * The environment interface can inform connected objects about changes using
 * this interface.
 * 
 * @author tristanbehrens
 *
 */
public interface AgentListener {

	/**
	 * Handles an event that is sent to a specific agent-
	 * 
	 * @param agent
	 * @param event
	 */
	void handlePercept(String agent, Percept percept);

}
