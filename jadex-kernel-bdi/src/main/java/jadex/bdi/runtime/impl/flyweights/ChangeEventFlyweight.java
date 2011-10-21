package jadex.bdi.runtime.impl.flyweights;

import jadex.bdi.model.IMElement;
import jadex.bdi.runtime.IChangeEvent;
import jadex.bdi.runtime.impl.SFlyweightFunctionality;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.rules.state.IOAVState;

/**
 *  Flyweight for change events.
 */
public class ChangeEventFlyweight extends ElementFlyweight implements IChangeEvent
{
	//-------- constructors --------
	
	/**
	 *  Create a new change event flyweight.
	 *  @param state	The state.
	 *  @param scope	The scope handle.
	 */
	public ChangeEventFlyweight(IOAVState state, Object scope, Object handle)
	{
		super(state, scope, handle);
	}
	
	//-------- methods --------
	
	/**
	 *  Get the element that caused the event.
	 *  @return The element.
	 */
	public ElementFlyweight getElement()
	{
		if(getInterpreter().getComponentAdapter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					object = SFlyweightFunctionality.getElement(getState(), getHandle(), getScope());
				}
			};
			return (ElementFlyweight)invoc.object;
		}
		else
		{
			return SFlyweightFunctionality.getElement(getState(), getHandle(), getScope());
		}
	}
	
	/**
	 *  Get the changeevent value.
	 *  @return The value (can be null).
	 */
	public Object getValue()
	{
		if(getInterpreter().getComponentAdapter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					object = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.changeevent_has_value);
				}
			};
			return invoc.object;
		}
		else
		{
			return getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.changeevent_has_value);
		}
	}
	
	/**
	 *  Get the type.
	 *  @return The type.
	 */
	public String getType()
	{
		if(getInterpreter().getComponentAdapter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					object = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.changeevent_has_type);
				}
			};
			return (String)invoc.object;
		}
		else
		{
			return (String)getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.changeevent_has_type);
		}
	}
	
//-------- element interface --------
	
	/**
	 *  Get the model element.
	 *  @return The model element.
	 */
	public IMElement getModelElement()
	{
		throw new RuntimeException("Element has no model: "+this);
	}
}
