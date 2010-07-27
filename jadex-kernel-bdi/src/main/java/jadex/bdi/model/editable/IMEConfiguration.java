package jadex.bdi.model.editable;

import jadex.bdi.model.IMConfigElement;
import jadex.bdi.model.IMConfiguration;

/**
 * 
 */
public interface IMEConfiguration extends IMConfiguration, IMEElement
{
	/**
	 *  Create an initial capability.
	 *  @param ref The referenced capability name.
	 *  @param conf The name of configuration to use.
	 */
	public void createInitialCapability(String ref, String conf);

	/**
	 *  Create an initial belief.
	 *  @param ref The referenced element name.
	 */
	public IMEBelief createInitialBelief(String ref);
	
	/**
	 *  Create an initial belief set.
	 *  @param ref The referenced element name.
	 */
	public IMEBeliefSet createInitialBeliefSet(String ref);
	
	/**
	 *  Create the initial goal.
	 *  @param ref The referenced element name.
	 */
	public IMConfigElement createInitialGoal(String ref);
	
	/**
	 *  Get the end goals.
	 *  @return The end goals.
	 */
	public IMConfigElement[] getEndGoals();
	
	/**
	 *  Get the initial plans.
	 *  @return The initial plans.
	 */
	public IMConfigElement[] getInitialPlans();
	
	/**
	 *  Get the end plans.
	 *  @return The end plans.
	 */
	public IMConfigElement[] getEndPlans();
	
	/**
	 *  Get the initial internal events.
	 *  @return The initial internal events.
	 */
	public IMConfigElement[] getInitialInternalEvents();
	
	/**
	 *  Get the end internal events.
	 *  @return The end internal events.
	 */
	public IMConfigElement[] getEndInternalEvents();
	
	/**
	 *  Get the initial message events.
	 *  @return The initial message events.
	 */
	public IMConfigElement[] getInitialMessageEvents();
	
	/**
	 *  Get the end message events.
	 *  @return The end message events.
	 */
	public IMConfigElement[] getEndMessageEvents();
}
