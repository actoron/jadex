package jadex.bdi.runtime.impl;

import jadex.bdi.interpreter.OAVBDIRuntimeModel;
import jadex.bdi.runtime.IChangeEvent;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVObjectType;

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
		ElementFlyweight ret; 
		Object elem = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.changeevent_has_element);
		OAVObjectType type = getState().getType(elem);
		
		if(type.isSubtype(OAVBDIRuntimeModel.goal_type))
		{
			ret = GoalFlyweight.getGoalFlyweight(getState(), getScope(), elem);
		}
		else if(type.isSubtype(OAVBDIRuntimeModel.messageevent_type))
		{
			ret = MessageEventFlyweight.getMessageFlyweight(getState(), getScope(), elem);
		}
		else if(type.isSubtype(OAVBDIRuntimeModel.internalevent_type))
		{
			ret = InternalEventFlyweight.getInternalFlyweight(getState(), getScope(), elem);
		}
		else if(type.isSubtype(OAVBDIRuntimeModel.belief_type))
		{
			ret = BeliefFlyweight.getBeliefFlyweight(getState(), getScope(), elem);
		}
		else if(type.isSubtype(OAVBDIRuntimeModel.beliefset_type))
		{
			ret = BeliefSetFlyweight.getBeliefSetFlyweight(getState(), getScope(), elem);
		}
		else
		{
			throw new RuntimeException("Unknown element type: "+elem);
		}
	
		return ret;
	}
	
	/**
	 *  Get the changeevent value.
	 *  @return The value (can be null).
	 */
	public Object getValue()
	{
		return getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.changeevent_has_value);
	}
	
	/**
	 *  Get the type.
	 *  @return The type.
	 */
	public String getType()
	{
		return (String)getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.changeevent_has_type);
	}
}
