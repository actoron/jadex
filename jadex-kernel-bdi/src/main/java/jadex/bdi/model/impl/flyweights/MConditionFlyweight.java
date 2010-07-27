package jadex.bdi.model.impl.flyweights;

import jadex.bdi.model.IMCondition;
import jadex.bdi.model.editable.IMECondition;
import jadex.rules.state.IOAVState;

/**
 * 
 */
public class MConditionFlyweight extends MExpressionFlyweight implements IMCondition, IMECondition
{
	//-------- constructors --------
	
	/**
	 *  Create a new element flyweight.
	 */
	public MConditionFlyweight(IOAVState state, Object scope, Object handle)
	{
		super(state, scope, handle);
	}
}
