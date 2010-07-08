package jadex.service;

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
	
	/** Flag if search upwards is ok. */
	protected boolean up;
	
	/** Flag if search downwards is ok. */
	protected boolean down;
	
	//-------- constructors --------

	/**
	 *  Create a new visit decider.
	 */
	public DefaultVisitDecider()
	{
		this(true, true);
	}
	
	/**
	 *  Create a new visit decider.
	 */
	public DefaultVisitDecider(boolean up, boolean down)
	{
		this.visited = new HashSet();
		this.up = up;
		this.down = down;
	}
	
	//-------- methods --------
	
	/**
	 *  Test if a specific node should be searched.
	 *  @param source The source data provider.
	 *  @param target The target data provider.
	 *  @param up A flag indicating the search direction.
	 */
	public synchronized boolean searchNode(IServiceProvider source, IServiceProvider target, boolean up)
	{
		boolean ret = false;
		
		if(!visited.contains(target.getName()))
		{
			if(up && this.up || !up && this.down)
			{
				visited.add(target);
				ret = true;
			}
		}
		
		return ret;
	}
}
