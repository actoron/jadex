package jadex.rules.rulesystem.rete.nodes;

import jadex.rules.rulesystem.AbstractAgenda;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVAttributeType;

/**
 *  A node that is able to consume objects from a
 *  source node.
 */
public interface IObjectConsumerNode extends INode
{
	/**
	 *  Send an object to this node.
	 *  @param object The object.
	 */
	public void addObject(Object object, IOAVState state, ReteMemory mem, AbstractAgenda agenda);
	
	/**
	 *  Send a removed object to this node.
	 *  @param object The object.
	 */
	public void removeObject(Object object, IOAVState state, ReteMemory mem, AbstractAgenda agenda);
	
	/**
	 *  Propagate an object change to this node.
	 *  @param object The new object.
	 */
	public void modifyObject(Object object, OAVAttributeType type, Object oldvalue, Object newvalue, IOAVState state, ReteMemory mem, AbstractAgenda agenda);

	/**
	 *  Set the object source of this node.
	 *  @param node The object source node.
	 */
	public void setObjectSource(IObjectSourceNode node);
	
	/**
	 *  Get the object source of this node.
	 *  @return The object source node.
	 */
	public IObjectSourceNode getObjectSource();
}
