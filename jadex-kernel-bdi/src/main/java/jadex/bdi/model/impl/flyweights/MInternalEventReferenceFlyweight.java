package jadex.bdi.model.impl.flyweights;

import jadex.bdi.model.IMInternalEventReference;
import jadex.bdi.model.editable.IMEInternalEventReference;
import jadex.rules.state.IOAVState;

/**
 *  Internal event reference flyweight.
 */
public class MInternalEventReferenceFlyweight extends MElementReferenceFlyweight implements IMInternalEventReference, IMEInternalEventReference
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