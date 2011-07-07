package jadex.kernelbase;

import jadex.javaparser.SimpleValueFetcher;

/**
 *  Fetcher for interpreter that supports:
 *  Supports:
 *  - $args, $arguments
 *  - $properties
 *  - $results
 *  - $component
 *  - $provider
 */
public class InterpreterFetcher extends SimpleValueFetcher
{
	/** The interpreter. */
	protected StatelessAbstractInterpreter interpreter;
	
	/**
	 *  Create a new interpreter.
	 */
	public InterpreterFetcher(StatelessAbstractInterpreter interpreter)
	{
		this.interpreter = interpreter;
	}
	
	/**
	 *  Fetch a value via its name.
	 *  @param name The name.
	 *  @return The value.
	 */
	public Object fetchValue(String name)
	{
		Object ret = null;
		
		if(name==null)
			throw new RuntimeException("Name must not be null.");
		
		if(name.equals("$args") || name.equals("$arguments"))
		{
			ret = interpreter.getArguments();
		}
		else if(name.equals("$properties"))
		{
			ret = interpreter.getProperties();
		}
		else if(name.equals("$results"))
		{
			ret = interpreter.getResults();
		}
		else if(name.equals("$component"))
		{
			ret = interpreter.getInternalAccess();
		}
		else if(name.equals("$provider") || name.equals("container"))
		{
			ret = interpreter.getServiceContainer();
		}
		else
		{
			ret = super.fetchValue(name);
		}
		
		return ret;
	}
	
	/**
	 *  Get the interpreter.
	 *  @return The interpreter.
	 */
	public StatelessAbstractInterpreter getInterpreter()
	{
		return interpreter;
	}
}
