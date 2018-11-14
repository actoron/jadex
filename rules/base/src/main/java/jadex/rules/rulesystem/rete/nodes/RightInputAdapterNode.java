package jadex.rules.rulesystem.rete.nodes;

import java.util.Collection;

import jadex.rules.rulesystem.AbstractAgenda;
import jadex.rules.rulesystem.rete.Tuple;
import jadex.rules.rulesystem.rete.extractors.AttributeSet;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVAttributeType;

/**
 *  A node for converting a tuple to an object.
 */
public class RightInputAdapterNode extends AbstractNode implements ITupleConsumerNode, IObjectSourceNode
{
	//-------- attributes --------
	
	/** The object consumers. */
	protected IObjectConsumerNode[] oconsumers;
	
	/** The tuple source. */
	protected ITupleSourceNode tsource;
	
	/** The set of relevant attributes. */
	protected volatile AttributeSet	relevants;

	//-------- ITupleConsumer interface --------

	/**
	 *  Create a new node. 
	 */
	public RightInputAdapterNode(int nodeid)
	{
		super(nodeid);
	}
	
	/**
	 *  Add a new tuple to this node.
	 *  @param tuple The tuple.
	 */
	public void addTuple(Tuple tuple, IOAVState state, ReteMemory mem, AbstractAgenda agenda)
	{
		// Propagate tuple as object.
		IObjectConsumerNode[] ocs = oconsumers;
		for(int j=0; ocs!=null && j<ocs.length; j++)
			ocs[j].addObject(tuple, state, mem, agenda);
	}
	
	/**
	 *  Remove a tuple from this node.
	 *  @param tuple The tuple.
	 */
	public void removeTuple(Tuple tuple, IOAVState state, ReteMemory mem, AbstractAgenda agenda)
	{
		// Propagate tuple as object.
		IObjectConsumerNode[] ocs = oconsumers;
		for(int j=0; ocs!=null && j<ocs.length; j++)
			ocs[j].removeObject(tuple, state, mem, agenda);
	}
	
	/**
	 *  Modify a tuple in this node.
	 *  @param tuple The tuple.
	 */
	public void modifyTuple(Tuple tuple, int tupleindex, OAVAttributeType type, Object oldvalue, 
		Object newvalue, IOAVState state, ReteMemory mem, AbstractAgenda agenda)
	{
		if(getRelevantAttributes().contains(type))
		{
			// Propagate tuple as object.
			IObjectConsumerNode[] ocs = oconsumers;
			for(int j=0; ocs!=null && j<ocs.length; j++)
				ocs[j].modifyObject(tuple, type, oldvalue, newvalue, state, mem, agenda);
		}
	}
	
	/**
	 *  Propagate an indirect object change to this node.
	 *  @param object The changed object.
	 */
	public void modifyIndirectObject(Object object, OAVAttributeType type, Object oldvalue, Object newvalue, IOAVState state, ReteMemory mem, AbstractAgenda agenda)
	{
		// Should never be called.
		throw new UnsupportedOperationException("Unsupported method.");
	}

	/**
	 *  Set the tuple source of this node.
	 *  @param node The tuple source node.
	 */
	public void setTupleSource(ITupleSourceNode node)
	{
		this.tsource = node;
	}
	
	/**
	 *  Get the tuple source of this node.
	 *  @return The object source node.
	 */
	public ITupleSourceNode getTupleSource()
	{
		return tsource;
	}

	//-------- object source interface --------
		
	/**
	 *  Add an object consumer node.
	 *  @param node A new consumer node.
	 */
	public void addObjectConsumer(IObjectConsumerNode node)
	{
		if(oconsumers==null)
		{
			oconsumers = new IObjectConsumerNode[]{node};
		}
		else
		{
			IObjectConsumerNode[]	tmp	= new IObjectConsumerNode[oconsumers.length+1];
			System.arraycopy(oconsumers, 0, tmp, 0, oconsumers.length);
			tmp[oconsumers.length]	= node;
			oconsumers	= tmp;
		}

		relevants	= null;	// Will be recalculated on next access;
	}
	
	/**
	 *  Remove an object consumer.
	 *  @param node The consumer node.
	 */
	public void removeObjectConsumer(IObjectConsumerNode node)
	{
		if(oconsumers!=null)
		{
			for(int i=0; i<oconsumers.length; i++)
			{
				if(oconsumers[i].equals(node))
				{
					if(oconsumers.length==1)
					{
						oconsumers	= null;
					}
					else
					{
						IObjectConsumerNode[]	tmp	= new IObjectConsumerNode[oconsumers.length-1];
						if(i>0)
							System.arraycopy(oconsumers, 0, tmp, 0, i);
						if(i<oconsumers.length-1)
							System.arraycopy(oconsumers, i+1, tmp, i, oconsumers.length-1-i);
						oconsumers	= tmp;
					}
					break;
				}
			}
		}
	}
	
	/**
	 *  Get the memory for this node.
	 *  @return The memory.
	 */
	public Collection getNodeMemory(ReteMemory mem)
	{
		// Delegate to source node.
		return tsource.getNodeMemory(mem);
	}

	/**
	 *  Get all object consumer nodes.
	 *  @return All object consumer nodes.
	 */
	public IObjectConsumerNode[] getObjectConsumers()
	{
		return oconsumers;
	}	
	
	//-------- methods --------
	
	/**
	 *  Create the node memory.
	 *  @param state	The state.
	 *  @return The node memory.
	 */
	public Object createNodeMemory(IOAVState state)
	{
		return null;
	}
	
	/**
	 *  Get the set of relevant attribute types.
	 */
	public AttributeSet getRelevantAttributes()
	{
		if(relevants==null)
		{
			synchronized(this)
			{
				if(relevants==null)
				{
					AttributeSet	relevants	= new AttributeSet();
					for(int i=0; oconsumers!=null && i<oconsumers.length; i++)
					{
						relevants.addAll(oconsumers[i].getRelevantAttributes());
					}
					this.relevants	= relevants;
				}
			}
		}
		return relevants;
	}
	
	/**
	 *  Get the set of indirect attribute types.
	 *  I.e. attributes of objects, which are not part of an object conditions
	 *  (e.g. for chained extractors) 
	 *  @return The relevant attribute types.
	 */
	public AttributeSet	getIndirectAttributes()
	{
		return AttributeSet.EMPTY_ATTRIBUTESET;
	}

	//-------- cloneable --------
	
	/**
	 *  Do clone makes a deep clone without regarding cycles.
	 *  Method is overridden by subclasses to actually incorporate their attributes.
	 *  @param theclone The clone.
	 */
	protected void doClone(Object theclone)
	{	
		RightInputAdapterNode clone = (RightInputAdapterNode)theclone;

		// Deep clone tuple consumers
		clone.oconsumers = new IObjectConsumerNode[oconsumers.length];
		for(int i=0; i<oconsumers.length; i++)
			clone.oconsumers[i] = (IObjectConsumerNode)oconsumers[i].clone();
		
		// Set the source
		clone.tsource = (ITupleSourceNode)tsource.clone();
		
		// Shallow copy the relevant attributes
		if(relevants!=null)
			clone.relevants = (AttributeSet)((AttributeSet)relevants).clone();
	}
}
