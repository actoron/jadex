package jadex.rules.rulesystem.rete.nodes;


import java.util.Collection;


/**
 *  A node that is a source of tuples. An arbitrary number
 *  of object consumer nodes can be attached to a tuple source. 
 */
public interface ITupleSourceNode extends INode
{
	/**
	 *  Add an tuple consumer node.
	 *  @param node A new consumer node.
	 */
	public void addTupleConsumer(ITupleConsumerNode node);
	
	/**
	 *  Remove an tuple consumer.
	 *  @param node The consumer node.
	 */
	public void removeTupleConsumer(ITupleConsumerNode node);
	
	/**
	 *  Get the memory for this node.
	 *  @return The memory.
	 */
	public Collection getNodeMemory(ReteMemory mem);

	/**
	 *  Get all tuple consumer nodes.
	 *  @return All tuple consumer nodes.
	 */
	public ITupleConsumerNode[] getTupleConsumers();
}
