package jadex.bdi.runtime.impl.eaflyweights;

import jadex.bdi.runtime.IEAExpression;
import jadex.bdi.runtime.impl.SFlyweightFunctionality;
import jadex.bdi.runtime.interpreter.BDIInterpreter;
import jadex.bdi.runtime.interpreter.OAVBDIFetcher;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.javaparser.IParsedExpression;
import jadex.rules.state.IOAVState;

/**
 * Flyweight for a runtime expression without model element.
 */
public class EAExpressionNoModel implements IEAExpression
{
	//-------- attributes --------
	
	/** The state. */
	protected IOAVState state;
	
	/** The parsed expression. */
	protected IParsedExpression expression;
	
	/** The value fetcher. */
	protected Object scope;
	
	/** The interpreter. */
	protected BDIInterpreter interpreter;
	
	//-------- constructors --------
	
	/**
	 *  Create a new expression.
	 *  @param state	The state.
	 *  @param scope	The scope handle.
	 */
	public EAExpressionNoModel(IOAVState state, Object scope, IParsedExpression expression)
	{
		this.state = state;
		this.scope = scope;
		this.expression = expression;
		this.interpreter = BDIInterpreter.getInterpreter(state);
	}
	
	//-------- methods --------

	/**
	 *  Evaluate the expression.
	 *  @return	The value of the expression.
	 */
	public IFuture getValue()
	{
		final Future ret = new Future();
		
		if(interpreter.isExternalThread())
		{
			interpreter.getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					ret.setResult(expression.getValue(new OAVBDIFetcher(state, scope)));
				}
			});
		}
		else
		{
			ret.setResult(expression.getValue(new OAVBDIFetcher(state, scope)));
		}
		
		return ret;
	}

	/**
	 *  Refresh the cached expression value.
	 * /
	public void refresh();*/

	//-------- expression parameters --------

	/**
	 *  Execute the query.
	 *  @return the result value of the query.
	 */
	public IFuture execute()
	{
		final Future ret = new Future();

		if(interpreter.isExternalThread())
		{
			interpreter.getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					ret.setResult(getValue());
				}
			});
		}
		else
		{
			ret.setResult(getValue());
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
		
		if(interpreter.isExternalThread())
		{
			interpreter.getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					ret.setResult(execute(new String[]{name}, new Object[]{value}));
				}
			});
		}
		else
		{
			ret.setResult(execute(new String[]{name}, new Object[]{value}));
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
		final Future ret = new Future();

		if(interpreter.isExternalThread())
		{
			interpreter.getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					ret.setResult(SFlyweightFunctionality.execute(state, expression, scope, true, names, values));
				}
			});
		}
		else
		{
			ret.setResult(SFlyweightFunctionality.execute(state, expression, scope, true, names, values));
		}
		
		return ret;
	}
	
	//-------- element interface --------
	
	/**
	 *  Get the model element.
	 *  @return The model element.
	 * /
	public IMElement getModelElement()
	{
		throw new RuntimeException("Expression has no model element.");
	}*/
}

