package jadex.rules.rulesystem.rete.nodes;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jadex.rules.rulesystem.AbstractAgenda;
import jadex.rules.rulesystem.rete.Tuple;
import jadex.rules.rulesystem.rete.constraints.IConstraintEvaluator;
import jadex.rules.rulesystem.rete.extractors.AttributeSet;
import jadex.rules.state.IOAVState;
import jadex.rules.state.IProfiler;
import jadex.rules.state.OAVAttributeType;

/**
 *  The purpose of a collect node is to compress a number of tuples to a
 *  new tuple, which contains a multi slot.
 *  Example: Incoming tuples are:
 *  [a, b, d1]
 *  [a, b, d2]
 *  [a, c, d3]
 *  [a, c, d1]
 *  -> 
 *  [a, b, {d1, d2}]
 *  [a, c, {d3, d1}]
 */
public class CollectNode extends AbstractNode implements ITupleConsumerNode, ITupleSourceNode
{
	//-------- attributes --------
	
	/** The tuple consumers. */
	protected ITupleConsumerNode[] tconsumers;
	
	/** The tuple source. */
	protected ITupleSourceNode tsource;
	
	/** The constraint evaluator. */
	protected IConstraintEvaluator[] evaluators;
	
	/** The set of relevant attributes. */
	protected volatile AttributeSet relevants;
	
	/** The set of indirect attributes. */
	protected volatile AttributeSet indirects;
	
	/** The tuple index to collect. */
	protected int tupleindex;

	//-------- constructors --------
	
