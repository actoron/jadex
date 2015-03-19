package jadex.bdi.runtime.impl.flyweights;

import jadex.bdi.features.IBDIAgentFeature;
import jadex.bdi.features.impl.BDIAgentFeature;
import jadex.bdi.model.IMElement;
import jadex.bdi.model.impl.flyweights.MExpressionFlyweight;
import jadex.bdi.runtime.IExpression;
import jadex.bdi.runtime.impl.SFlyweightFunctionality;
import jadex.bdi.runtime.interpreter.AgentRules;
import jadex.bdi.runtime.interpreter.OAVBDIFetcher;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.commons.Tuple;
import jadex.rules.state.IOAVState;

/**
 *  Flyweight for a runtime expression.
 */
public class ExpressionFlyweight extends ElementFlyweight implements IExpression
{
	//-------- constructors --------
	
	/**
	 *  Create a new expression flyweight.
	 *  @param state	The state.
	 *  @param scope	The scope handle.
	 */
	private ExpressionFlyweight(IOAVState state, Object scope, Object handle)
	{
		super(state, scope, handle);
	}
	
	/**
	 *  Get or create a flyweight.
	 *  @return The flyweight.
	 */
	public static ExpressionFlyweight getExpressionFlyweight(IOAVState state, Object scope, Object handle)
	{
		IBDIAgentFeature ip = BDIAgentFeature.getInterpreter(state);
		ExpressionFlyweight ret = (ExpressionFlyweight)ip.getFlyweightCache(IExpression.class, new Tuple(IExpression.class, handle));
		if(ret==null)
		{
			ret = new ExpressionFlyweight(state, scope, handle);
			ip.putFlyweightCache(IExpression.class, new Tuple(IExpression.class, handle), ret);
		}
		return ret;
	}
	
	//-------- methods --------

	/**
	 *  Evaluate the expression.
	 *  @return	The value of the expression.
	 */
	public Object getValue()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					object = AgentRules.evaluateExpression(getState(), getHandle(), new OAVBDIFetcher(getState(), getScope()));
				}
			};
			return invoc.object;
		}
		else
		{
			return AgentRules.evaluateExpression(getState(), getHandle(), new OAVBDIFetcher(getState(), getScope()));
		}
	}

	/**
	 *  Refresh the cached expression value.
	 * /
	public void refresh();*/

	//-------- expression parameters --------

	/**
	 *  Set an expression parameter.
	 *  @param name The parameter name.
	 *  @param value The parameter value.
	 * /
	// changed signature for javaflow, removed 2 final
	public void setParameter(String name, Object value)
	{
		if(isExternalThread())
		{
			new AgentInvocation(new Object[]{name, value})
			{
				public void run()
				{
					fetcher.setValue((String) args[0], args[1]);
				}
			};
		}
		else
		{
			fetcher.setValue(name, value);
		}
	}*/

	/**
	 *  Get an expression parameter.
	 *  @param name The parameter name.
	 *  @return The parameter value.
	 * /
	// changed signature for javaflow, removed final
	public Object getParameter(String name)
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation(name)
			{
				public void run()
				{
					object = fetcher.fetchValue((String) arg);
				}
			};
			return invoc.object;
		}
		else
		{
			return fetcher.fetchValue(name);
		}
	}*/

	/**
	 *  Execute the query.
	 *  @return the result value of the query.
	 */
	public Object	execute()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					object = getValue();
				}
			};
			return invoc.object;
		}
		else
		{
			return getValue();
		}
	}

	/**
	 *  Execute the query using a local parameter.
	 *  @param name The name of the local parameter.
	 *  @param value The value of the local parameter.
	 *  @return the result value of the query.
	 */
	// changed signature for javaflow, removed 2 final
	public Object	execute(String name, Object value)
	{
		// todo: remove values after call
		
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation(new Object[]{name, value})
			{
				public void run()
				{
					object = execute(new String[]{(String)args[0]}, new Object[]{args[1]});
				}
			};
			return invoc.object;
		}
		else
		{
			return execute(new String[]{name}, new Object[]{value});
		}
	}

	/**
	 *  Execute the query using local parameters.
	 *  @param names The names of parameters.
	 *  @param values The parameter values.
	 *  @return The return value.
	 */
	public Object execute(final String[] names, final Object[] values)
	{
		// todo: remove values after call
		
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					object = SFlyweightFunctionality.execute(getState(), getHandle(), getScope(), names, values);
				}
			};
			return invoc.object;
		}
		else
		{
			return SFlyweightFunctionality.execute(getState(), getHandle(), getScope(), names, values);
		}
	}
	
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
					object = new MExpressionFlyweight(getState(), mscope, me);
				}
			};
			return (IMElement)invoc.object;
		}
		else
		{
			Object me = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model);
			Object mscope = getState().getAttributeValue(getScope(), OAVBDIRuntimeModel.element_has_model);
			return new MExpressionFlyweight(getState(), mscope, me);
		}
	}
}
