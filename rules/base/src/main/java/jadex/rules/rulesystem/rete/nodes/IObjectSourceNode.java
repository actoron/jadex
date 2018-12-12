package jadex.rules.rulesystem.rete.nodes;


import java.util.Collection;


/**
 *  A node that is a source of objects. An arbitrary number
 *  of object consumer nodes can be attached to an object source. 
 */
public interface IObjectSourceNode extends INode
{
	/**
	 *  Add an object consumer node.
	 *  @param node A new consumer node.
	 */
	public void addObjectConsumer(IObjectConsumerNode node);
	
	/**
	 *  Remove an object consumer.
	 *  @param node The consumer node.
	 */
	public void removeObjectConsumer(IObjectConsumerNode node);
	
	/**
	 *  Get the memory for this node.
	 *  @return The memory.
	 */
	public Collection getNodeMemory(ReteMemory mem);

	/**
	 *  Get all object consumer nodes.
	 *  @return All object consumer nodes.
	 */
	public IObjectConsumerNode[] getObjectConsumers();
}
