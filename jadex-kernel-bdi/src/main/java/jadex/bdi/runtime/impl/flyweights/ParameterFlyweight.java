package jadex.bdi.runtime.impl.flyweights;

import jadex.bdi.model.IMElement;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.model.impl.flyweights.MParameterFlyweight;
import jadex.bdi.runtime.IParameter;
import jadex.bdi.runtime.interpreter.BDIInterpreter;
import jadex.bdi.runtime.interpreter.BeliefRules;
import jadex.bdi.runtime.interpreter.MessageEventRules;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.bridge.service.types.message.MessageType;
import jadex.bridge.service.types.message.MessageType.ParameterSpecification;
import jadex.commons.Tuple;
import jadex.rules.state.IOAVState;

import java.util.Collection;

/**
 *  Flyweight for a parameter on instance level.
 */
public class ParameterFlyweight extends ElementFlyweight implements IParameter
{
	/** Parameter name. */
	// Used only when handle is null, because no parameter value stored in state, yet.
	protected String	name;
	
	/** Parameter element handle. */
	protected Object	parameterelement;
	
	//-------- constructors --------
	
	/**
	 *  Create a new parameter flyweight.
	 *  @param state	The state.
	 *  @param scope	The scope handle.
	 *  @param handle	The parameter handle (or null if no value yet).
	 *  @param name	The parameter name (used, if no value yet).
	 *  @param parameterelement	The handle for the parameter element to which this parameter belongs.
	 */
	private ParameterFlyweight(IOAVState state, Object scope, 
		Object handle, String name, Object parameterelement)
	{
		super(state, scope, handle);
		this.name	= name;
		this.parameterelement = parameterelement;
		
		assert parameterelement!=null;
		
		if(parameterelement!=null)
			state.addExternalObjectUsage(parameterelement, this);
	}
	
	/**
	 *  Get or create a flyweight.
	 *  @return The flyweight.
	 */
	public static ParameterFlyweight getParameterFlyweight(IOAVState state, Object scope, Object handle, String name, Object parameterelement)
	{
		BDIInterpreter ip = BDIInterpreter.getInterpreter(state);
		ParameterFlyweight ret = (ParameterFlyweight)ip.getFlyweightCache(IParameter.class, new Tuple(IParameter.class, parameterelement, name));
		if(ret==null)
		{
			ret = new ParameterFlyweight(state, scope, handle, name, parameterelement);
			ip.putFlyweightCache(IParameter.class, new Tuple(IParameter.class, parameterelement, name), ret);
		}
		return ret;
	}
	
	/**
	 *  Actual cleanup code.
	 *  When overriding this method, super.doCleanup() has to be called. 
	 */
	protected void	doCleanup()
	{
		if(parameterelement!=null)
		{
			getState().removeExternalObjectUsage(parameterelement, this);
			parameterelement	= null;
		}
		super.doCleanup();
	}

	//-------- IParameter interface --------

