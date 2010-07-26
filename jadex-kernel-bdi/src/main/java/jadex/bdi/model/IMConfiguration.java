package jadex.bdi.model;

import jadex.rules.state.OAVAttributeType;

public interface IMConfiguration
{
	/**
	 *  Get the initial capabilities.
	 *  @return The initial capabilities.
	 */
	public IMInitialCapability[] getInitialCapabilities();

	/**
	 *  Get the initial beliefs.
	 *  @return The initial beliefs.
	 */
	public IMConfigBelief[] getInitialBeliefs();
	
	/**
	 *  Get the initial belief sets.
	 *  @return The initial belief sets.
	 */
	public IMConfigBeliefSet[] getInitialBeliefSets();
	
	/**
	 *  Get the initial goals.
	 *  @return The initial goals.
	 */
	public IMConfigElement[] getInitialGoals();
	
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