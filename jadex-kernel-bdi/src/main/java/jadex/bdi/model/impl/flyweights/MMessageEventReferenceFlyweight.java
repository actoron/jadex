package jadex.bdi.model.impl.flyweights;

import jadex.bdi.model.IMMessageEventReference;
import jadex.rules.state.IOAVState;

/**
 *  Message event reference flyweight.
 */
public class MMessageEventReferenceFlyweight extends MElementReferenceFlyweight implements IMMessageEventReference
{
	//-------- constructors --------
	
	/**
	 *  Create a new element flyweight.
	 */
	public MMessageEventReferenceFlyweight(IOAVState state, Object scope, Object handle)
	{
		super(state, scope, handle);
	}
}