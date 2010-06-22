package jadex.bdi.runtime.impl.eaflyweights;

import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.rules.state.IOAVState;

/**
 *  Flyweight for processable elements.
 */
public abstract class EAProcessableElementFlyweight extends EAParameterElementFlyweight
{
	//-------- constructors --------
	
	/**
	 *  Create a new parameter element flyweight.
	 *  @param state	The state.
	 *  @param scope	The scope handle.
	 *  @param handle	The element handle.
	 */
	protected EAProcessableElementFlyweight(IOAVState state, Object scope, Object handle)
	{
		super(state, scope, handle);
	}
	
	//-------- event flags --------

	/**
	 *  Get the post-to-all flag.
	 *  @return True, if post-to-all. 
	 */
	public IFuture isPostToAll()
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					Object me = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model);
					boolean bool = ((Boolean)getState().getAttributeValue(me, OAVBDIMetaModel.processableelement_has_posttoall)).booleanValue();
					ret.setResult(bool? Boolean.TRUE: Boolean.FALSE);
				}
			});
		}
		else
		{
			Object me = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model);
			boolean bool = ((Boolean)getState().getAttributeValue(me, OAVBDIMetaModel.processableelement_has_posttoall)).booleanValue();
			ret.setResult(bool? Boolean.TRUE: Boolean.FALSE);
		}
		
		return ret;
	}

	/**
	 *  Get the random selection flag.
	 *  @return True, if random selection.
	 */
	public IFuture isRandomSelection()
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					Object me = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model);
					boolean bool = ((Boolean)getState().getAttributeValue(me, OAVBDIMetaModel.processableelement_has_randomselection)).booleanValue();
					ret.setResult(bool? Boolean.TRUE: Boolean.FALSE);
				}
			});
		}
		else
		{
			Object me = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model);
			boolean bool = ((Boolean)getState().getAttributeValue(me, OAVBDIMetaModel.processableelement_has_randomselection)).booleanValue();
			ret.setResult(bool? Boolean.TRUE: Boolean.FALSE);
		}
		
		return ret;
	}
}
