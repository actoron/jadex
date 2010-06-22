package jadex.bdi.runtime.impl.eaflyweights;

import jadex.bdi.runtime.IEAParameterElement;
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
					Object param = getState().getAttributeValue(getHandle(), 
						OAVBDIRuntimeModel.parameterelement_has_parameters, name);
					ret.setResult(EAParameterFlyweight.getParameterFlyweight(getState(), getScope(), param, name, getHandle()));
				}
			});
		}
		else
		{
			Object param = getState().getAttributeValue(getHandle(), 
				OAVBDIRuntimeModel.parameterelement_has_parameters, name);
			ret.setResult(EAParameterFlyweight.getParameterFlyweight(getState(), getScope(), param, name, getHandle()));
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
					Object paramset = getState().getAttributeValue(getHandle(), 
						OAVBDIRuntimeModel.parameterelement_has_parametersets, name);
					ret.setResult(EAParameterSetFlyweight.getParameterSetFlyweight(getState(), getScope(), paramset, name, getHandle()));
				}
			});
		}
		else
		{
			Object paramset = getState().getAttributeValue(getHandle(), 
				OAVBDIRuntimeModel.parameterelement_has_parametersets, name);
			ret.setResult(EAParameterSetFlyweight.getParameterSetFlyweight(getState(), getScope(), paramset, name, getHandle()));
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
	public IFuture getType()
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					ret.setResult(getTypeName());
				}
			});
		}
		else
		{
			ret.setResult(getTypeName());
		}
		
		return ret;
	}
}