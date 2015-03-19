package jadex.bdi.runtime.impl.flyweights;

import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.rules.state.IOAVState;


/**
 *  Flyweight for processable elements.
 */
public abstract class ProcessableElementFlyweight extends ParameterElementFlyweight
{
	//-------- constructors --------
	
	/**
	 *  Create a new parameter element flyweight.
	 *  @param state	The state.
	 *  @param scope	The scope handle.
	 *  @param handle	The element handle.
	 */
	protected ProcessableElementFlyweight(IOAVState state, Object scope, Object handle)
	{
		super(state, scope, handle);
	}
	
	//-------- event flags --------

	/**
	 *  Get the post-to-all flag.
	 *  @return True, if post-to-all. 
	 */
	public boolean isPostToAll()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object me = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model);
					bool = ((Boolean)getState().getAttributeValue(me, OAVBDIMetaModel.processableelement_has_posttoall)).booleanValue();
				}
			};
			return invoc.bool;
		}
		else
		{
			Object me = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model);
			return ((Boolean)getState().getAttributeValue(me, OAVBDIMetaModel.processableelement_has_posttoall)).booleanValue();
		}
	}

	/**
	 *  Get the random selection flag.
	 *  @return True, if random selection.
	 */
	public boolean isRandomSelection()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object me = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model);
					bool = ((Boolean)getState().getAttributeValue(me, OAVBDIMetaModel.processableelement_has_randomselection)).booleanValue();
				}
			};
			return invoc.bool;
		}
		else
		{
			Object me = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model);
			return ((Boolean)getState().getAttributeValue(me, OAVBDIMetaModel.processableelement_has_randomselection)).booleanValue();
		}
	}
}
