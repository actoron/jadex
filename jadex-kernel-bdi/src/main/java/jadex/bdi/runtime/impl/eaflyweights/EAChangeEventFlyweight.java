package jadex.bdi.runtime.impl.eaflyweights;

import jadex.bdi.model.IMElement;
import jadex.bdi.runtime.IEAChangeEvent;
import jadex.bdi.runtime.impl.SFlyweightFunctionality;
import jadex.bdi.runtime.impl.flyweights.ElementFlyweight;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.rules.state.IOAVState;

/**
 *  Flyweight for change events.
 */
public class EAChangeEventFlyweight extends ElementFlyweight implements IEAChangeEvent
{
	//-------- constructors --------
	
	/**
	 *  Create a new change event flyweight.
	 *  @param state	The state.
	 *  @param scope	The scope handle.
	 */
	public EAChangeEventFlyweight(IOAVState state, Object scope, Object handle)
	{
		super(state, scope, handle);
	}
	
	//-------- methods --------
	
	/**
	 *  Get the element that caused the event.
	 *  @return The element.
	 */
	public IFuture getElement()
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					ret.setResult(SFlyweightFunctionality.getElement(getState(), getHandle(), getScope(), true));
				}
			});
		}
		else
		{
			ret.setResult(SFlyweightFunctionality.getElement(getState(), getHandle(), getScope(), true));
		}
		
		return ret;
	}
	
	/**
	 *  Get the changeevent value.
	 *  @return The value (can be null).
	 */
	public IFuture getValue()
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					ret.setResult(getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.changeevent_has_value));
				}
			});
		}
		else
		{
			ret.setResult(getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.changeevent_has_value));
		}
		
		return ret;
	}
	
	/**
	 *  Get the type.
	 *  @return The type.
	 */
	public IFuture getType()
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					ret.setResult(getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.changeevent_has_type));
				}
			});
		}
		else
		{
			ret.setResult(getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.changeevent_has_type));
		}
		
		return ret;
	}
	
	//-------- element interface --------
	
	/**
	 *  Get the model element.
	 *  @return The model element.
	 */
	public IMElement getModelElement()
	{
		throw new UnsupportedOperationException("Element has no model");
	}
}

