package jadex.extension.envsupport.environment;

import java.util.Map;

import jadex.commons.IPropertyObject;

/**
 *  Interface for space actions. These actions can be executed
 *  synchronously or asynchronously in the space according to
 *  the perform method chosen.
 */
public interface ISpaceAction extends IPropertyObject
{
	//-------- constants --------

	/** The constant for the object id parameter (target of the action). */
	public static final String OBJECT_ID = "object_id";
	
	/** The constant for the actor id parameter (component performing the action). */
	public static final String ACTOR_ID  = "actor_id";

	//-------- methods --------

	/**
	 * Executes the delayed action. Called by the Executor.
	 * @param parameters parameters for the action
	 * @param space the environment space
	 */
	public Object perform(Map<String, Object> parameters, IEnvironmentSpace space);
}