	/**
	 *  Create a new beta node.
	 */
	public CollectNode(int nodeid, int tupleindex, IConstraintEvaluator[] evaluators)
	{
		super(nodeid);
		this.tupleindex = tupleindex;
		this.evaluators	= evaluators;
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
	 *  Get the memory for this node.
	 *  @return The memory.
	 */
	public Collection getNodeMemory(ReteMemory mem)
	{
		return mem.hasNodeMemory(this)
			? ((CollectMemory)mem.getNodeMemory(this)).getResultMemory()
			: null;
	}
	
	/**
	 *  Get all tuple consumer nodes.
	 *  @return All tuple consumer nodes.
	 */
	public ITupleConsumerNode[] getTupleConsumers()
	{
		return tconsumers;
	}
	
	//-------- tuple consumer interface (left) --------
	
	/**
	 *  Send a tuple to this node.
	 *  @param tuple The tuple.
	 */
	public void addTuple(Tuple left, IOAVState state, ReteMemory mem, AbstractAgenda agenda)
	{
//		if(getNodeId()==531)
//			System.out.println("Add tuple called: "+this+" "+left);
		state.getProfiler().start(IProfiler.TYPE_NODE, this);
		state.getProfiler().start(IProfiler.TYPE_NODEEVENT, IProfiler.NODEEVENT_TUPLEADDED);

		Tuple indextuple = createIndexTuple(state, left, mem);
		CollectMemory nodemem = (CollectMemory)mem.getNodeMemory(this);
		
		Tuple resulttuple = nodemem.getWorkingTuple(indextuple);
		
		// Create new result tuple if none is present.
		if(resulttuple==null)
		{
			List obs = left.getObjects();
			for(int i=0; i<obs.size(); i++)
			{
				// Todo: should be i<tupleindex ???
				if(i!=tupleindex)
				{
					resulttuple = mem.getTuple(state, resulttuple, obs.get(i));
				}
				else
				{
					// todo: what kind of collection should be provided as result
					Set vals = state.isJavaIdentity() ? (Set)new MixedIdentityHashSet(state) : new HashSet();
					vals.add(obs.get(i));
					resulttuple = mem.getTuple(state, resulttuple, vals);
				}
			}
			nodemem.putWorkingTuple(indextuple, resulttuple);
		}
		// Add new value to existing result tuple.
		else
		{
			// Hack!!! Changing original tuple should be avoided,
			// as tuple is used as hashtable key.
			// Only because Tuple currently caches the hashcode, this works at all!
			Object newob = left.getObject(tupleindex);
			Set vals = (Set)resulttuple.getObject(tupleindex);
			if(vals.contains(newob))
				throw new UnsupportedOperationException("Multiple matches to same object not supported: "+newob);
			vals.add(newob);
		}
		
		if(checkConstraints(resulttuple, state))
		{
			//System.out.println("Object passed constraint check: "+this+" "+object);
			// If constraints passed and not in result -> add
			if(!nodemem.resultMemoryContains(resulttuple))
			{
				nodemem.addResultTuple(resulttuple);
				propagateAdditionToTupleConsumers(resulttuple, state, mem, agenda);
			}
			// If constraints passed and in result -> modify
			else
			{
				// todo: 
				propagateModificationToTupleConsumers(resulttuple, null, null, resulttuple, state, mem, agenda);
			}
		}
		else
		{
			// If constraints not passed and in result -> remove
			if(nodemem.resultMemoryContains(resulttuple))
			{
				nodemem.removeResultTuple(resulttuple);
				propagateRemovalToTupleConsumers(resulttuple, state, mem, agenda);
			}
			// If constraints not passed and not in result -> nop
		}
		
		state.getProfiler().stop(IProfiler.TYPE_NODEEVENT, IProfiler.NODEEVENT_TUPLEADDED);
		state.getProfiler().stop(IProfiler.TYPE_NODE, this);
		
//		System.out.println(nodemem);
	}

	/**
	 *  Remove a tuple from this node.
	 *  @param tuple The tuple.
	 */
	public void removeTuple(Tuple left, IOAVState state, ReteMemory mem, AbstractAgenda agenda)
	{
//		if(getNodeId()==531)
//			System.out.println("Remove tuple called: "+this+" "+left);
		state.getProfiler().start(IProfiler.TYPE_NODE, this);
		state.getProfiler().start(IProfiler.TYPE_NODEEVENT, IProfiler.NODEEVENT_TUPLEREMOVED);

		Tuple indextuple = createIndexTuple(state, left, mem);
		CollectMemory nodemem = (CollectMemory)mem.getNodeMemory(this);
		Tuple resulttuple = nodemem.getWorkingTuple(indextuple);
		
		assert resulttuple!=null: "No working tuple found: "+indextuple;
		
		Object val = left.getObject(tupleindex);
		Set vals = (Set)resulttuple.getObject(tupleindex);
		
		boolean removed = vals.remove(val);
		
		// Remove tuple when last element is removed from set. 
		if(vals.isEmpty())
		{
			nodemem.removeWorkingTuple(indextuple);
			if(nodemem.resultMemoryContains(resulttuple))
			{
				nodemem.removeResultTuple(resulttuple);
				propagateRemovalToTupleConsumers(resulttuple, state, mem, agenda);
			}
		}
		// Check constraints if at least one element.
		else
		{
			assert removed: "Value not found in result tuple: "+val;
			
			if(checkConstraints(resulttuple, state))
			{
				// If constraints passed and not in result -> add
				if(!nodemem.resultMemoryContains(resulttuple))
				{
					nodemem.addResultTuple(resulttuple);
					propagateAdditionToTupleConsumers(resulttuple, state, mem, agenda);
				}
				// If constraints passed and in result -> modify
				else
				{
					// todo: 
					propagateModificationToTupleConsumers(resulttuple, null, null, resulttuple, state, mem, agenda);
				}
			}
			else
			{
				// If constraints not passed and in result -> remove
				if(nodemem.resultMemoryContains(resulttuple))
				{
					nodemem.removeResultTuple(resulttuple);
					propagateRemovalToTupleConsumers(resulttuple, state, mem, agenda);
				}
				// If constraints not passed and not in result -> nop
			}
		}
		
		state.getProfiler().stop(IProfiler.TYPE_NODEEVENT, IProfiler.NODEEVENT_TUPLEREMOVED);
		state.getProfiler().stop(IProfiler.TYPE_NODE, this);
	
//		System.out.println(nodemem);
	}

	/**
	 *  Modify a tuple in this node.
	 *  @param left The tuple.
	 */
	public void modifyTuple(Tuple left, int tupleindex, OAVAttributeType type,
		Object oldvalue, Object newvalue, IOAVState state, ReteMemory mem, AbstractAgenda agenda)
	{
//		if(getNodeId()==531)
//			System.out.println("Modify tuple called: "+this+" "+left);

		if(!getRelevantAttributes().contains(type))
			return;

		// Problem: changed tuple could produce changed indextuple -> no identification of old value
		Tuple indextuple = createIndexTuple(state, left, mem);
		CollectMemory nodemem = (CollectMemory)mem.getNodeMemory(this);
		Tuple resulttuple = nodemem.getWorkingTuple(indextuple);
		assert resulttuple!=null: "No working tuple found: "+indextuple;

		// Check if modification changes node memory.
		boolean affected = isAffected(type);
	
		if(affected)
		{
			boolean	contains = nodemem.resultMemoryContains(resulttuple);
			boolean check = checkConstraints(resulttuple, state);
			
			// Object no longer valid -> remove
			if(contains && !check)
			{
				nodemem.removeResultTuple(resulttuple);
				propagateRemovalToTupleConsumers(resulttuple, state, mem, agenda);
			}
	
			// Tuple newly valid -> add
			else if(!contains && check)
			{
				nodemem.addResultTuple(resulttuple);
				propagateAdditionToTupleConsumers(resulttuple, state, mem, agenda);
			}
			
			else if(contains)
			{
				propagateModificationToTupleConsumers(resulttuple, type, oldvalue, newvalue, 
					state, mem, agenda);
			}
		}
		else
		{
			// Tuple changed in memory -> propagate modification
			boolean	contains = nodemem.resultMemoryContains(resulttuple);
			if(contains)
			{
				propagateModificationToTupleConsumers(resulttuple, type, oldvalue, newvalue, 
					state, mem, agenda);
			}
		}
		
		state.getProfiler().start(IProfiler.TYPE_NODE, this);
		state.getProfiler().start(IProfiler.TYPE_NODEEVENT, IProfiler.NODEEVENT_TUPLEMODIFIED);

		state.getProfiler().stop(IProfiler.TYPE_NODEEVENT, IProfiler.NODEEVENT_TUPLEMODIFIED);
		state.getProfiler().stop(IProfiler.TYPE_NODE, this);
	
//		System.out.println(nodemem);
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
	
	//-------- methods --------
	
	/**
	 *  Create the node memory.
	 *  @param state	The state.
	 *  @return The node memory.
	 */
	public Object createNodeMemory(IOAVState state)
	{
		return new CollectMemory();
	}
	
	/**
	 *  Get the evaluators.
	 *  @return The evaluators.
	 */
	public IConstraintEvaluator[] getConstraintEvaluators()
	{
		return evaluators;
	}
	
	//-------- methods --------
	
	/**
	 *  Test if the node is affected from a modification.
	 *  @param type The attribute type.
	 *  @return True, if possibly affected.
	 */
	public boolean isAffected(OAVAttributeType attr)
	{
		boolean ret = false;
		for(int i=0; !ret && evaluators!=null && i<evaluators.length; i++)
			ret = evaluators[i].isAffected(-1, attr);
		return ret;
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
					for(int i=0; evaluators!=null && i<evaluators.length; i++)
					{
						relevants.addAll(evaluators[i].getRelevantAttributes());
					}
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
					for(int i=0; evaluators!=null && i<evaluators.length; i++)
					{
						indirects.addAll(evaluators[i].getIndirectAttributes());
					}
					this.indirects	= indirects; 
				}
			}
		}
		return indirects;
	}

	/**
	 *  Get the tuple index.
	 *  @return The tuple index.
	 */
	public int getTupleIndex()
	{
		return tupleindex;
	}
	
