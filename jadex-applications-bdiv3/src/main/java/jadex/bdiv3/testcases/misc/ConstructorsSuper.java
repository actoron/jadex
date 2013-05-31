package jadex.bdiv3.testcases.misc;

import jadex.micro.annotation.Agent;

import java.util.ArrayList;
import java.util.List;

/**
 *  Test if constructors are called in correct order.
 */
@Agent
public abstract class ConstructorsSuper
{
	//-------- attributes --------
	
	/** The constructor calls. */
	protected List<String>	calls	= new ArrayList<String>();
	
	//-------- constructors --------
	
	/**
	 *  Create the agent.
	 */
	public ConstructorsSuper()
	{
		calls.add("A");
	}
	
	/**
	 *  Create the agent.
	 */
	public ConstructorsSuper(String arg)
	{
		this();
		calls.add(arg);
	}
}
