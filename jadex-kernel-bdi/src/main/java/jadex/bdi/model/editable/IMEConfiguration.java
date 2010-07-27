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
	 *  Create an initial goal.
	 *  @param ref The referenced element name.
	 */
	public IMEConfigElement createInitialGoal(String ref);
	
	/**
	 *  Create an end goal.
	 *  @param ref The goal reference.
	 *  @return The end goal.
	 */
	public IMConfigElement createEndGoal(String ref);
	
	/**
	 *  Create an initial plan.
	 *  @param ref The plan reference.
	 *  @return The initial plan.
	 */
	public IMConfigElement createInitialPlan(String ref);
	
	/**
	 *  Create an end plan.
	 *  @param ref The plan reference.
	 *  @return The end plan.
	 */
	public IMConfigElement createEndPlan(String ref);
	
	/**
	 *  Create an initial internal event.
	 *  @param ref The event reference.
	 *  @return The initial internal event.
	 */
	public IMConfigElement createInitialInternalEvent(String ref);
	
	/**
	 *  Create an end internal event.
	 *  @param ref The event reference.
	 *  @return The end internal event.
	 */
	public IMConfigElement createEndInternalEvent(String ref);
	
	/**
	 *  Create an initial message event.
	 *  @param ref The event reference.
	 *  @return The initial message event.
	 */
	public IMConfigElement createInitialMessageEvent(String ref);
	
	/**
	 *  Create an end message event.
	 *  @param ref The event reference.
	 *  @return The end message event.
	 */
	public IMConfigElement createEndMessageEvent(String ref);
}
