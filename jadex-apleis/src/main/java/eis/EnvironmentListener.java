package eis;

import eis.iilang.EnvironmentEvent;

/**
 * The environment interface can inform connected objects about changes using
 * this interface.
 * 
 * @author tristanbehrens
 *
 */
public interface EnvironmentListener {

	/**
	 * Handles an environment-event.
	 * 
	 * @param event
	 */
	void handleEnvironmentEvent(EnvironmentEvent event);

	/**
	 * Handles the event that an entity has been freed.
	 * @param entity
	 */
	void handleFreeEntity(String entity);
	
	/**
	 * Handles the event that an entity has been deleted.
	 * @param entity
	 */
	void handleDeletedEntity(String entity);

	/**
	 * Handles the event that an entity has been newly created.
	 * @param entity
	 */
	void handleNewEntity(String entity);
	
}
