package jadex.rules.rulesystem.rete.nodes;

import java.util.Collection;
import java.util.LinkedHashSet;

import jadex.rules.rulesystem.AbstractAgenda;
import jadex.rules.rulesystem.rete.Tuple;
import jadex.rules.rulesystem.rete.constraints.IConstraintEvaluator;
import jadex.rules.rulesystem.rete.extractors.AttributeSet;
import jadex.rules.state.IOAVState;
import jadex.rules.state.IProfiler;
import jadex.rules.state.OAVAttributeType;


/**
 *  A test node takes evaluates a predicate.
 */
public class TestNode extends AbstractNode implements ITupleConsumerNode, ITupleSourceNode
{
	//-------- attributes --------
	
	/** The tuple consumers. */
	protected ITupleConsumerNode[] tconsumers;
	
	/** The tuple source. */
	protected ITupleSourceNode tsource;
	
	/** The constraint evaluator. */
	final protected IConstraintEvaluator evaluator;
	
	/** The set of relevant attributes. */
	protected volatile AttributeSet relevants;
	
	/** The set of indirect attributes. */
	protected volatile AttributeSet indirects;
	
	//-------- constructors --------
	
	/**
	 *  Create a new test node.
	 *  @param evaluator The evaluator.
	 */
	public TestNode(int nodeid, IConstraintEvaluator evaluator)
	{
		super(nodeid);
		this.evaluator = evaluator;
	}
	
	//-------- ITupleConsumerNode ---------
	
	/**
	 *  Add a new tuple to this node.
	 *  @param tuple The tuple.
	 */
	public void addTuple(Tuple tuple, IOAVState state, ReteMemory mem, AbstractAgenda agenda)
	{
		//System.out.println("Add tuple called: "+this+" "+tuple);
		state.getProfiler().start(IProfiler.TYPE_NODE, this);
		state.getProfiler().start(IProfiler.TYPE_NODEEVENT, IProfiler.NODEEVENT_TUPLEADDED);
		
		Collection tmem = (Collection)mem.getNodeMemory(this);
		if(!tmem.contains(tuple) && evaluator.evaluate(null, tuple, state))
		{
			tmem.add(tuple);
			//System.out.println("Tuple passed constraint check: "+this+" "+object);
			propagateAdditionToTupleConsumers(tuple, state, mem, agenda);
		}

		state.getProfiler().stop(IProfiler.TYPE_NODEEVENT, IProfiler.NODEEVENT_TUPLEADDED);
		state.getProfiler().stop(IProfiler.TYPE_NODE, this);
	}
	
	/**
	 *  Remove a tuple from this node.
	 *  @param tuple The tuple.
	 */
	public void removeTuple(Tuple tuple, IOAVState state, ReteMemory mem, AbstractAgenda agenda)
	{
		//System.out.println("Remove tuple called: "+this+" "+tuple);
		state.getProfiler().start(IProfiler.TYPE_NODE, this);
		state.getProfiler().start(IProfiler.TYPE_NODEEVENT, IProfiler.NODEEVENT_TUPLEREMOVED);
		
		if(mem.hasNodeMemory(this))
		{
			if(((Collection)mem.getNodeMemory(this)).remove(tuple))
			{
				propagateRemovalToTupleConsumers(tuple, state, mem, agenda);
			}
		}

		state.getProfiler().stop(IProfiler.TYPE_NODEEVENT, IProfiler.NODEEVENT_TUPLEREMOVED);
		state.getProfiler().stop(IProfiler.TYPE_NODE, this);
	}
	
	/**
	 *  Modify a tuple in this node.
	 *  @param tuple The tuple.
	 */
	public void modifyTuple(Tuple tuple, int tupleindex, OAVAttributeType type, 
		Object oldvalue, Object newvalue, IOAVState state, ReteMemory mem, AbstractAgenda agenda)
	{
		//System.out.println("Modify object called: "+this+" "+object);
		state.getProfiler().start(IProfiler.TYPE_NODE, this);
		state.getProfiler().start(IProfiler.TYPE_NODEEVENT, IProfiler.NODEEVENT_TUPLEMODIFIED);

		if(getRelevantAttributes().contains(type))
		{
			// Check if modification changes node memory.
			boolean affected = isAffected(type);
			boolean	contains	= mem.hasNodeMemory(this) && ((Collection)mem.getNodeMemory(this)).contains(tuple);
		
			if(affected)
			{
				boolean check = evaluator.evaluate(null, tuple, state);
				
				// tuple no longer valid -> remove
				if(contains && !check)
				{
					((Collection)mem.getNodeMemory(this)).remove(tuple);
					propagateRemovalToTupleConsumers(tuple, state, mem, agenda);
				}
		
				// tuple newly valid -> add
				else if(!contains && check)
				{
					((Collection)mem.getNodeMemory(this)).add(tuple);
					propagateAdditionToTupleConsumers(tuple, state, mem, agenda);
				}
				
				else if(contains)
				{
					propagateModificationToTupleConsumers(tuple, tupleindex, type, 
						oldvalue, newvalue, state, mem, agenda);
				}
			}
			else
			{
				// tuple changed in memory -> propagate modification
				if(contains)
				{
					propagateModificationToTupleConsumers(tuple, tupleindex, type, 
						oldvalue, newvalue, state, mem, agenda);
				}
			}
		}

		state.getProfiler().stop(IProfiler.TYPE_NODEEVENT, IProfiler.NODEEVENT_TUPLEMODIFIED);
		state.getProfiler().stop(IProfiler.TYPE_NODE, this);
	}
	
