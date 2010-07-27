package jadex.bdi.model.impl.flyweights;

import jadex.bdi.model.IMBeliefReference;
import jadex.bdi.model.editable.IMEBeliefReference;
import jadex.rules.state.IOAVState;

/**
 *  Belief model reference flyweight.
 */
public class MBeliefReferenceFlyweight extends MElementReferenceFlyweight implements IMBeliefReference, IMEBeliefReference
{
	//-------- constructors --------
	
	/**
	 *  Create a new referenceable element flyweight.
	 */
	public MBeliefReferenceFlyweight(IOAVState state, Object scope, Object handle)
	{
		super(state, scope, handle);
	}
}
