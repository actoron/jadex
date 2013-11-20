package jadex.bdiv3.examples.disastermanagement.movement;

import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.ContinuousSpace2D;

/**
 * 
 */
public interface IEnvAccess
{
	/**
	 *  Get the env.
	 *  @return The env.
	 */
	public ContinuousSpace2D getEnvironment();

	/**
	 *  Get the myself.
	 *  @return The myself.
	 */
	public ISpaceObject getMyself();
}	
