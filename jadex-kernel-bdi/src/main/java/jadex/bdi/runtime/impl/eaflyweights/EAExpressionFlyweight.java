package jadex.bdi.runtime.impl.eaflyweights;

import jadex.bdi.model.IMElement;
import jadex.bdi.model.impl.flyweights.MExpressionFlyweight;
import jadex.bdi.runtime.IEAExpression;
import jadex.bdi.runtime.impl.flyweights.ElementFlyweight;
import jadex.bdi.runtime.interpreter.AgentRules;
import jadex.bdi.runtime.interpreter.BDIInterpreter;
import jadex.bdi.runtime.interpreter.OAVBDIFetcher;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.Tuple;
import jadex.rules.state.IOAVState;

/**
 *  Flyweight for a runtime expression.
 */
public class EAExpressionFlyweight extends ElementFlyweight implements IEAExpression
{
	//-------- constructors --------
	
	/**
	 *  Create a new expression flyweight.
	 *  @param state	The state.
	 *  @param scope	The scope handle.
	 */
	private EAExpressionFlyweight(IOAVState state, Object scope, Object handle)
	{
		super(state, scope, handle);
	}
	
	/**
	 *  Get or create a flyweight.
	 *  @return The flyweight.
	 */
	public static EAExpressionFlyweight getExpressionFlyweight(IOAVState state, Object scope, Object handle)
	{
		BDIInterpreter ip = BDIInterpreter.getInterpreter(state);
		EAExpressionFlyweight ret = (EAExpressionFlyweight)ip.getFlyweightCache(IEAExpression.class, new Tuple(IEAExpression.class, handle));
		if(ret==null)
		{
			ret = new EAExpressionFlyweight(state, scope, handle);
			ip.putFlyweightCache(IEAExpression.class, new Tuple(IEAExpression.class, handle), ret);
		}
		return ret;
	}
	
	//-------- methods --------

	/**
	 *  Evaluate the expression.
	 *  @return	The value of the expression.
	 */
	public IFuture getValue()
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					ret.setResult(AgentRules.evaluateExpression(getState(), getHandle(), new OAVBDIFetcher(getState(), getScope())));
				}
			});
		}
		else
		{
			ret.setResult(AgentRules.evaluateExpression(getState(), getHandle(), new OAVBDIFetcher(getState(), getScope())));
		}
		
		return ret;
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
		if(getInterpreter().isExternalThread())
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
		if(getInterpreter().isExternalThread())
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
	public IFuture execute()
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					ret.setResult(AgentRules.evaluateExpression(getState(), getHandle(), new OAVBDIFetcher(getState(), getScope())));
				}
			});
		}
		else
		{
			ret.setResult(AgentRules.evaluateExpression(getState(), getHandle(), new OAVBDIFetcher(getState(), getScope())));
		}
		
		return ret;
	}

	/**
	 *  Execute the query using a local parameter.
	 *  @param name The name of the local parameter.
	 *  @param value The value of the local parameter.
	 *  @return the result value of the query.
	 */
	public IFuture execute(final String name, final Object value)
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					OAVBDIFetcher fetcher = new OAVBDIFetcher(getState(), getScope());
					fetcher.setValue(name, value);
					ret.setResult(AgentRules.evaluateExpression(getState(), getHandle(), fetcher));
				}
			});
		}
		else
		{
			OAVBDIFetcher fetcher = new OAVBDIFetcher(getState(), getScope());
			fetcher.setValue(name, value);
			ret.setResult(AgentRules.evaluateExpression(getState(), getHandle(), fetcher));
		}
		
		return ret;
	}

	/**
	 *  Execute the query using local parameters.
	 *  @param names The names of parameters.
	 *  @param values The parameter values.
	 *  @return The return value.
	 */
	public IFuture execute(final String[] names, final Object[] values)
	{
		// todo: remove values after call
		
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					OAVBDIFetcher fetcher = new OAVBDIFetcher(getState(), getScope());
					for(int i=0; i<names.length; i++)
						fetcher.setValue(names[i], values[i]);
					ret.setResult(AgentRules.evaluateExpression(getState(), getHandle(), fetcher));
				}
			});
		}
		else
		{
			OAVBDIFetcher fetcher = new OAVBDIFetcher(getState(), getScope());
			for(int i=0; i<names.length; i++)
				fetcher.setValue(names[i], values[i]);
			ret.setResult(AgentRules.evaluateExpression(getState(), getHandle(), fetcher));
		}
		
		return ret;
	}
	
	//-------- element interface --------
	
	/**
	 *  Get the model element.
	 *  @return The model element.
	 */
	public IMElement getModelElement()
	{
		if(getInterpreter().isExternalThread())
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

