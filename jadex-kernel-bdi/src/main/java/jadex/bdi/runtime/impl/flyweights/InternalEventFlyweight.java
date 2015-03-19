package jadex.bdi.runtime.impl.flyweights;

import jadex.bdi.features.IBDIAgentFeature;
import jadex.bdi.features.impl.BDIAgentFeature;
import jadex.bdi.model.IMElement;
import jadex.bdi.model.impl.flyweights.MInternalEventFlyweight;
import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.commons.Tuple;
import jadex.rules.state.IOAVState;

/**
 *  Flyweight for an internal event.
 */
public class InternalEventFlyweight extends ProcessableElementFlyweight implements IInternalEvent
{
	//-------- constructors --------
	
	/**
	 *  Create a new internal event flyweight.
	 *  @param state	The state.
	 *  @param scope	The scope handle.
	 */
	private InternalEventFlyweight(IOAVState state, Object scope, Object handle)
	{
		super(state, scope, handle);
	}
	
	/**
	 *  Get or create a flyweight.
	 *  @return The flyweight.
	 */
	public static InternalEventFlyweight getInternalEventFlyweight(IOAVState state, Object scope, Object handle)
	{
		IBDIAgentFeature ip = BDIAgentFeature.getInterpreter(state);
		InternalEventFlyweight ret = (InternalEventFlyweight)ip.getFlyweightCache(IInternalEvent.class, new Tuple(IInternalEvent.class, handle));
		if(ret==null)
		{
			ret = new InternalEventFlyweight(state, scope, handle);
			ip.putFlyweightCache(IInternalEvent.class, new Tuple(IInternalEvent.class, handle), ret);
		}
		return ret;
	}
	
	//-------- event flags --------
	
	/**
	 *  Get the goal type.
	 *  @return The goal type.
	 * /
	public String	getType()
	{
		throw new UnsupportedOperationException();
	}*/
	
	//-------- listeners --------
	
	/**
	 *  Add an internal event listener.
	 *  @param listener The internal event listener.
	 * /
	public void addInternalEventListener(final IInternalEventListener listener)
	{
		if(isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					getInterpreter().getEventDispatcher().addInternalEventListener(getHandle(), listener);
				}
			};
		}
		else
		{
			getInterpreter().getEventDispatcher().addInternalEventListener(getHandle(), listener);
		}
	}*/
	
	/**
	 *  Remove a internal event listener.
	 *  @param listener The internal event listener.
	 * /
	public void removeInternalEventListener(final IInternalEventListener listener)
	{
		if(isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					getInterpreter().getEventDispatcher().removeInternalEventListener(getHandle(), listener);
				}
			};
		}
		else
		{
			getInterpreter().getEventDispatcher().removeInternalEventListener(getHandle(), listener);
		}
	}*/
		
	//-------- element interface --------
	
	/**
	 *  Get the model element.
	 *  @return The model element.
	 */
	public IMElement getModelElement()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object me = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model);
					Object mscope = getState().getAttributeValue(getScope(), OAVBDIRuntimeModel.element_has_model);
					object = new MInternalEventFlyweight(getState(), mscope, me);
				}
			};
			return (IMElement)invoc.object;
		}
		else
		{
			Object me = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model);
			Object mscope = getState().getAttributeValue(getScope(), OAVBDIRuntimeModel.element_has_model);
			return new MInternalEventFlyweight(getState(), mscope, me);
		}
	}
}
