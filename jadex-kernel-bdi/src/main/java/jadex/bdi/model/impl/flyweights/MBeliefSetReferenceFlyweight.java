package jadex.bdi.model.impl.flyweights;

import jadex.bdi.model.IMBeliefSetReference;
import jadex.rules.state.IOAVState;

/**
 *  Beliefset model reference flyweight.
 */
public class MBeliefSetReferenceFlyweight extends MElementReferenceFlyweight implements IMBeliefSetReference
{
	//-------- constructors --------
	
	/**
	 *  Create a new referenceable element flyweight.
	 */
	public MBeliefSetReferenceFlyweight(IOAVState state, Object scope, Object handle)
	{
		super(state, scope, handle);
	}
}
