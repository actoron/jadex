package jadex.bdi.model.impl.flyweights;

import jadex.bdi.model.IMInternalEvent;
import jadex.bdi.model.editable.IMEInternalEvent;
import jadex.rules.state.IOAVState;

/**
 *  Flyweight for internal event model element.
 */
public class MInternalEventFlyweight extends MProcessableElementFlyweight implements IMInternalEvent, IMEInternalEvent
{
	//-------- constructors --------
	
	/**
	 *  Create a new internal event flyweight.
	 */
	public MInternalEventFlyweight(IOAVState state, Object scope, Object handle)
	{
		super(state, scope, handle);
	}
}
