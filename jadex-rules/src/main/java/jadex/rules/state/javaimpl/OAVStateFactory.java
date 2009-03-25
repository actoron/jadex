package jadex.rules.state.javaimpl;

import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVTypeModel;

/**
 *  Factory to decouple state creation.
 */
public class OAVStateFactory
{
	/**
	 *  Create a new OAV state for the given type model.
	 */
	public static IOAVState	createOAVState(OAVTypeModel model)
	{
//		return new OAVState(model);
//		return new OAVMixedWeakState(model);
		return new OAVContentIdState(model);
	}
}
