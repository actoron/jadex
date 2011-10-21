package jadex.bdi.runtime.impl.flyweights;

import jadex.bdi.model.IMElement;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.model.impl.flyweights.MParameterSetFlyweight;
import jadex.bdi.runtime.IParameterSet;
import jadex.bdi.runtime.interpreter.BDIInterpreter;
import jadex.bdi.runtime.interpreter.BeliefRules;
import jadex.bdi.runtime.interpreter.MessageEventRules;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.bridge.service.types.message.MessageType;
import jadex.bridge.service.types.message.MessageType.ParameterSpecification;
import jadex.commons.SReflect;
import jadex.commons.Tuple;
import jadex.rules.state.IOAVState;

import java.lang.reflect.Array;
import java.util.Collection;

/**
 *  Flyweight for a parameter set on instance level.
 */
public class ParameterSetFlyweight extends ElementFlyweight implements IParameterSet
{
	//-------- attributes --------
	
	/** Parameter name. */
	// Used only when handle is null, because no parameter value stored in state, yet.
	protected String	name;
	
	/** Parameter element handle. */
	protected Object	pe;
	
	//-------- constructors --------
	
	/**
	 *  Create a new parameter flyweight.
	 *  @param state	The state.
	 *  @param scope	The scope handle.
	 *  @param handle	The parameter handle (or null if no value yet).
	 *  @param name	The parameter name (used, if no value yet).
	 *  @param pe	The handle for the parameter element to which this parameter belongs.
	 */
	private ParameterSetFlyweight(IOAVState state, Object scope, Object handle, String name, Object pe)
	{
		super(state, scope, handle);
		this.name	= name;
		this.pe = pe;
	}
	
	/**
	 *  Get or create a flyweight.
	 *  @return The flyweight.
	 */
	public static ParameterSetFlyweight getParameterSetFlyweight(IOAVState state, Object scope, Object handle, String name, Object parameterelement)
	{
		BDIInterpreter ip = BDIInterpreter.getInterpreter(state);
		ParameterSetFlyweight ret = (ParameterSetFlyweight)ip.getFlyweightCache(IParameterSet.class, new Tuple(IParameterSet.class, parameterelement, name));
		if(ret==null)
		{
			ret = new ParameterSetFlyweight(state, scope, handle, name, parameterelement);
			ip.putFlyweightCache(IParameterSet.class, new Tuple(IParameterSet.class, parameterelement, name), ret);
		}
		return ret;
	}
	
	//-------- IParameter interface --------