//	/**
//	 *  Get the string representation.
//	 *  @return The string representation. 
//	 */
//	public String toString()
//	{
//		return toString(", indexers="+Srules.arrayToString(indexers)
//			+ ", evaluators="+Srules.arrayToString(evaluators)
//			+ ", relevants="+getRelevantAttributes());
//	}
		
	//-------- cloneable --------
	
	/**
	 *  Do clone makes a deep clone without regarding cycles.
	 *  Method is overridden by subclasses to actually incorporate their attributes.
	 *  @param theclone The clone.
	 */
	protected void doClone(Object theclone)
	{	
		CollectNode clone = (CollectNode)theclone;
		
		// Deep clone tuple consumers
		clone.tconsumers = new ITupleConsumerNode[tconsumers.length];
		for(int i=0; i<tconsumers.length; i++)
			clone.tconsumers[i] = (ITupleConsumerNode)tconsumers[i].clone();
		
		// Set the tuple source
		clone.tsource = (ITupleSourceNode)tsource.clone();
		
		// Shallow clone evaluators consumers
		if(evaluators!=null)
		{
			clone.evaluators = new IConstraintEvaluator[evaluators.length];
			System.arraycopy(evaluators, 0, clone.evaluators, 0, evaluators.length);
		}
		
		// Shallow copy the relevant attributes
		if(relevants!=null)
			clone.relevants = (AttributeSet)((AttributeSet)relevants).clone();
	}
	
	//-------- helpers --------
	
	/**
	 *  Check the constraints with respect 
	 *  to the object.
	 *  @return True, if object fits constraints.
	 */
	protected boolean checkConstraints(Tuple left, IOAVState state)
	{
		boolean pass = true;
		for(int i=0; pass && evaluators!=null && i<evaluators.length; i++)
			pass = evaluators[i].evaluate(null, left, state);
		return pass;
	}
	
	/**
	 *  Create an index tuple from a tuple.
	 *  The index tuple excludes the index position at
	 *  which the compression happens (and all elements
	 *  thereafter -> why, could be the last node?).
	 *  @param tuple The tuple.
	 *  @return The index tuple. 
	 */
	protected Tuple createIndexTuple(IOAVState state, Tuple tuple, ReteMemory mem)
	{
		List obs = tuple.getObjects();
		Tuple t = null;
		for(int i=0; i<obs.size(); i++)
		{
			if(i<tupleindex)
				t = mem.getTuple(state, t, obs.get(i)); // Create tuple from tuples
		}
		return t;
	}
	
	/**
	 *  Propagate a new tuple to all tuple consumers.
	 *  @param object The new object.
	 */
	protected void propagateAdditionToTupleConsumers(Tuple tuple, IOAVState state, ReteMemory mem, AbstractAgenda agenda)
	{
		ITupleConsumerNode[]	tcon	= tconsumers;
		for(int i=0; tcon!=null && i<tcon.length; i++)
			tcon[i].addTuple(tuple, state, mem, agenda);
	}
	
	/**
	 *  Propagate a removed tuple to all tuple consumers.
	 *  @param object The new object.
	 */
	protected void propagateRemovalToTupleConsumers(Tuple tuple, IOAVState state, ReteMemory mem, AbstractAgenda agenda)
	{
		ITupleConsumerNode[]	tcon	= tconsumers;
		for(int i=0; tcon!=null && i<tcon.length; i++)
			tcon[i].removeTuple(tuple, state, mem, agenda);
	}
	
	/**
	 *  Propagate a modified object to all object consumers.
	 *  @param object The new object.
	 */
	protected void propagateModificationToTupleConsumers(Tuple tuple, OAVAttributeType type, Object oldvalue, 
		Object newvalue, IOAVState state, ReteMemory mem, AbstractAgenda agenda)
	{
		ITupleConsumerNode[]	tcon	= tconsumers;
		for(int i=0; tcon!=null && i<tcon.length; i++)
			tcon[i].modifyTuple(tuple, tupleindex, type, oldvalue, newvalue, state, mem, agenda);
	}
	
}
