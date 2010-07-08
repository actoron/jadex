package jadex.service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 *  Default visit decider that implements the following strategy:
 *  - record visited nodes and don't visit any node twice
 *  - Use up and down flags for searching in specific directions only.
 */
public class DefaultVisitDecider implements IVisitDecider
{
	//-------- attributes --------
	
	/** The set of visited nodes. */
	protected Set visited;

	/** A flag that indicates if node should not be searched when one result is already available. */
	protected boolean abort;
	
	//-------- constructors --------

	/**
	 *  Create a new visit decider.
	 *  Abort on first service found is true.
	 */
	public DefaultVisitDecider()
	{
		this(true);
	}
	
	/**
	 *  Create a new visit decider.
	 */
	public DefaultVisitDecider(boolean abort)
	{
		this.visited = new HashSet();
		this.abort = abort;
	}
	
	//-------- methods --------
	
	/**
	 *  Test if a specific node should be searched.
	 *  @param source The source data provider.
	 *  @param target The target data provider.
	 *  @param results The preliminary results.
	 */
	public synchronized boolean searchNode(IServiceProvider source, IServiceProvider target, Collection results)
	{
		boolean ret = !(abort && results.size()>0);
		
		if(ret && !visited.contains(target.getId()))
		{
			visited.add(target);
			ret = true;
		}
		
		return ret;
	}
}
