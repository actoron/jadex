package jadex.bdi.model.impl.flyweights;

import jadex.bdi.model.IMInternalEventReference;
import jadex.rules.state.IOAVState;

/**
 *  Internal event reference flyweight.
 */
public class MInternalEventReferenceFlyweight extends MElementReferenceFlyweight implements IMInternalEventReference
{
	//-------- constructors --------
	
	/**
	 *  Create a new element flyweight.
	 */
	public MInternalEventReferenceFlyweight(IOAVState state, Object scope, Object handle)
	{
		super(state, scope, handle);
	}
}