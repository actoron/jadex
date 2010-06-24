package jadex.bdi.runtime.impl.eaflyweights;

import jadex.bdi.runtime.IEAParameterElement;
import jadex.bdi.runtime.IParameter;
import jadex.bdi.runtime.IParameterSet;
import jadex.bdi.runtime.impl.FlyweightFunctionality;
import jadex.bdi.runtime.impl.flyweights.ElementFlyweight;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.rules.state.IOAVState;

/**
 *  Flyweight for parameter elements.
 */
public abstract class EAParameterElementFlyweight extends ElementFlyweight implements IEAParameterElement
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
	protected EAParameterElementFlyweight(IOAVState state, Object scope, Object handle)
	{
		super(state, scope, handle);
		this.type = FlyweightFunctionality.getTypeName(state, handle);
	}
	
	//-------- methods ---------
	
	/**
	 *  Get all parameters.
	 *  @return All parameters.
	 */
	public IFuture getParameters()
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					ret.setResult(FlyweightFunctionality.getParameters(getState(), getScope(), getHandle(), true));
				}
			});
		}
		else
		{
			ret.setResult(FlyweightFunctionality.getParameters(getState(), getScope(), getHandle(), true));
		}
		
		return ret;
	}

	/**
	 *  Get all parameter sets.
	 *  @return All parameter sets.
	 */
	public IFuture getParameterSets()
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					ret.setResult(FlyweightFunctionality.getParameterSets(getState(), getScope(), getHandle(), true));
				}
			});
		}
		else
		{
			ret.setResult(FlyweightFunctionality.getParameterSets(getState(), getScope(), getHandle(), true));
		}
		
		return ret;
	}

	/**
	 *  Get the parameter.
	 *  @param name The name.
	 *  @return The param.
	 */
	public IFuture getParameter(final String name)
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					ret.setResult(FlyweightFunctionality.getParameter(getState(), getScope(), getHandle(), name, true));
				}
			});
		}
		else
		{
			ret.setResult(FlyweightFunctionality.getParameter(getState(), getScope(), getHandle(), name, true));
		}
		
		return ret;
	}

	/**
	 *  Get the parameter set element.
 	 *  @param name The name.
	 *  @return The param set.
	 */
	public IFuture getParameterSet(final String name)
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					ret.setResult(FlyweightFunctionality.getParameterSet(getState(), getScope(), getHandle(), name, true));
				}
			});
		}
		else
		{
			ret.setResult(FlyweightFunctionality.getParameterSet(getState(), getScope(), getHandle(), name, true));
		}
		
		return ret;
	}

	/**
	 *  Has the element a parameter element.
	 *  @param name The name.
	 *  @return True, if it has the parameter.
	 */
	public IFuture hasParameter(final String name)
	{
		final Future ret = new Future(); 
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					boolean bool = getState().containsKey(getHandle(), 
						OAVBDIRuntimeModel.parameterelement_has_parameters, name);
					ret.setResult(bool? Boolean.TRUE: Boolean.FALSE);
				}
			});
		}
		else
		{
			boolean bool = getState().containsKey(getHandle(), 
				OAVBDIRuntimeModel.parameterelement_has_parameters, name);
			ret.setResult(bool? Boolean.TRUE: Boolean.FALSE);
		}
		
		return ret;
	}

	/**
	 *  Has the element a parameter set element.
	 *  @param name The name.
	 *  @return True, if it has the parameter set.
	 */
	public IFuture hasParameterSet(final String name)
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					boolean bool = getState().containsKey(getHandle(), 
						OAVBDIRuntimeModel.parameterelement_has_parametersets, name);
					ret.setResult(bool? Boolean.TRUE: Boolean.FALSE);
				}
			});
		}
		else
		{
			boolean bool = getState().containsKey(getHandle(), 
				OAVBDIRuntimeModel.parameterelement_has_parametersets, name);
			ret.setResult(bool? Boolean.TRUE: Boolean.FALSE);
		}
		
		return ret;
	}


	/**
	 *  Get the element type (i.e. the name declared in the ADF).
	 *  @return The element type.
	 */
	public String getType()
	{
		return type;
	}
	
	//-------- convenience methods --------
	
	/**
	 *  Get the value of a parameter.
	 *  @return The value.
	 */
	public IFuture getParameterValue(final String parameter)
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					IParameter param = (IParameter)FlyweightFunctionality.getParameter(getState(), getScope(), getHandle(), parameter, false);
					ret.setResult(param.getValue());
				}
			});
		}
		else
		{
			IParameter param = (IParameter)FlyweightFunctionality.getParameter(getState(), getScope(), getHandle(), parameter, false);
			ret.setResult(param.getValue());
		}
		
		return ret;
	}
	
	/**
	 *  Set the parameter value.
	 *  @param parameter The parameter name.
	 *  @param value The value.
	 */
	public void setParameterValue(final String parameter, final Object value)
	{
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					IParameter param = (IParameter)FlyweightFunctionality.getParameter(getState(), getScope(), getHandle(), parameter, false);
					param.setValue(value);
				}
			});
		}
		else
		{
			IParameter param = (IParameter)FlyweightFunctionality.getParameter(getState(), getScope(), getHandle(), parameter, false);
			param.setValue(value);
		}
		
	}
	
	/**
	 *  Get the values of a parameterset.
	 *  @return The values.
	 */
	public IFuture getParameterSetValues(final String parameterset)
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					IParameterSet paramset = (IParameterSet)FlyweightFunctionality.getParameterSet(getState(), getScope(), getHandle(), parameterset, false);
					ret.setResult(paramset.getValues());
				}
			});
		}
		else
		{
			IParameterSet paramset = (IParameterSet)FlyweightFunctionality.getParameterSet(getState(), getScope(), getHandle(), parameterset, false);
			ret.setResult(paramset.getValues());
		}
		
		return ret;
	}
	
	/**
	 *  Add a value to a parameterset.
	 *  @param parameterset The parameterset name.
	 *  @param value The value.
	 */
	public void addParameterSetValue(final String parameterset, final Object value)
	{
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					IParameterSet paramset = (IParameterSet)FlyweightFunctionality.getParameterSet(getState(), getScope(), getHandle(), parameterset, false);
					paramset.addValue(value);
				}
			});
		}
		else
		{
			IParameterSet paramset = (IParameterSet)FlyweightFunctionality.getParameterSet(getState(), getScope(), getHandle(), parameterset, false);
			paramset.addValue(value);
		}
	}
	
	/**
	 *  Add values to a parameterset.
	 *  @param parameterset The parameterset name.
	 *  @param values The values.
	 */
	public void addParameterSetValues(final String parameterset, final Object[] values)
	{
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					IParameterSet paramset = (IParameterSet)FlyweightFunctionality.getParameterSet(getState(), getScope(), getHandle(), parameterset, false);
					paramset.addValues(values);
				}
			});
		}
		else
		{
			IParameterSet paramset = (IParameterSet)FlyweightFunctionality.getParameterSet(getState(), getScope(), getHandle(), parameterset, false);
			paramset.addValues(values);
		}
	}
	
	/**
	 *  Remove a value to a parameterset.
	 *  @param parameterset The parameterset name.
	 *  @param value The value.
	 */
	public void removeParameterSetValue(final String parameterset, final Object value)
	{
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					IParameterSet paramset = (IParameterSet)FlyweightFunctionality.getParameterSet(getState(), getScope(), getHandle(), parameterset, false);
					paramset.removeValue(value);
				}
			});
		}
		else
		{
			IParameterSet paramset = (IParameterSet)FlyweightFunctionality.getParameterSet(getState(), getScope(), getHandle(), parameterset, false);
			paramset.removeValue(value);
		}
	}
	
	/**
	 *  Remove all values of a parameterset.
	 *  @param parameterset The parameterset name.
	 */
	public void removeParameterSetValues(final String parameterset)
	{
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					IParameterSet paramset = (IParameterSet)FlyweightFunctionality.getParameterSet(getState(), getScope(), getHandle(), parameterset, false);
					paramset.removeValues();
				}
			});
		}
		else
		{
			IParameterSet paramset = (IParameterSet)FlyweightFunctionality.getParameterSet(getState(), getScope(), getHandle(), parameterset, false);
			paramset.removeValues();
		}
	}
	
	/**
	 *  Remove a value to a parameterset.
	 *  @param parameterset The parameterset name.
	 *  @param value The value.
	 */
	public IFuture containsParameterSetValue(final String parameterset, final Object value)
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					IParameterSet paramset = (IParameterSet)FlyweightFunctionality.getParameterSet(getState(), getScope(), getHandle(), parameterset, false);
					ret.setResult(paramset.containsValue(value)? Boolean.TRUE: Boolean.FALSE);
				}
			});
		}
		else
		{
			IParameterSet paramset = (IParameterSet)FlyweightFunctionality.getParameterSet(getState(), getScope(), getHandle(), parameterset, false);
			ret.setResult(paramset.containsValue(value)? Boolean.TRUE: Boolean.FALSE);
		}
		
		return ret;
	}
	
	/**
	 *  Get the number of values currently
	 *  contained in this set.
	 *  @return The values count.
	 */
	public IFuture getParameterSetSize(final String parameterset)
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					IParameterSet paramset = (IParameterSet)FlyweightFunctionality.getParameterSet(getState(), getScope(), getHandle(), parameterset, false);
					ret.setResult(new Integer(paramset.size()));
				}
			});
		}
		else
		{
			IParameterSet paramset = (IParameterSet)FlyweightFunctionality.getParameterSet(getState(), getScope(), getHandle(), parameterset, false);
			ret.setResult(new Integer(paramset.size()));
		}
		
		return ret;
	}

}