	/**
	 *  Propagate an indirect object change to this node.
	 *  @param object The changed object.
	 */
	public void modifyIndirectObject(Object object, OAVAttributeType type, Object oldvalue, Object newvalue, IOAVState state, ReteMemory mem, AbstractAgenda agenda)
	{
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
	
	//-------- ITupleSourceNode --------
	
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
	 *  Get the memory for this node.
	 *  @return The memory.
	 */
	public Collection getNodeMemory(ReteMemory mem)
	{
		return mem.hasNodeMemory(this) ? (Collection)mem.getNodeMemory(this) : null;
	}

	/**
	 *  Get all tuple consumer nodes.
	 *  @return All tuple consumer nodes.
	 */
	public ITupleConsumerNode[] getTupleConsumers()
	{
		return tconsumers;
	}
	
	//-------- INode --------
	
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
					relevants.addAll(evaluator.getRelevantAttributes());
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
		if(indirects==null)
		{
			synchronized(this)
			{
				if(indirects==null)
				{
					AttributeSet	indirects	= new AttributeSet();
					indirects.addAll(evaluator.getIndirectAttributes());
					this.indirects	= indirects;
				}
			}
		}
		return indirects;
	}

	/**
	 *  Create the node memory.
	 *  @param state	The state.
	 *  @return The node memory.
	 */
	public Object createNodeMemory(IOAVState state)
	{
		return new LinkedHashSet();
	}
	
	//-------- methods --------
	
	/**
	 *  Test if the node is affected from a modification.
	 *  @param type The attribute type.
	 *  @return True, if possibly affected.
	 */
	public boolean isAffected(OAVAttributeType attr)
	{
		return evaluator.isAffected(-1, attr);
	}
	
	/**
	 *  Get the evaluator.
	 */
	public IConstraintEvaluator getConstraintEvaluator()
	{
		return evaluator;
	}
	
	//-------- helper methods --------

	/**
	 *  Propagate a new tuple to all tuple consumers.
	 *  @param tuple The new tuple.
	 */
	protected void propagateAdditionToTupleConsumers(Tuple tuple, IOAVState state, ReteMemory mem, AbstractAgenda agenda)
	{
		ITupleConsumerNode[]	tcon	= tconsumers;
		for(int i=0; tcon!=null && i<tcon.length; i++)
			tcon[i].addTuple(tuple, state, mem, agenda);
	}
	
	/**
	 *  Propagate a removed tuple to all tuple consumers.
	 *  @param tuple The new tuple.
	 */
	protected void propagateRemovalToTupleConsumers(Tuple tuple, IOAVState state, ReteMemory mem, AbstractAgenda agenda)
	{
		ITupleConsumerNode[]	tcon	= tconsumers;
		for(int i=0; tcon!=null && i<tcon.length; i++)
			tcon[i].removeTuple(tuple, state, mem, agenda);
	}
	
	/**
	 *  Propagate a modified tuple to all tuple consumers.
	 *  @param tuple The new tuple.
	 */
	protected void propagateModificationToTupleConsumers(Tuple tuple, int tupleindex, OAVAttributeType type, Object oldvalue, 
		Object newvalue, IOAVState state, ReteMemory mem, AbstractAgenda agenda)
	{
		ITupleConsumerNode[]	tcon	= tconsumers;
		for(int i=0; tcon!=null && i<tcon.length; i++)
			tcon[i].modifyTuple(tuple, tupleindex, type, oldvalue, newvalue, state, mem, agenda);
	}
	
	//-------- cloneable --------
	
	/**
	 *  Do clone makes a deep clone without regarding cycles.
	 *  Method is overridden by subclasses to actually incorporate their attributes.
	 *  @param theclone The clone.
	 */
	protected void doClone(Object theclone)
	{	
		TestNode clone = (TestNode)theclone;

		// Deep clone tuple consumers
		clone.tconsumers = new ITupleConsumerNode[tconsumers.length];
		for(int i=0; i<tconsumers.length; i++)
			clone.tconsumers[i] = (ITupleConsumerNode)tconsumers[i].clone();
		
		// Set the source
		clone.tsource = (ITupleSourceNode)tsource.clone();
		
		// Keep the evaluator
		
		// Shallow copy the relevant attributes
		if(relevants!=null)
			clone.relevants = (AttributeSet)((AttributeSet)relevants).clone();
	}
	
}
