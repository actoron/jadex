package jadex.bdi.model.impl.flyweights;

import jadex.bdi.model.IMQueryGoal;
import jadex.rules.state.IOAVState;

/**
 *  Flyweight for query goal. 
 */
public class MQueryGoalFlyweight extends MGoalFlyweight implements IMQueryGoal
{
	//-------- constructors --------
	
	/**
	 *  Create a new element flyweight.
	 */
	public MQueryGoalFlyweight(IOAVState state, Object scope, Object handle)
	{
		super(state, scope, handle);
	}
}