	/**
	 *  Set a value of a parameter.
	 *  @param value The new value.
	 */
	public void setValue(final Object value)
	{
		if(getInterpreter().getComponentAdapter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					if(!hasHandle())
					{
						setHandle(getState().getAttributeValue(parameterelement, 
							OAVBDIRuntimeModel.parameterelement_has_parameters, name));
					}
					if(!hasHandle())
					{
						Object mparamelem = getState().getAttributeValue(parameterelement, OAVBDIRuntimeModel.element_has_model);	
						Object mparam = getState().getAttributeValue(mparamelem, OAVBDIMetaModel.parameterelement_has_parameters, name);
						Class clazz = resolveClazz(getState(), mparamelem, name);
						setHandle(BeliefRules.createParameter(getState(), name, null, clazz, parameterelement, mparam, getScope()));
					}

					String	direction 	= resolveDirection();
					if(OAVBDIMetaModel.PARAMETER_DIRECTION_FIXED.equals(direction)
						|| OAVBDIMetaModel.PARAMETER_DIRECTION_IN.equals(direction) && inprocess(getState(), parameterelement, getScope())
						|| OAVBDIMetaModel.PARAMETER_DIRECTION_OUT.equals(direction) && !inprocess(getState(), parameterelement, getScope()))
						throw new RuntimeException("Write access not allowed to parameter: "
							+direction+" "+getName());

					BeliefRules.setParameterValue(getState(), getHandle(), value);
				}
			};
		}
		else
		{
			if(!hasHandle())
			{
				setHandle(getState().getAttributeValue(parameterelement, 
					OAVBDIRuntimeModel.parameterelement_has_parameters, name));
			}
			if(!hasHandle())
			{
				Object mparamelem = getState().getAttributeValue(parameterelement, OAVBDIRuntimeModel.element_has_model);	
				Object mparam = getState().getAttributeValue(mparamelem, OAVBDIMetaModel.parameterelement_has_parameters, name);
				Class clazz = resolveClazz(getState(), mparamelem, name);
				setHandle(BeliefRules.createParameter(getState(), name, null, clazz, parameterelement, mparam, getScope()));
			}
			
			String	direction 	= resolveDirection();
			if(OAVBDIMetaModel.PARAMETER_DIRECTION_FIXED.equals(direction)
				|| OAVBDIMetaModel.PARAMETER_DIRECTION_IN.equals(direction) && inprocess(getState(), parameterelement, getScope())
				|| OAVBDIMetaModel.PARAMETER_DIRECTION_OUT.equals(direction) && !inprocess(getState(), parameterelement, getScope()))
				throw new RuntimeException("Write access not allowed to parameter: "
					+direction+" "+getName());
			
			getInterpreter().startMonitorConsequences();
			BeliefRules.setParameterValue(getState(), getHandle(), value);
			getInterpreter().endMonitorConsequences();
		}
	}

	/**
	 *  Get the value of a parameter.
	 *  @return The value.
	 */
	public Object	getValue()
	{
		if(getInterpreter().getComponentAdapter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					if(!hasHandle() && getState().containsKey(parameterelement, 
						OAVBDIRuntimeModel.parameterelement_has_parameters, name))
					{
						setHandle(getState().getAttributeValue(parameterelement, 
							OAVBDIRuntimeModel.parameterelement_has_parameters, name));
					}
					if(hasHandle())
					{
						object	= getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.parameter_has_value);
					}
				}
			};
			return invoc.object;
		}
		else
		{
			Object	ret	= null;
			if(!hasHandle() && getState().containsKey(parameterelement, 
				OAVBDIRuntimeModel.parameterelement_has_parameters, name))
			{
				setHandle(getState().getAttributeValue(parameterelement, 
					OAVBDIRuntimeModel.parameterelement_has_parameters, name));
			}
			if(hasHandle())
			{
				ret	= getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.parameter_has_value);
			}
			return ret;
		}
	}
	
	/**
	 *  Get the name.
	 *  @return The name.
	 */
	public String getName()
	{
		if(getInterpreter().getComponentAdapter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					string = (String)getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.parameter_has_name);
				}
			};
			return invoc.string;
		}
		else
		{
			return (String)getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.parameter_has_name);
		}
	}

	//-------- IElement interface --------

	/**
	 *  Get the model element.
	 *  @return The model element.
	 */
	public IMElement getModelElement()
	{
		if(getInterpreter().getComponentAdapter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object	mpe	= getState().getAttributeValue(parameterelement, OAVBDIRuntimeModel.element_has_model);
					Object	mparameter	= getState().getAttributeValue(mpe, OAVBDIMetaModel.parameterelement_has_parameters, name);
					Object	mscope	= getState().getAttributeValue(getScope(), OAVBDIRuntimeModel.element_has_model);
					object	= new MParameterFlyweight(getState(), mscope, mparameter);
				}
			};
			return (IMElement)invoc.object;
		}
		else
		{
			IMElement	ret	= null;
			Object	mpe	= getState().getAttributeValue(parameterelement, OAVBDIRuntimeModel.element_has_model);
			Object	mparameter	= getState().getAttributeValue(mpe, OAVBDIMetaModel.parameterelement_has_parameters, name);
			Object	mscope	= getState().getAttributeValue(getScope(), OAVBDIRuntimeModel.element_has_model);
			ret	= new MParameterFlyweight(getState(), mscope, mparameter);
			return ret;
		}
	}

	/**
	 *  Resolve the parameter class.
	 */
	public static Class resolveClazz(IOAVState state, Object mparamelem, String name)
	{
		Class clazz = null;
//		Object mparamelem = state.getAttributeValue(parameterelement, OAVBDIRuntimeModel.element_has_model);	
		Object mparam = state.getAttributeValue(mparamelem, OAVBDIMetaModel.parameterelement_has_parameters, name);
		if(mparam!=null)
		{
			clazz = (Class)state.getAttributeValue(mparam, OAVBDIMetaModel.typedelement_has_class);
		}
		else if(state.getType(mparamelem).isSubtype(OAVBDIMetaModel.messageevent_type))
		{
			MessageType mt = MessageEventRules.getMessageEventType(state, mparamelem);
			ParameterSpecification ps = mt.getParameter(name);
			clazz = ps.getClazz();
		}
		if(clazz==null)
			clazz = Object.class;
		
		return clazz;
	}

	
	/**
	 *  Resolve the parameter direction.
	 */
	protected String resolveDirection()
	{
		String direction = null;
		Object mparamelem = getState().getAttributeValue(parameterelement, OAVBDIRuntimeModel.element_has_model);	
		Object mparam = getState().getAttributeValue(mparamelem, OAVBDIMetaModel.parameterelement_has_parameters, name);
		if(mparam!=null)
		{
			direction = (String)getState().getAttributeValue(mparam, OAVBDIMetaModel.parameter_has_direction);
		}
		if(direction==null)
			direction = OAVBDIMetaModel.PARAMETER_DIRECTION_IN;
		
		return direction;
	}
	
	/**
	 *  Check if the parameterelement is in process (for parameter write protection).
	 */
	protected static boolean inprocess(IOAVState state, Object parameterelement, Object scope)
	{
		boolean	ret;
		
		if(state.getType(parameterelement).isSubtype(OAVBDIRuntimeModel.plan_type))
		{
			ret	= OAVBDIRuntimeModel.PLANPROCESSINGTATE_RUNNING.equals(state.getAttributeValue(parameterelement, OAVBDIRuntimeModel.plan_has_processingstate));
		}
		else if(state.getType(parameterelement).isSubtype(OAVBDIRuntimeModel.goal_type))
		{
			// For goals, "inprocess" actually means adopted (i.e. active/option/suspended).
			ret	= OAVBDIRuntimeModel.GOALLIFECYCLESTATE_ACTIVE.equals(state.getAttributeValue(parameterelement, OAVBDIRuntimeModel.goal_has_lifecyclestate))
				|| OAVBDIRuntimeModel.GOALLIFECYCLESTATE_OPTION.equals(state.getAttributeValue(parameterelement, OAVBDIRuntimeModel.goal_has_lifecyclestate))
				|| OAVBDIRuntimeModel.GOALLIFECYCLESTATE_SUSPENDED.equals(state.getAttributeValue(parameterelement, OAVBDIRuntimeModel.goal_has_lifecyclestate));
		}
		else // if(getState().getType(parameterelement).isSubtype(OAVBDIRuntimeModel.processableelement_type))
		{
			// Event is in process, when reasoning is finished.
			ret	= OAVBDIRuntimeModel.PROCESSABLEELEMENT_CANDIDATESSELECTED.equals(state.getAttributeValue(parameterelement, OAVBDIRuntimeModel.processableelement_has_state))
			 || OAVBDIRuntimeModel.PROCESSABLEELEMENT_NOCANDIDATES.equals(state.getAttributeValue(parameterelement, OAVBDIRuntimeModel.processableelement_has_state));

			// When reasoning not done, event is also in process, when dispatched (i.e. in capability).
			if(!ret && state.getType(parameterelement).isSubtype(OAVBDIRuntimeModel.messageevent_type))
			{
				Collection events	= state.getAttributeValues(scope, OAVBDIRuntimeModel.capability_has_messageevents);
				ret	= events!=null && events.contains(parameterelement);
			}
			else if(!ret && state.getType(parameterelement).isSubtype(OAVBDIRuntimeModel.internalevent_type))
			{
				Collection events	= state.getAttributeValues(scope, OAVBDIRuntimeModel.capability_has_internalevents);
				ret	= events!=null && events.contains(parameterelement);
			}
		}
		
		return ret;
	}
}
