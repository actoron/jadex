package jadex.rules.rulesystem.rete.nodes;


import java.util.Collection;

import jadex.rules.rulesystem.AbstractAgenda;
import jadex.rules.rulesystem.rete.extractors.AttributeSet;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVAttributeType;

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
	public AttributeSet getRelevantAttributes();
	
	/**
	 *  Get the set of indirect attribute types.
	 *  I.e. attributes of objects, which are not part of an object conditions
	 *  (e.g. for chained extractors) 
	 *  @return The relevant attribute types.
	 */
	public AttributeSet getIndirectAttributes();

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
	
	/**
	 *  Propagate an indirect object change to this node.
	 *  @param object The changed object.
	 */
	public void modifyIndirectObject(Object object, OAVAttributeType type, Object oldvalue, Object newvalue, IOAVState state, ReteMemory mem, AbstractAgenda agenda);

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
