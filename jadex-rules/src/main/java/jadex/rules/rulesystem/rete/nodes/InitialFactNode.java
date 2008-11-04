package jadex.rules.rulesystem.rete.nodes;

import jadex.rules.rulesystem.AbstractAgenda;
import jadex.rules.rulesystem.rete.Tuple;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVAttributeType;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 *  Dummy fact node for not conditions, which are no joins.
 */
public class InitialFactNode extends AbstractNode implements ITupleSourceNode, IObjectConsumerNode
{
	//-------- constants -------
	
	/** The initial fact. */
	// Todo: should be in state?
	public static final String	INITIAL_FACT	= "initial-fact";
	
	/** The initial fact tuple. */
	public static final Tuple	INITIAL_FACT_TUPLE	= new Tuple(null, INITIAL_FACT);
	
	/** The initial fact memory. */
	public static final Collection	INITIAL_FACT_MEM	= Collections.singletonList(INITIAL_FACT_TUPLE);
	
	//-------- attributes --------
	
	/** The tuple consumers. */
	protected ITupleConsumerNode[]	tconsumers;
	
	/** The object source. */
	protected IObjectSourceNode osource;

	/** The set of relevant attributes. */
	protected Set	relevants;

	//-------- methods --------
	
	/**
	 *  Initialize the pattern matcher.
	 *  Called before the agenda is accessed
	 *  to perform any initialization, if necessary.
	 */
	public void init(IOAVState state, ReteMemory mem, AbstractAgenda agenda)
	{
		ITupleConsumerNode[]	tcon	= tconsumers;
		for(int i=0; tcon!=null && i<tcon.length; i++)
			tcon[i].addTuple(INITIAL_FACT_TUPLE, state, mem, agenda);
	}

	//-------- object consumer interface --------
	
	/**
	 *  Send a new object to this node.
	 *  @param object The object.
	 */
	public void addObject(Object object, IOAVState state, ReteMemory mem, AbstractAgenda agenda)
	{
		//System.out.println("Add object called: "+this+" "+object);
		
		// Todo: support propagation of initial fact with reset?
	}
	
	/**
	 *  Send a removed object to this node.
	 *  @param object The object.
	 */
	public void removeObject(Object object, IOAVState state, ReteMemory mem, AbstractAgenda agenda)
	{
		//System.out.println("Remove object called: "+this+" "+object);
		
		// Todo: support retraction of initial fact with reset?
	}
	
	/**
	 *  Propagate an object change to this node.
	 *  @param object The new object.
	 */
	public void modifyObject(Object object, OAVAttributeType type, Object oldvalue, Object newvalue, IOAVState state, ReteMemory mem, AbstractAgenda agenda)
	{
		// Nothing to do...
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
		return INITIAL_FACT_MEM;
	}


	//-------- INode methods --------
	
	/**
	 *  Create the node memory.
	 *  @return The node memory.
	 */
	public Object createNodeMemory()
	{
		return null;
	}
	
	/**
	 *  Get the set of relevant attribute types.
	 */
	public Set	getRelevantAttributes()
	{
		if(relevants==null)
		{
			synchronized(this)
			{
				if(relevants==null)
				{
					relevants	= new HashSet();
					for(int i=0; tconsumers!=null && i<tconsumers.length; i++)
					{
						relevants.addAll(tconsumers[i].getRelevantAttributes());
					}
				}
			}
		}
		return relevants;
	}

	//-------- cloneable --------
	
	/**
	 *  Do clone makes a deep clone without regarding cycles.
	 *  Method is overridden by subclasses to actually incorporate their attributes.
	 *  @param theclone The clone.
	 */
	protected void doClone(Object theclone)
	{	
		InitialFactNode clone = (InitialFactNode)theclone;
		
		// Deep clone tuple consumers
		clone.tconsumers = new ITupleConsumerNode[tconsumers.length];
		for(int i=0; i<tconsumers.length; i++)
			clone.tconsumers[i] = (ITupleConsumerNode)tconsumers[i].clone();
		
		// Set the source
		clone.osource = (IObjectSourceNode)osource.clone();
	}
}
