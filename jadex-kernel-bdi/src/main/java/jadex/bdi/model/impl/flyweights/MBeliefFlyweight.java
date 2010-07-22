package jadex.bdi.model.impl.flyweights;

import jadex.bdi.model.IMBelief;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.rules.state.IOAVState;

/**
 *  Belief model element.
 */
public class MBeliefFlyweight extends MTypedElementFlyweight implements IMBelief
{
	//-------- constructors --------
	
	/**
	 *  Create a new element flyweight.
	 *  @param state	The state.
	 *  @param scope	The scope handle.
	 *  @param handle	The element handle.
	 *  @param rplan	The calling plan (if called from plan)
	 */
	public MBeliefFlyweight(IOAVState state, Object scope, Object handle)
	{
		super(state, scope, handle);
	}
	
	//-------- methods --------
	
	/**
	 *  Get the clazz.
	 *  @return The clazz. 
	 * /
	public IMExpression getFactExpression()
	{
		throw new UnsupportedOperationException();
		
//		if(getInterpreter().isExternalThread())
//		{
//			AgentInvocation invoc = new AgentInvocation()
//			{
//				public void run()
//				{
//					getState().getAttributeValue(getHandle(), OAVBDIMetaModel.belief_has_fact);
//				}
//			};
//			return invoc.string;
//		}
//		else
//		{
//			return null;
//		}
	}*/
	
	/**
	 *  Test if the belief is used as argument.
	 *  @return True if used as argument. 
	 */
	public boolean isArgument()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					bool = ((Boolean)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.belief_has_argument)).booleanValue();
				}
			};
			return invoc.bool;
		}
		else
		{
			return ((Boolean)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.belief_has_argument)).booleanValue();
		}
	}
	
	/**
	 *  Test if the belief is used as result.
	 *  @return True if used as result. 
	 */
	public boolean isResult()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					bool = ((Boolean)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.belief_has_result)).booleanValue();
				}
			};
			return invoc.bool;
		}
		else
		{
			return ((Boolean)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.belief_has_result)).booleanValue();
		}
	}
}
