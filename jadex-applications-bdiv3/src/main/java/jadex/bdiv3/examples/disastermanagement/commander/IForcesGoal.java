package jadex.bdiv3.examples.disastermanagement.commander;

import jadex.extension.envsupport.environment.ISpaceObject;

import java.util.Collection;

/**
 * 
 */
public interface IForcesGoal
{
	/**
	 *  Get the disaster.
	 *  @return The disaster.
	 */
	public ISpaceObject getDisaster();

	/**
	 *  Get the units.
	 *  @return The units.
	 */
	public Collection<Object> getUnits();
}
