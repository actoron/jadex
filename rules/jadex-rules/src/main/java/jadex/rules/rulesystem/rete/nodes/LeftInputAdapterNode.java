package jadex.rules.rulesystem.rete.nodes;

import java.util.Collection;
import java.util.LinkedHashSet;

import jadex.rules.rulesystem.AbstractAgenda;
import jadex.rules.rulesystem.rete.Tuple;
import jadex.rules.rulesystem.rete.extractors.AttributeSet;
import jadex.rules.state.IOAVState;
import jadex.rules.state.IProfiler;
import jadex.rules.state.OAVAttributeType;

/**
 *  A node for converting an object to a tuple.
 */
public class LeftInputAdapterNode extends AbstractNode implements IObjectConsumerNode, ITupleSourceNode
{
	//-------- attributes --------
	
	/** The tuple consumers. */
	protected ITupleConsumerNode[] tconsumers;
	
	/** The object source. */
	protected IObjectSourceNode osource;
	
	/** The set of relevant attributes. */
	protected volatile AttributeSet	relevants;

	//-------- object consumer node --------
	
	/**
	 *  Create a new node. 
	 */
	public LeftInputAdapterNode(int nodeid)
	{
		super(nodeid);
	}
	
	/**
	 *  Send an object to this node.
	 *  @param object The object.
	 */
	public void addObject(Object object, IOAVState state, ReteMemory mem, AbstractAgenda agenda)
	{
		//System.out.println("Add object called: "+this+" "+object);
		state.getProfiler().start(IProfiler.TYPE_NODE, this);
		state.getProfiler().start(IProfiler.TYPE_NODEEVENT, IProfiler.NODEEVENT_OBJECTADDED);
		
		Collection amem = (Collection)mem.getNodeMemory(this);
		Tuple tuple = mem.getTuple(state, null, object);
		amem.add(tuple);
		
		ITupleConsumerNode[] tcs = tconsumers;
		for(int j=0; tcs!=null && j<tcs.length; j++)
			tcs[j].addTuple(tuple, state, mem, agenda);

		state.getProfiler().stop(IProfiler.TYPE_NODEEVENT, IProfiler.NODEEVENT_OBJECTADDED);
		state.getProfiler().stop(IProfiler.TYPE_NODE, this);
	}
	
	/**
	 *  Send a removed object to this node.
	 *  @param object The object.
	 */
	public void removeObject(Object object, IOAVState state, ReteMemory mem, AbstractAgenda agenda)
	{
		//System.out.println("Remove object called: "+this+" "+object);
		state.getProfiler().start(IProfiler.TYPE_NODE, this);
		state.getProfiler().start(IProfiler.TYPE_NODEEVENT, IProfiler.NODEEVENT_OBJECTREMOVED);
		
		if(mem.hasNodeMemory(this))
		{
			Collection amem = (Collection)mem.getNodeMemory(this);
			Tuple tuple = mem.getTuple(state, null, object);
			if(amem.remove(tuple))
			{
				ITupleConsumerNode[] tcs = tconsumers;
				for(int j=0; tcs!=null && j<tcs.length; j++)
					tcs[j].removeTuple(tuple, state, mem, agenda);
			}
		}

		state.getProfiler().stop(IProfiler.TYPE_NODEEVENT, IProfiler.NODEEVENT_OBJECTREMOVED);
		state.getProfiler().stop(IProfiler.TYPE_NODE, this);
	}
	
