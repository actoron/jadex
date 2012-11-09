package jadex.bpmn.model;

import java.util.ArrayList;
import java.util.List;

/**
 *  A named configuration.
 */
public class MConfiguration extends MNamedIdElement
{
	//-------- attributes --------
	
	/** The list of arguments. */
	protected List arguments;

	/** The associated pools. */
	protected List pools;
	
	//-------- constructors --------
	
	/**
	 *  Create a new configuration.
	 */
	public MConfiguration()
	{
		this.arguments = new ArrayList();
		this.pools = new ArrayList();
	}
	
	//-------- methods --------
	
	/**
	 *  Add an argument.
	 *  @param argument The argument.
	 */
	public void addArgument(Object argument)
	{
		this.arguments.add(argument);
	}

	/**
	 *  Get the arguments.
	 *  @return The arguments.
	 */
	public List getArguments()
	{
		return this.arguments;
	}
	
	/**
	 *  Add a pool.
	 *  @param pool The pool.
	 */
	public void addPool(MPool pool)
	{
		this.pools.add(pool);
	}

	/**
	 *  Get the pools.
	 *  @return The pools.
	 */
	public List getPools()
	{
		return this.pools;
	}
	
}
