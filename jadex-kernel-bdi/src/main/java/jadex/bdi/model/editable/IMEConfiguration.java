package jadex.bdi.model.editable;

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
	public IMEConfigBelief createInitialBelief(String ref);
	
	/**
	 *  Create an initial belief set.
	 *  @param ref The referenced element name.
	 */
	public IMEConfigBeliefSet createInitialBeliefSet(String ref);
	
	/**
	 *  Create an initial goal.
	 *  @param ref The referenced element name.
	 */
	public IMEConfigElement createInitialGoal(String ref);
	
	/**
	 *  Create an end goal.
	 *  @param ref The goal reference.
	 *  @return The end goal.
	 */
	public IMEConfigElement createEndGoal(String ref);
	
	/**
	 *  Create an initial plan.
	 *  @param ref The plan reference.
	 *  @return The initial plan.
	 */
	public IMEConfigElement createInitialPlan(String ref);
	
	/**
	 *  Create an end plan.
	 *  @param ref The plan reference.
	 *  @return The end plan.
	 */
	public IMEConfigElement createEndPlan(String ref);
	
	/**
	 *  Create an initial internal event.
	 *  @param ref The event reference.
	 *  @return The initial internal event.
	 */
	public IMEConfigElement createInitialInternalEvent(String ref);
	
	/**
	 *  Create an end internal event.
	 *  @param ref The event reference.
	 *  @return The end internal event.
	 */
	public IMEConfigElement createEndInternalEvent(String ref);
	
	/**
	 *  Create an initial message event.
	 *  @param ref The event reference.
	 *  @return The initial message event.
	 */
	public IMEConfigElement createInitialMessageEvent(String ref);
	
	/**
	 *  Create an end message event.
	 *  @param ref The event reference.
	 *  @return The end message event.
	 */
	public IMEConfigElement createEndMessageEvent(String ref);
}
