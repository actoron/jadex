package jadex.bdi.runtime.impl;

import jadex.bdi.interpreter.BDIInterpreter;
import jadex.bdi.interpreter.OAVBDIFetcher;
import jadex.bdi.runtime.IExpression;
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
	protected OAVBDIFetcher fetcher;
	
	/** The interpreter. */
	protected BDIInterpreter interpreter;
	
	//-------- constructors --------
	
	/**
	 *  Create a new expression.
	 *  @param state	The state.
	 *  @param scope	The scope handle.
	 */
	public ExpressionNoModel(IOAVState state, Object scope, IParsedExpression expression)
	{
		this.state = state;
		this.expression = expression;
		this.fetcher = new OAVBDIFetcher(state, scope);
		this.interpreter = BDIInterpreter.getInterpreter(state);
	}
	
	//-------- methods --------

	/**
	 *  Evaluate the expression.
	 *  @return	The value of the expression.
	 */
	public Object getValue()
	{
		if(interpreter.isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					object	= expression.getValue(fetcher);
				}
			};
			return invoc.object;
		}
		else
		{
			Object ret	= expression.getValue(fetcher);
			return ret;
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
	 */
	// changed signature for javaflow, removed 2 final
	public void setParameter(String name, Object value)
	{
		if(interpreter.isExternalThread())
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
	}

	/**
	 *  Get an expression parameter.
	 *  @param name The parameter name.
	 *  @return The parameter value.
	 */
	// changed signature for javaflow, removed final from argument(s)
	public Object getParameter(String name)
	{
		if(interpreter.isExternalThread())
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
	}

	/**
	 *  Execute the query.
	 *  @return the result value of the query.
	 */
	public Object	execute()
	{
		if(interpreter.isExternalThread())
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
	// changed signature for javaflow, removed final from argument(s)
	public Object	execute(String name, Object value)
	{
		// todo: remove values after call
		
		if(interpreter.isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation(new Object[]{name, value})
			{
				public void run()
				{
					fetcher.setValue((String) args[0], args[1]);
					object = getValue();
				}
			};
			return invoc.object;
		}
		else
		{
			fetcher.setValue(name, value);
			return getValue();
		}
	}

	/**
	 *  Execute the query using local parameters.
	 *  @param names The names of parameters.
	 *  @param values The parameter values.
	 *  @return The return value.
	 */
	public Object	execute(String[] names, Object[] values)
	{
		// todo: remove values after call
		
		if(interpreter.isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation(new Object[]{names, values})
			{
				public void run()
				{
					String[] names = (String[]) args[0];
					Object[] values = (Object[]) args[1];
					for(int i=0; i<names.length; i++)
						fetcher.setValue(names[i], values[i]);
					object = getValue();
				}
			};
			return invoc.object;
		}
		else
		{
			for(int i=0; i<names.length; i++)
				fetcher.setValue(names[i], values[i]);
			return getValue();
		}
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
			interpreter.invokeSynchronized(this);
		}
		
		/**
		 *  Create an action to be executed in sync with the agent thread.
		 */
		public AgentInvocation(Object arg)
		{
			this.arg = arg;
			interpreter.invokeSynchronized(this);
		}
		
		/**
		 *  Create an action to be executed in sync with the agent thread.
		 */
		public AgentInvocation(Object[] args)
		{
			this.args = args;
			interpreter.invokeSynchronized(this);
		}
	}
}
