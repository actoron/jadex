package jadex.bdi.model.impl.flyweights;

import jadex.bdi.model.IMExpressionReference;
import jadex.rules.state.IOAVState;

/**
 *  Belief model reference flyweight.
 */
public class MExpressionReferenceFlyweight extends MElementReferenceFlyweight implements IMExpressionReference
{
	//-------- constructors --------
	
	/**
	 *  Create a new element flyweight.
	 */
	public MExpressionReferenceFlyweight(IOAVState state, Object scope, Object handle)
	{
		super(state, scope, handle);
	}
}