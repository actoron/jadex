package jadex.adapter.base.envsupport.environment;

import jadex.commons.IPropertyObject;

import java.util.Map;

/**
 *  Interface for agent actions. These actions are asynchronous and
 *  first scheduled in the environment. They will be executed when the
 *  next step() is called.
 */
public interface IAgentAction extends IPropertyObject
{
	//-------- constants --------

	/** The constant for the object id parameter (target of the action). */
	public static final String OBJECT_ID = "object_id";
	
	/** The constant for the actor id parameter (agent performing the action). */
	public static final String ACTOR_ID  = "actor_id";

	//-------- methods --------

	/**
	 * Executes the delayed action. Called by the Executor.
	 * @param parameters parameters for the action
	 * @param space the environment space
	 */
	public Object perform(Map parameters, IEnvironmentSpace space);
}
