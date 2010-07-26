package jadex.bdi.model.impl.flyweights;

import jadex.rules.state.IOAVState;

/**
 *  Flyweight for perform goal model element.
 */
public class MPerformGoalFlyweight extends MGoalFlyweight
{
	//-------- constructors --------
	
	/**
	 *  Create a new element flyweight.
	 */
	public MPerformGoalFlyweight(IOAVState state, Object scope, Object handle)
	{
		super(state, scope, handle);
	}
}
