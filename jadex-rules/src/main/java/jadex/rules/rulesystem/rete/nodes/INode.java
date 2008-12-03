package jadex.rules.rulesystem.rete.nodes;


import jadex.rules.state.IOAVState;

import java.util.Collection;
import java.util.Set;

/**
 *  Basic interface for all nodes.
 */
public interface INode extends Cloneable
{
	/**
	 *  Get the nodeid.
	 *  @return The nodeid.
	 */
	public int getNodeId();

	/**
	 *  Get the use count.
	 *  @return The number of rules that use this node.
	 */
	//public int getUseCount();
	
	/**
	 *  Increment use count.
	 */
	//public void incrementUseCount();
	
	/**
	 *  Decrement use count.
	 */
	//public void decrementUseCount();
	
	/**
	 *  Get the set of relevant attribute types.
	 */
	public Set	getRelevantAttributes();
	
	/**
	 *  Create the node memory.
	 *  @param state	The state.
	 *  @return The node memory.
	 */
	public Object createNodeMemory(IOAVState state);
	
	/**
	 *  Get the memory for this node.
	 *  @return The memory.
	 */
	public Collection getNodeMemory(ReteMemory mem);
	
	//-------- cloneable --------
	
	/**
	 *  Clone this object.
	 *  @return A clone of this object.
	 */
	public Object clone();

	//-------- debugging --------

	/**
	 *  Check the consistency of the node.
	 */
	public boolean	checkNodeConsistency(ReteMemory mem);
}
