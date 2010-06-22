package jadex.bdi.runtime.impl;

import jadex.bdi.runtime.impl.flyweights.ElementFlyweight;
import jadex.rules.state.IOAVState;

/**
 * 
 */
public interface IFlyweightCreator
{
	/**
	 * 
	 */
	public ElementFlyweight createFlyweight(IOAVState state, Object scope, Object handle);
}

