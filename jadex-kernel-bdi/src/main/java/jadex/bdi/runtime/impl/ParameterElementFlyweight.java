package jadex.bdi.runtime.impl;

import jadex.bdi.runtime.IParameter;
import jadex.bdi.runtime.IParameterElement;
import jadex.bdi.runtime.IParameterSet;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.rules.state.IOAVState;

import java.util.Collection;
import java.util.Iterator;

/**
 *  Flyweight for parameter elements.
 */
public abstract class ParameterElementFlyweight extends ElementFlyweight implements IParameterElement
{
	//-------- constructors --------
	
	/**
	 *  Create a new parameter element flyweight.
	 *  @param state	The state.
	 *  @param scope	The scope handle.
	 *  @param handle	The element handle.
	 */
	protected ParameterElementFlyweight(IOAVState state, Object scope, Object handle)
	{
		super(state, scope, handle);
	}
	
	//-------- methods ---------
	
	/**
	 *  Get all parameters.
	 *  @return All parameters.
	 */
	public IParameter[]	getParameters()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Collection params = getState().getAttributeValues(getHandle(), 
						OAVBDIRuntimeModel.parameterelement_has_parameters);
					if(params!=null)
					{
						oarray = new IParameter[params.size()];
						int i=0;
						for(Iterator it=params.iterator(); it.hasNext(); i++)
						{
							Object param = it.next();
							String name = (String)getState().getAttributeValue(param, OAVBDIRuntimeModel.parameter_has_name);
							oarray[i] = ParameterFlyweight.getParameterFlyweight(getState(), getScope(), param, name, getHandle());
						}
					}
					else
					{
						oarray = new IParameter[0];
					}
				}
			};
			return (IParameter[])invoc.oarray;
		}
		else
		{
			Collection params = getState().getAttributeValues(getHandle(), 
				OAVBDIRuntimeModel.parameterelement_has_parameters);
			if(params!=null)
			{
				IParameter[] oarray = new IParameter[params.size()];
				int i=0;
				for(Iterator it=params.iterator(); it.hasNext(); i++)
				{
					Object param = it.next();
					String name = (String)getState().getAttributeValue(param, OAVBDIRuntimeModel.parameter_has_name);
					oarray[i] = ParameterFlyweight.getParameterFlyweight(getState(), getScope(), param, name, getHandle());
				}
				return oarray;
			}
			else
			{
				return new IParameter[0];
			}
		}
	}

	/**
	 *  Get all parameter sets.
	 *  @return All parameter sets.
	 */
	public IParameterSet[]	getParameterSets()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Collection paramsets = getState().getAttributeValues(getHandle(), 
						OAVBDIRuntimeModel.parameterelement_has_parametersets);
					if(paramsets!=null)
					{
						oarray = new IParameterSet[paramsets.size()];
						int i=0;
						for(Iterator it=paramsets.iterator(); it.hasNext(); i++)
						{
							Object paramset = it.next();
							String name = (String)getState().getAttributeValue(paramset, OAVBDIRuntimeModel.parameterset_has_name);
							oarray[i] = ParameterSetFlyweight.getParameterSetFlyweight(getState(), getScope(), paramset, name, getHandle());
						}
					}
					else
					{
						oarray = new IParameterSet[0];
					}
				}
			};
			return (IParameterSet[])invoc.oarray;
		}
		else
		{
			Collection paramsets = getState().getAttributeValues(getHandle(), 
				OAVBDIRuntimeModel.parameterelement_has_parametersets);
			if(paramsets!=null)
			{
				IParameterSet[] oarray = new IParameterSet[paramsets.size()];
				int i=0;
				for(Iterator it=paramsets.iterator(); it.hasNext(); i++)
				{
					Object paramset = it.next();
					String name = (String)getState().getAttributeValue(paramset, OAVBDIRuntimeModel.parameterset_has_name);
					oarray[i] = ParameterSetFlyweight.getParameterSetFlyweight(getState(), getScope(), paramset, name, getHandle());
				}
				return oarray;
			}
			else
			{
				return new IParameterSet[0];
			}
		}
	}

	/**
	 *  Get the parameter.
	 *  @param name The name.
	 *  @return The param.
	 */
	public IParameter getParameter(final String name)
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object param = getState().getAttributeValue(getHandle(), 
						OAVBDIRuntimeModel.parameterelement_has_parameters, name);
					object = ParameterFlyweight.getParameterFlyweight(getState(), getScope(), param, name, getHandle());
				}
			};
			return (IParameter)invoc.object;
		}
		else
		{
			Object param = getState().getAttributeValue(getHandle(), 
				OAVBDIRuntimeModel.parameterelement_has_parameters, name);
			return ParameterFlyweight.getParameterFlyweight(getState(), getScope(), param, name, getHandle());
		}
	}

	/**
	 *  Get the parameter set element.
 	 *  @param name The name.
	 *  @return The param set.
	 */
	public IParameterSet getParameterSet(final String name)
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object paramset = getState().getAttributeValue(getHandle(), 
						OAVBDIRuntimeModel.parameterelement_has_parametersets, name);
					object = ParameterSetFlyweight.getParameterSetFlyweight(getState(), getScope(), paramset, name, getHandle());
				}
			};
			return (IParameterSet)invoc.object;
		}
		else
		{
			Object paramset = getState().getAttributeValue(getHandle(), 
				OAVBDIRuntimeModel.parameterelement_has_parametersets, name);
			return ParameterSetFlyweight.getParameterSetFlyweight(getState(), getScope(), paramset, name, getHandle());
		}
	}

	/**
	 *  Has the element a parameter element.
	 *  @param name The name.
	 *  @return True, if it has the parameter.
	 */
	public boolean hasParameter(final String name)
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					bool = getState().containsKey(getHandle(), 
						OAVBDIRuntimeModel.parameterelement_has_parameters, name);
				}
			};
			return invoc.bool;
		}
		else
		{
			return getState().containsKey(getHandle(), 
				OAVBDIRuntimeModel.parameterelement_has_parameters, name);
		}
	}

	/**
	 *  Has the element a parameter set element.
	 *  @param name The name.
	 *  @return True, if it has the parameter set.
	 */
	public boolean hasParameterSet(final String name)
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					bool = getState().containsKey(getHandle(), 
						OAVBDIRuntimeModel.parameterelement_has_parametersets, name);
				}
			};
			return invoc.bool;
		}
		else
		{
			return getState().containsKey(getHandle(), 
				OAVBDIRuntimeModel.parameterelement_has_parametersets, name);
		}
	}


	/**
	 *  Get the element type (i.e. the name declared in the ADF).
	 *  @return The element type.
	 */
	public String	getType()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					string	= getTypeName();
				}
			};
			return invoc.string;
		}
		else
		{
			return	getTypeName();
		}
	}
}