	/**
	 *  Propagate an object change to this node.
	 *  @param object The new object.
	 */
	public void modifyObject(Object object, OAVAttributeType type, Object oldvalue, Object newvalue, IOAVState state, ReteMemory mem, AbstractAgenda agenda)
	{
		state.getProfiler().start(IProfiler.TYPE_NODE, this);
		state.getProfiler().start(IProfiler.TYPE_NODEEVENT, IProfiler.NODEEVENT_OBJECTMODIFIED);

		if(getRelevantAttributes().contains(type))
		{
			// Check if modification changes node memory.
			Tuple tuple = mem.getTuple(state, null, object);
			ITupleConsumerNode[] tcs = tconsumers;
			for(int j=0; tcs!=null && j<tcs.length; j++)
				tcs[j].modifyTuple(tuple, 0, type, oldvalue, newvalue, state, mem, agenda);
		}

		state.getProfiler().stop(IProfiler.TYPE_NODEEVENT, IProfiler.NODEEVENT_OBJECTMODIFIED);
		state.getProfiler().stop(IProfiler.TYPE_NODE, this);
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
	 *  Set the object source of this node.
	 *  @param node The object source node.
	 */
	public void setObjectSource(IObjectSourceNode node)
	{
		this.osource = node;
	}
	
	/**
	 *  Get the object source of this node.
	 *  @return The object source node.
	 */
	public IObjectSourceNode getObjectSource()
	{
		return osource;
	}
	
	//-------- tuple source interface --------
	
	/**
	 *  Add an tuple consumer node.
	 *  @param node A new consumer node.
	 */
	public void addTupleConsumer(ITupleConsumerNode node)
	{
		if(tconsumers==null)
		{
			tconsumers = new ITupleConsumerNode[]{node};
		}
		else
		{
			ITupleConsumerNode[]	tmp	= new ITupleConsumerNode[tconsumers.length+1];
			System.arraycopy(tconsumers, 0, tmp, 0, tconsumers.length);
			tmp[tconsumers.length]	= node;
			tconsumers	= tmp;
		}

		relevants	= null;	// Will be recalculated on next access;
	}
	
	/**
	 *  Remove an tuple consumer.
	 *  @param node The consumer node.
	 */
	public void removeTupleConsumer(ITupleConsumerNode node)
	{
		if(tconsumers!=null)
		{
			for(int i=0; i<tconsumers.length; i++)
			{
				if(tconsumers[i].equals(node))
				{
					if(tconsumers.length==1)
					{
						tconsumers	= null;
					}
					else
					{
						ITupleConsumerNode[]	tmp	= new ITupleConsumerNode[tconsumers.length-1];
						if(i>0)
							System.arraycopy(tconsumers, 0, tmp, 0, i);
						if(i<tconsumers.length-1)
							System.arraycopy(tconsumers, i+1, tmp, i, tconsumers.length-1-i);
						tconsumers	= tmp;
					}
					break;
				}
			}
		}
	}
	
	/**
	 *  Get all tuple consumer nodes.
	 *  @return All tuple consumer nodes.
	 */
	public ITupleConsumerNode[] getTupleConsumers()
	{
		return tconsumers;
	}
	
	/**
	 *  Get the memory for this node.
	 *  @return The memory.
	 */
	public Collection getNodeMemory(ReteMemory mem)
	{
		return mem.hasNodeMemory(this) ? (Collection)mem.getNodeMemory(this) : null;
	}
	
	//-------- methods --------
	
	/**
	 *  Create the node memory.
	 *  @param state	The state.
	 *  @return The node memory.
	 */
	public Object createNodeMemory(IOAVState state)
	{
		return new LinkedHashSet();
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
					for(int i=0; tconsumers!=null && i<tconsumers.length; i++)
					{
						relevants.addAll(tconsumers[i].getRelevantAttributes());
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
	 *  Clone this object.
	 *  @return A clone of this object.
	 */
	public Object clone()
	{
		LeftInputAdapterNode ret = null;
		
		try
		{	
			ret = (LeftInputAdapterNode)super.clone();
			
			// Deep clone tuple consumers
			ret.tconsumers = new ITupleConsumerNode[tconsumers.length];
			for(int i=0; i<tconsumers.length; i++)
			{
				ret.tconsumers[i] = (ITupleConsumerNode)tconsumers[i].clone();
				ret.tconsumers[i].setTupleSource(ret);
			}
			
			// Don't change the source, will be done by the source
			
			// Shallow copy the relevant attributes
			if(relevants!=null)
				ret.relevants = (AttributeSet)((AttributeSet)relevants).clone();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException("Cloning did not work.");
		}
		
		return ret;
	}
	
	//-------- cloneable --------
	
	/**
	 *  Do clone makes a deep clone without regarding cycles.
	 *  Method is overridden by subclasses to actually incorporate their attributes.
	 *  @param theclone The clone.
	 */
	protected void doClone(Object theclone)
	{	
		LeftInputAdapterNode clone = (LeftInputAdapterNode)theclone;
		
		// Deep clone tuple consumers
		clone.tconsumers = new ITupleConsumerNode[tconsumers.length];
		for(int i=0; i<tconsumers.length; i++)
			clone.tconsumers[i] = (ITupleConsumerNode)tconsumers[i].clone();
		
		// Set the source
		clone.osource = (IObjectSourceNode)osource.clone();
		
		// Shallow copy the relevant attributes
		if(relevants!=null)
			clone.relevants = (AttributeSet)((AttributeSet)relevants).clone();
	}
}
