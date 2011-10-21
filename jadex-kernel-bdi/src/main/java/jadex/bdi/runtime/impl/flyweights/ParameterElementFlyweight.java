package jadex.bdi.runtime.impl.flyweights;

import jadex.bdi.runtime.IParameter;
import jadex.bdi.runtime.IParameterElement;
import jadex.bdi.runtime.IParameterSet;
import jadex.bdi.runtime.impl.SFlyweightFunctionality;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.rules.state.IOAVState;

/**
 *  Flyweight for parameter elements.
 */
public abstract class ParameterElementFlyweight extends ElementFlyweight implements IParameterElement
{
	//-------- attributes --------
	
	/** The cached type. */
	protected String type;
	
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
		this.type = SFlyweightFunctionality.getTypeName(state, handle);
	}
	
	//-------- methods ---------
	
	/**
	 *  Get all parameters.
	 *  @return All parameters.
	 */
	public IParameter[]	getParameters()
	{
		if(getInterpreter().getComponentAdapter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					oarray = SFlyweightFunctionality.getParameters(getState(), getScope(), getHandle());
				}
			};
			return (IParameter[])invoc.oarray;
		}
		else
		{
			return (IParameter[])SFlyweightFunctionality.getParameters(getState(), getScope(), getHandle());
		}
	}

	/**
	 *  Get all parameter sets.
	 *  @return All parameter sets.
	 */
	public IParameterSet[]	getParameterSets()
	{
		if(getInterpreter().getComponentAdapter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					oarray = (IParameterSet[])SFlyweightFunctionality.getParameterSets(getState(), getScope(), getHandle());
				}
			};
			return (IParameterSet[])invoc.oarray;
		}
		else
		{
			return (IParameterSet[])SFlyweightFunctionality.getParameterSets(getState(), getScope(), getHandle());
		}
	}

	/**
	 *  Get the parameter.
	 *  @param name The name.
	 *  @return The param.
	 */
	public IParameter getParameter(final String name)
	{
		if(getInterpreter().getComponentAdapter().isExternalThread())
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
		if(getInterpreter().getComponentAdapter().isExternalThread())
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
		if(getInterpreter().getComponentAdapter().isExternalThread())
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
		if(getInterpreter().getComponentAdapter().isExternalThread())
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
	public String getType()
	{
		return type;
	}
}
