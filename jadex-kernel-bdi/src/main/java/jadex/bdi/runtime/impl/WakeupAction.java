package jadex.bdi.runtime.impl;

import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.runtime.GoalFailureException;
import jadex.bdi.runtime.TimeoutException;
import jadex.bdi.runtime.impl.flyweights.ChangeEventFlyweight;
import jadex.bdi.runtime.impl.flyweights.ExternalAccessFlyweight;
import jadex.bdi.runtime.impl.flyweights.GoalFlyweight;
import jadex.bdi.runtime.impl.flyweights.InternalEventFlyweight;
import jadex.bdi.runtime.impl.flyweights.MessageEventFlyweight;
import jadex.bdi.runtime.interpreter.BDIInterpreter;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.bridge.CheckedAction;
import jadex.commons.future.Future;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVObjectType;

import java.util.List;

/**
 *  The action class for continuing the external thread.
 */
public class WakeupAction extends CheckedAction
{
	//-------- attributes --------
	
	/** The state. */
	protected IOAVState state;
	
	/** The scope. */
	protected Object scope;
	
	/** The wait abstraction. */
	protected Object wa;
	
	/** The external access. */
	protected Object ea;
	
	/** The external access flyweight. */
	protected Object eafly;
	
	/** The observeds elements (factadded, changed, removed). */
	protected List observeds;
	
	/** The future. */
	protected Future future;
	
	/** The timeout flag. */
	protected boolean timeout;
	
	//-------- constructors --------
	
	/**
	 *  Create a new wakeup action.
	 */
	public WakeupAction(IOAVState state, Object scope, Object wa, Object ea, 
		ExternalAccessFlyweight eafly, List observeds, Future future)
	{
		this.state = state;
		this.scope = scope;
		this.wa = wa;
		this.ea = ea;
		this.eafly = eafly;
		this.observeds = observeds;
		this.future = future;
		this.timeout	= true;
	}
	
	//-------- methods --------
	
	/**
	 *  After external wait notify future with result.
	 */
	public void run()
	{
		Object ret = null;
		Exception e = null;

		if(isTimeout())
		{
			if(wa!=null)
			{
//				state.removeAttributeValue(scope, OAVBDIRuntimeModel.capability_has_externalaccesses, ea);
				e = new TimeoutException();
			}
		}
		else
		{
			Object de = state.getAttributeValue(ea, OAVBDIRuntimeModel.externalaccess_has_dispatchedelement);
			if(de!=null)
			{
				OAVObjectType type = state.getType(de);
				if(OAVBDIRuntimeModel.goal_type.equals(type))
				{
					// When goal is not succeeded (or idle for maintaingoals) throw exception.
					if(!OAVBDIRuntimeModel.GOALPROCESSINGSTATE_SUCCEEDED.equals(
						state.getAttributeValue(de, OAVBDIRuntimeModel.goal_has_processingstate)))
					{
						Object	mgoal	= state.getAttributeValue(de, OAVBDIRuntimeModel.element_has_model);
						if(!state.getType(mgoal).isSubtype(OAVBDIMetaModel.maintaingoal_type)
							|| !OAVBDIRuntimeModel.GOALPROCESSINGSTATE_IDLE.equals(
								state.getAttributeValue(de, OAVBDIRuntimeModel.goal_has_processingstate)))
						{
//							state.removeAttributeValue(scope, OAVBDIRuntimeModel.capability_has_externalaccesses, ea);
							e = new GoalFailureException("Goal failed: "+de+" "+state.getAttributeValue(mgoal, OAVBDIMetaModel.modelelement_has_name));
						}
					}
					ret = GoalFlyweight.getGoalFlyweight(state, scope, de);
				}
				else if(OAVBDIRuntimeModel.internalevent_type.equals(type))
				{
					// Todo: Hack!!! wrong scope
					ret = InternalEventFlyweight.getInternalEventFlyweight(state, scope, de);
				}
				else if(OAVBDIRuntimeModel.messageevent_type.equals(type))
				{
					// Todo: Hack!!! wrong scope
					ret = MessageEventFlyweight.getMessageEventFlyweight(state, scope, de);
				}
				else if(OAVBDIRuntimeModel.changeevent_type.equals(type))
				{
					// Todo: Hack!!! wrong scope
					ret = new ChangeEventFlyweight(state, scope, de);
				}
				else if(OAVBDIMetaModel.condition_type.equals(type))
				{
					// Todo: change event for triggered condition. 
					ret = state.getAttributeValue(de, OAVBDIMetaModel.modelelement_has_name);
				}
			}
		}
		
		state.removeAttributeValue(scope, OAVBDIRuntimeModel.capability_has_externalaccesses, ea);
		if(wa!=null)
			state.removeExternalObjectUsage(wa, eafly);
		
		if(observeds!=null)
		{
			BDIInterpreter ip = BDIInterpreter.getInterpreter(state);
			for(int i=0; i<observeds.size(); i++)
				ip.getEventReificator().removeObservedElement(observeds.get(i));
		}
		
		if(e!=null)
			future.setException(e);
		else
			future.setResult(ret);
	}
	
	/**
	 *  Get the timeout flag.
	 *  @return True when a timeout occurred.
	 */
	public boolean isTimeout()
	{
		return timeout;
	}
	
	/**
	 *  Set the timeout flag to false when an event was dispatched.
	 */
	public void setTimeout(boolean timeout)
	{
		this.timeout	= timeout;
	}
}