	/**
	 *  Add a value to a parameter set.
	 *  @param value The new value.
	 */
	public void addValue(final Object value)
	{
		if(getInterpreter().getComponentAdapter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					if(!hasHandle() && getState().containsKey(pe, 
						OAVBDIRuntimeModel.parameterelement_has_parametersets, name))
					{
						setHandle(getState().getAttributeValue(pe, 
							OAVBDIRuntimeModel.parameterelement_has_parametersets, name));
					}
					if(!hasHandle())
					{
						Object mparamelem = getState().getAttributeValue(pe, OAVBDIRuntimeModel.element_has_model);	
						Object mparamset = getState().getAttributeValue(mparamelem, OAVBDIMetaModel.parameterelement_has_parametersets, name);
						setHandle(BeliefRules.createParameterSet(getState(), name, null, resolveClazz(), pe, mparamset, getScope()));
					}
					String	direction 	= resolveDirection();
					if(OAVBDIMetaModel.PARAMETER_DIRECTION_FIXED.equals(direction)
						|| OAVBDIMetaModel.PARAMETER_DIRECTION_IN.equals(direction) && ParameterFlyweight.inprocess(getState(), pe, getScope())
						|| OAVBDIMetaModel.PARAMETER_DIRECTION_OUT.equals(direction) && !ParameterFlyweight.inprocess(getState(), pe, getScope()))
						throw new RuntimeException("Write access not allowed to parameter set: "
							+direction+" "+getName());

					BeliefRules.addParameterSetValue(getState(), getHandle(), value);
				}
			};
		}
		else
		{
			if(!hasHandle() && getState().containsKey(pe, 
				OAVBDIRuntimeModel.parameterelement_has_parametersets, name))
			{
				setHandle(getState().getAttributeValue(pe, 
					OAVBDIRuntimeModel.parameterelement_has_parametersets, name));
			}
			if(!hasHandle())
			{
				Object mparamelem = getState().getAttributeValue(pe, OAVBDIRuntimeModel.element_has_model);	
				Object mparamset = getState().getAttributeValue(mparamelem, OAVBDIMetaModel.parameterelement_has_parametersets, name);
				setHandle(BeliefRules.createParameterSet(getState(), name, null, resolveClazz(), pe, mparamset, getScope()));
			}
			String	direction 	= resolveDirection();
			if(OAVBDIMetaModel.PARAMETER_DIRECTION_FIXED.equals(direction)
				|| OAVBDIMetaModel.PARAMETER_DIRECTION_IN.equals(direction) && ParameterFlyweight.inprocess(getState(), pe, getScope())
				|| OAVBDIMetaModel.PARAMETER_DIRECTION_OUT.equals(direction) && !ParameterFlyweight.inprocess(getState(), pe, getScope()))
				throw new RuntimeException("Write access not allowed to parameter set: "
					+direction+" "+getName());

			getInterpreter().startMonitorConsequences();
			BeliefRules.addParameterSetValue(getState(), getHandle(), value);
			getInterpreter().endMonitorConsequences();
		}
	}

	/**
	 *  Remove a value to a parameter set.
	 *  @param value The new value.
	 */
	public void removeValue(final Object value)
	{
		if(getInterpreter().getComponentAdapter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					if(!hasHandle() && getState().containsKey(pe, 
						OAVBDIRuntimeModel.parameterelement_has_parametersets, name))
					{
						setHandle(getState().getAttributeValue(pe, 
							OAVBDIRuntimeModel.parameterelement_has_parametersets, name));
					}
					String	direction 	= resolveDirection();
					if(OAVBDIMetaModel.PARAMETER_DIRECTION_FIXED.equals(direction)
						|| OAVBDIMetaModel.PARAMETER_DIRECTION_IN.equals(direction) && ParameterFlyweight.inprocess(getState(), pe, getScope())
						|| OAVBDIMetaModel.PARAMETER_DIRECTION_OUT.equals(direction) && !ParameterFlyweight.inprocess(getState(), pe, getScope()))
						throw new RuntimeException("Write access not allowed to parameter set: "
							+direction+" "+getName());

					if(!hasHandle())
						throw new RuntimeException("Value not contained: "+value);
					BeliefRules.removeParameterSetValue(getState(), getHandle(), value);
				}
			};
		}
		else
		{
			if(!hasHandle() && getState().containsKey(pe, 
				OAVBDIRuntimeModel.parameterelement_has_parametersets, name))
			{
				setHandle(getState().getAttributeValue(pe, 
					OAVBDIRuntimeModel.parameterelement_has_parametersets, name));
			}
			String	direction 	= resolveDirection();
			if(OAVBDIMetaModel.PARAMETER_DIRECTION_FIXED.equals(direction)
				|| OAVBDIMetaModel.PARAMETER_DIRECTION_IN.equals(direction) && ParameterFlyweight.inprocess(getState(), pe, getScope())
				|| OAVBDIMetaModel.PARAMETER_DIRECTION_OUT.equals(direction) && !ParameterFlyweight.inprocess(getState(), pe, getScope()))
				throw new RuntimeException("Write access not allowed to parameter set: "
					+direction+" "+getName());

			if(!hasHandle())
				throw new RuntimeException("Value not contained: "+value);
			
			getInterpreter().startMonitorConsequences();
			BeliefRules.removeParameterSetValue(getState(), getHandle(), value);
			getInterpreter().endMonitorConsequences();
		}
	}

	/**
	 *  Add values to a parameter set.
	 */
	public void addValues(final Object[] values)
	{
		if(values==null)
			return;
	
		if(getInterpreter().getComponentAdapter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					if(!hasHandle() && getState().containsKey(pe, 
							OAVBDIRuntimeModel.parameterelement_has_parametersets, name))
					{
						setHandle(getState().getAttributeValue(pe, 
							OAVBDIRuntimeModel.parameterelement_has_parametersets, name));
					}
					if(!hasHandle())
					{
						Object mparamelem = getState().getAttributeValue(pe, OAVBDIRuntimeModel.element_has_model);	
						Object mparamset = getState().getAttributeValue(mparamelem, OAVBDIMetaModel.parameterelement_has_parametersets, name);
						setHandle(BeliefRules.createParameterSet(getState(), name, null, resolveClazz(), pe, mparamset, getScope()));
					}
					
					String	direction 	= resolveDirection();
					if(OAVBDIMetaModel.PARAMETER_DIRECTION_FIXED.equals(direction)
						|| OAVBDIMetaModel.PARAMETER_DIRECTION_IN.equals(direction) && ParameterFlyweight.inprocess(getState(), pe, getScope())
						|| OAVBDIMetaModel.PARAMETER_DIRECTION_OUT.equals(direction) && !ParameterFlyweight.inprocess(getState(), pe, getScope()))
						throw new RuntimeException("Write access not allowed to parameter set: "
							+direction+" "+getName());

					for(int i=0; i<values.length; i++)
						BeliefRules.addParameterSetValue(getState(), getHandle(), values[i]);
				}
			};
		}
		else
		{
			if(!hasHandle() && getState().containsKey(pe, 
					OAVBDIRuntimeModel.parameterelement_has_parametersets, name))
			{
				setHandle(getState().getAttributeValue(pe, 
					OAVBDIRuntimeModel.parameterelement_has_parametersets, name));
			}
			if(!hasHandle())
			{
				Object mparamelem = getState().getAttributeValue(pe, OAVBDIRuntimeModel.element_has_model);	
				Object mparamset = getState().getAttributeValue(mparamelem, OAVBDIMetaModel.parameterelement_has_parametersets, name);
				setHandle(BeliefRules.createParameterSet(getState(), name, null, resolveClazz(), pe, mparamset, getScope()));
			}
			String	direction 	= resolveDirection();
			if(OAVBDIMetaModel.PARAMETER_DIRECTION_FIXED.equals(direction)
				|| OAVBDIMetaModel.PARAMETER_DIRECTION_IN.equals(direction) && ParameterFlyweight.inprocess(getState(), pe, getScope())
				|| OAVBDIMetaModel.PARAMETER_DIRECTION_OUT.equals(direction) && !ParameterFlyweight.inprocess(getState(), pe, getScope()))
				throw new RuntimeException("Write access not allowed to parameter set: "
					+direction+" "+getName());

			getInterpreter().startMonitorConsequences();
			for(int i=0; i<values.length; i++)
				BeliefRules.addParameterSetValue(getState(), getHandle(), values[i]);
			getInterpreter().endMonitorConsequences();
		}
	}

	/**
	 *  Remove all values from a parameter set.
	 */
	public void removeValues()
	{
		if(getInterpreter().getComponentAdapter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					if(!hasHandle() && getState().containsKey(pe, 
						OAVBDIRuntimeModel.parameterelement_has_parametersets, name))
					{
						setHandle(getState().getAttributeValue(pe, 
							OAVBDIRuntimeModel.parameterelement_has_parametersets, name));
					}
					String	direction 	= resolveDirection();
					if(OAVBDIMetaModel.PARAMETER_DIRECTION_FIXED.equals(direction)
						|| OAVBDIMetaModel.PARAMETER_DIRECTION_IN.equals(direction) && ParameterFlyweight.inprocess(getState(), pe, getScope())
						|| OAVBDIMetaModel.PARAMETER_DIRECTION_OUT.equals(direction) && !ParameterFlyweight.inprocess(getState(), pe, getScope()))
						throw new RuntimeException("Write access not allowed to parameter set: "
							+direction+" "+getName());

					if(hasHandle())
					{
						Collection vals = getState().getAttributeValues(getHandle(), OAVBDIRuntimeModel.parameterset_has_values);
						if(vals!=null)
						{
							Object[]	avals	= vals.toArray();
							for(int i=0; i<avals.length; i++)
								BeliefRules.removeParameterSetValue(getState(), getHandle(), avals[i]);
						}				
					}
				}
			};
		}
		else
		{
			if(!hasHandle() && getState().containsKey(pe, 
				OAVBDIRuntimeModel.parameterelement_has_parametersets, name))
			{
				setHandle(getState().getAttributeValue(pe, 
					OAVBDIRuntimeModel.parameterelement_has_parametersets, name));
			}
			String	direction 	= resolveDirection();
			if(OAVBDIMetaModel.PARAMETER_DIRECTION_FIXED.equals(direction)
				|| OAVBDIMetaModel.PARAMETER_DIRECTION_IN.equals(direction) && ParameterFlyweight.inprocess(getState(), pe, getScope())
				|| OAVBDIMetaModel.PARAMETER_DIRECTION_OUT.equals(direction) && !ParameterFlyweight.inprocess(getState(), pe, getScope()))
				throw new RuntimeException("Write access not allowed to parameter set: "
					+direction+" "+getName());

			if(hasHandle())
			{
				Collection vals = getState().getAttributeValues(getHandle(), OAVBDIRuntimeModel.parameterset_has_values);
				if(vals!=null)
				{
					Object[]	avals	= vals.toArray();
					getInterpreter().startMonitorConsequences();
					for(int i=0; i<avals.length; i++)
						BeliefRules.removeParameterSetValue(getState(), getHandle(), avals[i]);
					getInterpreter().endMonitorConsequences();
				}				
			}
		}
	}

	/**
	 *  Get a value equal to the given object.
	 *  @param oldval The old value.
	 * /
	public Object	getValue(final Object oldval)
	{
		if(getInterpreter().getComponentAdapter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					if(!hasHandle() && getState().containsKey(pe, 
						OAVBDIRuntimeModel.parameterelement_has_parametersets, name))
					{
						setHandle(getState().getAttributeValue(pe, 
							OAVBDIRuntimeModel.parameterelement_has_parametersets, name));
					}
					if(!hasHandle())
					{
						setHandle(getState().createObject(OAVBDIRuntimeModel.parameterset_type));
						getState().setAttributeValue(getHandle(), OAVBDIRuntimeModel.parameterset_has_name, name);
						getState().addAttributeValue(pe, OAVBDIRuntimeModel.parameterelement_has_parametersets, getHandle());
					}
					
					Collection vals = getState().getAttributeValues(getHandle(), OAVBDIRuntimeModel.parameterset_has_values);
					int index = indexOf(oldval);
					
					if(index != -1)
					{
						found	= true;
						newval	= values.get(index);
					}
				}
			};
			return invoc.bool;
		}
		else
		{
			if(!hasHandle() && getState().containsKey(pe, 
				OAVBDIRuntimeModel.parameterelement_has_parametersets, name))
			{
				setHandle(getState().getAttributeValue(pe, 
					OAVBDIRuntimeModel.parameterelement_has_parametersets, name));
			}
			if(!hasHandle())
			{
				setHandle(getState().createObject(OAVBDIRuntimeModel.parameterset_type));
				getState().setAttributeValue(getHandle(), OAVBDIRuntimeModel.parameterset_has_name, name);
				getState().addAttributeValue(pe, OAVBDIRuntimeModel.parameterelement_has_parametersets, getHandle());
			}
			
			Collection vals = getState().getAttributeValues(getHandle(), OAVBDIRuntimeModel.parameterset_has_values);
			return vals.contains(value);
		}
	}*/

	/**
	 *  Test if a value is contained in a parameter.
	 *  @param value The value to test.
	 *  @return True, if value is contained.
	 */
	public boolean containsValue(final Object value)
	{
		if(getInterpreter().getComponentAdapter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					if(!hasHandle() && getState().containsKey(pe, 
						OAVBDIRuntimeModel.parameterelement_has_parametersets, name))
					{
						setHandle(getState().getAttributeValue(pe, 
							OAVBDIRuntimeModel.parameterelement_has_parametersets, name));
					}
					
					Collection vals	= null;
					Object newval = value;
					if(hasHandle())
					{
//						Class clazz = (Class)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.typedelement_has_class);
						newval = SReflect.convertWrappedValue(value, resolveClazz());
						vals = getState().getAttributeValues(getHandle(), OAVBDIRuntimeModel.parameterset_has_values); 
					}
					bool = vals!=null && vals.contains(newval);
				}
			};
			return invoc.bool;
		}
		else
		{
			if(!hasHandle() && getState().containsKey(pe, 
				OAVBDIRuntimeModel.parameterelement_has_parametersets, name))
			{
				setHandle(getState().getAttributeValue(pe, 
					OAVBDIRuntimeModel.parameterelement_has_parametersets, name));
			}
			
			Collection vals	= null;
			Object newval = value;
			if(hasHandle())
			{
				//Class clazz = (Class)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.typedelement_has_class);
				newval = SReflect.convertWrappedValue(value, resolveClazz());
				vals = getState().getAttributeValues(getHandle(), OAVBDIRuntimeModel.parameterset_has_values); 
			}
			return vals!=null && vals.contains(newval);
		}
	}

	/**
	 *  Get the values of a parameterset.
	 *  @return The values.
	 */
	public Object[]	getValues()
	{
		if(getInterpreter().getComponentAdapter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Collection vals = null;
					if(!hasHandle() && getState().containsKey(pe, 
						OAVBDIRuntimeModel.parameterelement_has_parametersets, name))
					{
						setHandle(getState().getAttributeValue(pe, 
							OAVBDIRuntimeModel.parameterelement_has_parametersets, name));
					}
					if(hasHandle())
					{
						vals = getState().getAttributeValues(getHandle(), OAVBDIRuntimeModel.parameterset_has_values);
					}
					
					Object[] ret = (Object[])Array.newInstance(resolveClazz(), vals!=null ? vals.size() : 0);
					oarray = vals!=null ? vals.toArray(ret) : ret;
				}
			};
			return invoc.oarray;
		}
		else
		{
			Collection vals = null;
			if(!hasHandle() && getState().containsKey(pe, 
				OAVBDIRuntimeModel.parameterelement_has_parametersets, name))
			{
				setHandle(getState().getAttributeValue(pe, 
					OAVBDIRuntimeModel.parameterelement_has_parametersets, name));
			}
			if(hasHandle())
			{
				vals = getState().getAttributeValues(getHandle(), OAVBDIRuntimeModel.parameterset_has_values);
			}
			
			Object[] ret = (Object[])Array.newInstance(resolveClazz(), vals!=null ? vals.size() : 0);
			return vals!=null ? vals.toArray(ret) : ret;
		}
	}
	
	/**
	 *  Get the number of values currently
	 *  contained in this set.
	 *  @return The values count.
	 */
	public int size()
	{
		if(getInterpreter().getComponentAdapter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					integer = 0;
					if(!hasHandle() && getState().containsKey(pe, 
						OAVBDIRuntimeModel.parameterelement_has_parametersets, name))
					{
						setHandle(getState().getAttributeValue(pe, 
							OAVBDIRuntimeModel.parameterelement_has_parametersets, name));
					}
					if(hasHandle())
					{
						Collection coll = getState().getAttributeValues(getHandle(), OAVBDIRuntimeModel.parameterset_has_values);
						if(coll!=null)
							integer = coll.size();
					}
				}
			};
			return invoc.integer;
		}
		else
		{
			int ret = 0;
			if(!hasHandle() && getState().containsKey(pe, 
				OAVBDIRuntimeModel.parameterelement_has_parametersets, name))
			{
				setHandle(getState().getAttributeValue(pe, 
					OAVBDIRuntimeModel.parameterelement_has_parametersets, name));
			}
			if(hasHandle())
			{
				Collection coll = getState().getAttributeValues(getHandle(), OAVBDIRuntimeModel.parameterset_has_values);
				if(coll!=null)
					ret = coll.size();
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
					string = (String)getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.parameterset_has_name);
				}
			};
			return invoc.string;
		}
		else
		{
			return (String)getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.parameterset_has_name);
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
					Object	mpe	= getState().getAttributeValue(pe, OAVBDIRuntimeModel.element_has_model);
					Object	mparameterset = getState().getAttributeValue(mpe, OAVBDIMetaModel.parameterelement_has_parametersets, name);
					Object	mscope	= getState().getAttributeValue(getScope(), OAVBDIRuntimeModel.element_has_model);
					object	= new MParameterSetFlyweight(getState(), mscope, mparameterset);
				}
			};
			return (IMElement)invoc.object;
		}
		else
		{
			IMElement	ret	= null;
			Object	mpe	= getState().getAttributeValue(pe, OAVBDIRuntimeModel.element_has_model);
			Object	mparameterset = getState().getAttributeValue(mpe, OAVBDIMetaModel.parameterelement_has_parametersets, name);
			Object	mscope	= getState().getAttributeValue(getScope(), OAVBDIRuntimeModel.element_has_model);
			ret	= new MParameterSetFlyweight(getState(), mscope, mparameterset);
			return ret;
		}
	}
	
	/**
	 *  Resolve the parameterset class.
	 */
	protected Class resolveClazz()
	{
		Class clazz = null;
		Object mparamelem = getState().getAttributeValue(pe, OAVBDIRuntimeModel.element_has_model);	
		Object mparamset = getState().getAttributeValue(mparamelem, OAVBDIMetaModel.parameterelement_has_parametersets, name);
		if(mparamset!=null)
		{
			clazz = (Class)getState().getAttributeValue(mparamset, OAVBDIMetaModel.typedelement_has_class);
		}
		else if(getState().getType(mparamelem).isSubtype(OAVBDIMetaModel.messageevent_type))
		{
			MessageType mt = MessageEventRules.getMessageEventType(getState(), mparamelem);
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
		Object mparamelem = getState().getAttributeValue(pe, OAVBDIRuntimeModel.element_has_model);	
		Object mparamset = getState().getAttributeValue(mparamelem, OAVBDIMetaModel.parameterelement_has_parametersets, name);
		if(mparamset!=null)
		{
			direction = (String)getState().getAttributeValue(mparamset, OAVBDIMetaModel.parameterset_has_direction);
		}
		if(direction==null)
			direction = OAVBDIMetaModel.PARAMETER_DIRECTION_IN;
		
		return direction;
	}
}
