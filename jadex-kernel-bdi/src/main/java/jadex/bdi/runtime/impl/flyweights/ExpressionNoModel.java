package jadex.bdi.runtime.impl.flyweights;

import jadex.bdi.features.IBDIAgentFeature;
import jadex.bdi.features.impl.BDIAgentFeature;
import jadex.bdi.model.IMElement;
import jadex.bdi.runtime.IExpression;
import jadex.bdi.runtime.impl.SFlyweightFunctionality;
import jadex.bdi.runtime.interpreter.OAVBDIFetcher;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.javaparser.IParsedExpression;
import jadex.rules.state.IOAVState;

/**
 * Flyweight for a runtime expression without model element.
 */
public class ExpressionNoModel implements IExpression
{
	//-------- attributes --------
	
	/** The state. */
	protected IOAVState state;
	
	/** The parsed expression. */
	protected IParsedExpression expression;
	
	/** The value fetcher. */
	protected Object scope;
	
	/** The interpreter. */
	protected IInternalAccess interpreter;
	
	//-------- constructors --------
	
	/**
	 *  Create a new expression.
	 *  @param state	The state.
	 *  @param scope	The scope handle.
	 */
	public ExpressionNoModel(IOAVState state, Object scope, IParsedExpression expression)
	{
		this.state = state;
		this.scope = scope;
		this.expression = expression;
		this.interpreter = BDIAgentFeature.getInternalAccess(state);
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
					object	= expression.getValue(new OAVBDIFetcher(state, scope));
				}
			};
			return invoc.object;
		}
		else
		{
			Object ret	= expression.getValue(new OAVBDIFetcher(state, scope));
			return ret;
		}
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
	public Object execute(final String name, final Object value)
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					object = execute(new String[]{name}, new Object[]{value});
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
					object = SFlyweightFunctionality.execute(state, expression, scope, names, values);
				}
			};
			return invoc.object;
		}
		else
		{
			return SFlyweightFunctionality.execute(state, expression, scope, names, values);
		}
	}
	
	//-------- element interface --------
	
	/**
	 *  Get the model element.
	 *  @return The model element.
	 */
	public IMElement getModelElement()
	{
		throw new RuntimeException("Expression has no model element.");
	}
	
	//-------- inner classes --------

	/**
	 *  An action to be executed on the agent thread.
	 *  Provides predefined variables to store results.
	 *  Directly invokes agenda in construcor.
	 */
	public abstract class AgentInvocation	implements Runnable
	{
		//-------- attributes --------

		/** Argument. */
		protected Object arg;
		
		/** Arguments. */
		protected Object[] args;
		
		//-------- out parameters --------

		/** The object result variable. */
		protected Object	object;

		/** The string result variable. */
		protected String	string;

		/** The int result variable. */
		protected int	integer;

		/** The long result variable. */
		protected long	longint;

		/** The boolean result variable. */
		protected boolean	bool;

		/** The object array result variable. */
		protected Object[]	oarray;

		/** The string result variable. */
		protected String[]	sarray;

		/** The class result variable. */
		protected Class	clazz;

		/** The exception. */
		protected Exception exception;

		//-------- constructors --------

		/**
		 *  Create an action to be executed in sync with the agent thread.
		 */
		public AgentInvocation()
		{
			interpreter.getComponentFeature(IBDIAgentFeature.class).invokeSynchronized(this);
		}
		
		/**
		 *  Create an action to be executed in sync with the agent thread.
		 */
		public AgentInvocation(Object arg)
		{
			this.arg = arg;
			interpreter.getComponentFeature(IBDIAgentFeature.class).invokeSynchronized(this);
		}
		
		/**
		 *  Create an action to be executed in sync with the agent thread.
		 */
		public AgentInvocation(Object[] args)
		{
			this.args = args;
			interpreter.getComponentFeature(IBDIAgentFeature.class).invokeSynchronized(this);
		}
	}
	
	/**
	 * 
	 */
	protected boolean isExternalThread()
	{
		return !BDIAgentFeature.getInternalAccess(state).getComponentFeature(IExecutionFeature.class).isComponentThread();
	}
}
