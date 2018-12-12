package jadex.rules.rulesystem.rete.nodes;

import jadex.rules.rulesystem.AbstractAgenda;
import jadex.rules.rulesystem.rete.Tuple;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVAttributeType;

/**
 *  A node that is able to consume tuples from a
 *  source node.
 */
public interface ITupleConsumerNode extends INode
{
	/**
	 *  Add a new tuple to this node.
	 *  @param tuple The tuple.
	 */
	public void addTuple(Tuple tuple, IOAVState state, ReteMemory mem, AbstractAgenda agenda);
	
	/**
	 *  Remove a tuple from this node.
	 *  @param tuple The tuple.
	 */
	public void removeTuple(Tuple tuple, IOAVState state, ReteMemory mem, AbstractAgenda agenda);
	
	/**
	 *  Modify a tuple in this node.
	 *  @param tuple The tuple.
	 */
	public void modifyTuple(Tuple tuple, int tupleindex, OAVAttributeType type, Object oldvalue, Object newvalue, IOAVState state, ReteMemory mem, AbstractAgenda agenda);
	
	/**
	 *  Set the tuple source of this node.
	 *  @param node The tuple source node.
	 */
	public void setTupleSource(ITupleSourceNode node);
	
	/**
	 *  Get the tuple source of this node.
	 *  @return The tuple source node.
	 */
	public ITupleSourceNode getTupleSource();
}
