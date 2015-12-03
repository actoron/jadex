package jadex.rules.rulesystem.rete.nodes;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import jadex.rules.rulesystem.AbstractAgenda;
import jadex.rules.rulesystem.rete.Tuple;
import jadex.rules.rulesystem.rete.constraints.ConstraintIndexer;
import jadex.rules.rulesystem.rete.constraints.IConstraintEvaluator;
import jadex.rules.rulesystem.rete.extractors.AttributeSet;
import jadex.rules.state.IOAVState;
import jadex.rules.state.IProfiler;
import jadex.rules.state.OAVAttributeType;

/**
 *  A beta node has the purpose to perform a constraints check
 *  between (at least) two objects. The beta node has to incoming
 *  coming connections which both can activate the node.
 */
public abstract class AbstractBetaNode extends AbstractNode implements IObjectConsumerNode,
	ITupleConsumerNode, ITupleSourceNode
{
	//-------- attributes --------
	
	/** The tuple consumers. */
	protected ITupleConsumerNode[] tconsumers;
	
	/** The object source. */
	protected IObjectSourceNode osource;
	
	/** The tuple source. */
	protected ITupleSourceNode tsource;
	
	/** The constraint evaluator. */
	protected IConstraintEvaluator[] evaluators;
	
	/** The indexed constraint indexers. */
	protected ConstraintIndexer[] indexers;

	/** The set of relevant attributes. */
	protected volatile AttributeSet relevants;

	/** The set of indirect attributes. */
	protected volatile AttributeSet indirects;

	//-------- constructors --------
	
	/**
	 *  Create a new beta node.
	 */
	public AbstractBetaNode(int nodeid, IConstraintEvaluator[] evaluators, ConstraintIndexer[] xevaluators)
	{
		super(nodeid);
		this.evaluators	= evaluators;
		this.indexers = xevaluators;
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
			? ((BetaMemory)mem.getNodeMemory(this)).getResultMemory()
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
//		if(getNodeId()==530)
//			System.out.println(this+".addTuple: "+left);
		
//		System.out.println("Add tuple called: "+this+" "+left);
		state.getProfiler().start(IProfiler.TYPE_NODE, this);
		state.getProfiler().start(IProfiler.TYPE_NODEEVENT, IProfiler.NODEEVENT_TUPLEADDED);

		// Update the indexed memories
		if(indexers!=null)
		{
			BetaMemory bmem = (BetaMemory)mem.getNodeMemory(this);
			for(int i=0; i<indexers.length; i++)
			{
				indexers[i].addTuple(left, state, bmem);
			}
		}

		// Get object memory from the indexed constraints
		Collection omem = fetchObjectMemory(state, left, mem);
		if(omem!=null)
		{
			// Evaluate non-indexed constraints for all found objects
			for(Iterator it=omem.iterator(); it.hasNext(); )
			{
				Object right = it.next();
				if(checkNonindexedConstraints(left, right, state))
				{
					addMatch(left, right, state, mem, agenda);
				}
			}
		}

		state.getProfiler().stop(IProfiler.TYPE_NODEEVENT, IProfiler.NODEEVENT_TUPLEADDED);
		state.getProfiler().stop(IProfiler.TYPE_NODE, this);
	}

	/**
	 *  Remove a tuple from this node.
	 *  @param tuple The tuple.
	 */
	public void removeTuple(Tuple left, IOAVState state, ReteMemory mem, AbstractAgenda agenda)
	{
//		if(getNodeId()==3713)
//			System.out.println("Remove tuple called: "+this+" "+left);
		state.getProfiler().start(IProfiler.TYPE_NODE, this);
		state.getProfiler().start(IProfiler.TYPE_NODEEVENT, IProfiler.NODEEVENT_TUPLEREMOVED);

		Collection omem = fetchObjectMemory(state, left, mem);
		if(omem!=null)
		{
//			if(getNodeId()==3713)
//			{
//				System.out.println("omem: "+this+" "+omem);
//				System.out.println("resultmem"+this+" "+mem.getNodeMemory(this));
//			}
			// Simply tries to remove all found entries
			for(Iterator it=omem.iterator(); it.hasNext(); )
			{
				removeMatch(left, it.next(), state, mem, agenda);
			}
		}

		// Remove tuple from indexed memories
		if(indexers!=null)
		{
			BetaMemory bmem = (BetaMemory)mem.getNodeMemory(this);
			for(int i=0; i<indexers.length; i++)
			{
				indexers[i].removeTuple(left, bmem);
			}
		}
		
		// Test if remove really removes all occurrences
		boolean	asserts_enabled	= false;
		assert asserts_enabled=true;
		if(asserts_enabled)
		{
			BetaMemory bmem = (BetaMemory)mem.getNodeMemory(this);
			for(Iterator it=bmem.getResultMemory().iterator(); it.hasNext(); )
			{
				Tuple next = (Tuple)it.next();
				if(next.getLastTuple().equals(left))
					System.out.println("error: "+this+" "+next);
				assert !next.getLastTuple().equals(left) : "Tuple not removed: "+left+", "+this;
			}
		}

//		if(getNodeId()==3713)
//			System.out.println("FINISHED Remove tuple called: "+this+" "+left);
		state.getProfiler().stop(IProfiler.TYPE_NODEEVENT, IProfiler.NODEEVENT_TUPLEREMOVED);
		state.getProfiler().stop(IProfiler.TYPE_NODE, this);
	}

	/**
	 *  Modify a tuple in this node.
	 *  @param left The tuple.
	 */
	public void modifyTuple(Tuple left, int tupleindex, OAVAttributeType type,
		Object oldvalue, Object newvalue, IOAVState state, ReteMemory mem, AbstractAgenda agenda)
	{
		if(!getRelevantAttributes().contains(type))
			return;

		state.getProfiler().start(IProfiler.TYPE_NODE, this);
		state.getProfiler().start(IProfiler.TYPE_NODEEVENT, IProfiler.NODEEVENT_TUPLEMODIFIED);

		if(isAffected(type))
		{
			// Update tuple in indexed left memories
			Collection oldmem	= null;
			boolean	changed	= false;
			if(indexers!=null)
			{
				oldmem = fetchObjectMemory(state, left, mem);
				for(int i=0; i<indexers.length; i++)
				{
					if(indexers[i].isLeftIndex(tupleindex, type))
					{
						// Update index by removing (under old value) and readding tuple (under new value).
						BetaMemory bmem = (BetaMemory)mem.getNodeMemory(this);
						indexers[i].removeTuple(left, bmem);
						indexers[i].addTuple(left, state, bmem);
						changed	= true;
					}
				}
			}
			
			// Handle objects from current (new) object memory
			Collection newmem = indexers==null || changed ? fetchObjectMemory(state, left, mem) : oldmem;
			if(newmem!=null)
			{
				for(Iterator it=newmem.iterator(); it.hasNext(); )
				{
					// Action depends on if tuple is now valid (=check) and was previously valid (=contains)
					Object	right	= it.next();
					boolean contains = isMatchContained(state, left, right, mem);
					boolean	check	= checkNonindexedConstraints(left, right, state);
	
					// Tuple newly valid -> add match
					if(!contains && check)
						addMatch(left, right, state, mem, agenda);

					// Tuple no longer valid -> remove match
					else if(contains && !check)
						removeMatch(left, right, state, mem, agenda);
				
					// Tuple changed in memory -> propagate modification
					else if(contains)
						propagateModification(left, right, tupleindex, type, 
							oldvalue, newvalue, state, mem, agenda);
				}
			}
			
			// Handle objects no longer in object memory (only when indexers are present,
			// otherwise complete object memory is already handled before). 
			if(indexers!=null)
			{
				if(changed && oldmem!=null)
				{
					for(Iterator it=oldmem.iterator(); it.hasNext(); )
					{
						Object right = it.next();
						boolean contains = isMatchContained(state, left, right, mem);
		
						// Remove matches, when object in old memory but not in new memory.
						if(contains && (newmem==null || !newmem.contains(right)))
							removeMatch(left, right, state, mem, agenda);
					}
				}
			}
		}
		
		state.getProfiler().stop(IProfiler.TYPE_NODEEVENT, IProfiler.NODEEVENT_TUPLEMODIFIED);
		state.getProfiler().stop(IProfiler.TYPE_NODE, this);
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
	
	//-------- object consumer interface (right) --------
	
	/**
	 *  Send an object to this node.
	 *  @param object The object.
	 */
	public void addObject(Object right, IOAVState state, ReteMemory mem, AbstractAgenda agenda)
	{
//		if(getNodeId()==530)
//			System.out.println(this+".addObject: "+right);

//		System.out.println("Add object called: "+this+" "+right);
		state.getProfiler().start(IProfiler.TYPE_NODE, this);
		state.getProfiler().start(IProfiler.TYPE_NODEEVENT, IProfiler.NODEEVENT_OBJECTADDED);
		
		// Update the indexed memories
		if(indexers!=null)
		{
			BetaMemory bmem = (BetaMemory)mem.getNodeMemory(this);
			for(int i=0; i<indexers.length; i++)
			{
				indexers[i].addObject(right, state, bmem);
			}
		}

		// Evaluate the indexed constraints
		Collection tmem = fetchTupleMemory(state, right, mem);
		if(tmem!=null)
		{
			// Evaluate non-indexed constraints for all found tuples
			for(Iterator it=tmem.iterator(); it.hasNext(); )
			{
				Tuple left = (Tuple)it.next();
				if(checkNonindexedConstraints(left, right, state))
				{
					addMatch(left, right, state, mem, agenda);
				}
			}
		}
				
//		if(this instanceof NotNode)
//			checkConsistency(mem);
		state.getProfiler().stop(IProfiler.TYPE_NODEEVENT, IProfiler.NODEEVENT_OBJECTADDED);
		state.getProfiler().stop(IProfiler.TYPE_NODE, this);
	}
	
	/**
	 *  Remove an object from this node.
	 *  @param object The object.
	 */
	public void removeObject(Object right, IOAVState state, ReteMemory mem, AbstractAgenda agenda)
	{
//		if(getNodeId()==1137)
//			System.out.println(this+".removeObject: "+right);
		
//		if(this.getNodeId()==161)
//			mem.debug++;
//		
//		if(mem.debug>0)
//		if(this instanceof NotNode)
//		{
//			if(mem.debug==null)
//			{
//				mem.debug	= new ArrayList();
//			}
//			mem.debug.add("+++Remove object called: "+this+" "+right+", "+mem.getNodeMemory(this));
//		}
	
		state.getProfiler().start(IProfiler.TYPE_NODE, this);
		state.getProfiler().start(IProfiler.TYPE_NODEEVENT, IProfiler.NODEEVENT_OBJECTREMOVED);
		
		Collection tmem = fetchTupleMemory(state, right, mem);
		if(tmem!=null)
		{
			// Simply tries to remove all found entries
			for(Iterator it=tmem.iterator(); it.hasNext(); )
			{
				removeMatch((Tuple)it.next(), right, state, mem, agenda);
			}
		}

		// Remove object from indexed memories
		if(indexers!=null)
		{
			BetaMemory bmem = (BetaMemory)mem.getNodeMemory(this);
			for(int i=0; i<indexers.length; i++)
			{
//				if(mem.debug!=null)
//					mem.debug.add("removeObject(): indexer "+this+" "+indexers[i]);
				indexers[i].removeObject(right, bmem);
			}			
		}
	
//		if(this instanceof NotNode)
//		{
//			checkConsistency(mem);
////			mem.debug	= null;
//		}
		
		state.getProfiler().stop(IProfiler.TYPE_NODEEVENT, IProfiler.NODEEVENT_OBJECTREMOVED);
		state.getProfiler().stop(IProfiler.TYPE_NODE, this);
	}
	
	/**
	 *  Propagate an object change to this node.
	 *  @param right The new object.
	 */
	public void modifyObject(Object right, OAVAttributeType type, Object oldvalue, Object newvalue, IOAVState state, ReteMemory mem, AbstractAgenda agenda)
	{
		if(!getRelevantAttributes().contains(type))
			return;

//		if(getNodeId()==1137)
//			System.out.println(this+".modifyObject: "+right);

		state.getProfiler().start(IProfiler.TYPE_NODE, this);
		state.getProfiler().start(IProfiler.TYPE_NODEEVENT, IProfiler.NODEEVENT_OBJECTMODIFIED);

		if(isAffected(type))
		{
			// Update indexers
			Collection oldmem	= null;
			boolean	changed	= false;
			if(indexers!=null)
			{
				oldmem = fetchTupleMemory(state, right, mem);
				for(int i=0; i<indexers.length; i++)
				{
					if(indexers[i].isRightIndex(type))
					{
						// Update index by removing (under old value) and readding object (under new value).
						BetaMemory bmem = (BetaMemory)mem.getNodeMemory(this);
						indexers[i].removeObject(right, bmem);
						indexers[i].addObject(right, state, bmem);
						changed	= true;
					}
				}
			}
			
			// Handle tuples from current (new) tuple memory
			Collection newmem = indexers==null || changed ? fetchTupleMemory(state, right, mem) : oldmem;
			if(newmem!=null)
			{
				for(Iterator it=newmem.iterator(); it.hasNext(); )
				{
					// Action depends on if tuple is now valid (=check) and was previously valid (=contains)
					Tuple	left	= (Tuple)it.next();
					boolean contains = isMatchContained(state, left, right, mem);
					boolean	check	= checkNonindexedConstraints(left, right, state);
	
					// Tuple newly valid -> add match
					if(!contains && check)
					{
//						if(getNodeId()==530)
//							System.out.println("add: "+left+" "+right);
						addMatch(left, right, state, mem, agenda);
					}
					
					// Tuple no longer valid -> remove match
					else if(contains && !check)
						removeMatch(left, right, state, mem, agenda);
				
					// Tuple changed in memory -> propagate modification
					else if(contains)
						propagateModification(left, right, left.size(), type, oldvalue, newvalue, state, mem, agenda);
				}
			}

			// Handle tuples no longer in tuple memory (only when indexers are present,
			// otherwise complete tuple memory is already handled before). 
			if(indexers!=null)
			{
				if(changed && oldmem!=null)
				{
					for(Iterator it=oldmem.iterator(); it.hasNext(); )
					{
						Tuple left = (Tuple)it.next();
						boolean contains = isMatchContained(state, left, right, mem);
		
						// Remove matches, when object in old memory but not in new memory.
						if(contains && (newmem==null || !newmem.contains(left)))
							removeMatch(left, right, state, mem, agenda);
					}
				}
			}			
		}
		
		// else
		// propagation of unaffected nodes is handled by subtypes.

//		if(this instanceof NotNode)
//			checkConsistency(mem);
		state.getProfiler().stop(IProfiler.TYPE_NODEEVENT, IProfiler.NODEEVENT_OBJECTMODIFIED);
		state.getProfiler().stop(IProfiler.TYPE_NODE, this);
	}

	/**
	 *  Propagate an indirect object change to this node.
	 *  @param id The changed object.
	 */
	public void modifyIndirectObject(Object object, OAVAttributeType type, Object oldvalue, Object newvalue, IOAVState state, ReteMemory mem, AbstractAgenda agenda)
	{
		Collection	linput	= getTupleSource().getNodeMemory(mem);
		if(linput!=null)
		{
			// Todo: Use index for avoiding the need for checking all tuple/object pairs.
			for(Iterator it=linput.iterator(); it.hasNext(); )
			{
				Tuple	left	= (Tuple)it.next();
				// Get indexed objects for tuple. (Todo: Indices need to be rebuilt!?)
				Collection omem = fetchObjectMemory(state, left, mem);
				if(omem!=null)
				{
					for(Iterator it2=omem.iterator(); it2.hasNext(); )
					{
						Object	right	= it2.next();
						boolean	contains	= isMatchContained(state, left, right, mem);
						boolean check = checkNonindexedConstraints(left, right, state);
		
						// Object no longer valid -> remove
						if(contains && !check)
						{
							removeMatch(left, right, state, mem, agenda);
						}
		
						// Object newly valid -> add
						else if(!contains && check)
						{
							addMatch(left, right, state, mem, agenda);
						}
					}
				}
			}
		}
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
	
	//-------- methods --------
	
	/**
	 *  Create the node memory.
	 *  @param state	The state.
	 *  @return The node memory.
	 */
	public Object createNodeMemory(IOAVState state)
	{
		return new BetaMemory(state);
	}
	
	/**
	 *  Get the indexers.
	 *  @return The indexers.
	 */
	public ConstraintIndexer[] getConstraintIndexers()
	{
		return indexers;
	}
	
	/**
	 *  Get the evaluators.
	 *  @return The evaluators.
	 */
	public IConstraintEvaluator[] getConstraintEvaluators()
	{
		return evaluators;
	}
	
	//-------- helpers --------
	
	/**
	 *  Compute the intersection of two collections.
	 *  @param c1 The first collection.
	 *  @param c2 The second collection.
	 *  @return The intersection.
	 */
	protected Collection intersection(IOAVState state, Collection c1, Collection c2)
	{
		if(c1==null || c2==null)
			return null;
		
		Collection ret = new LinkedHashSet();
		for(Iterator it=c1.iterator(); it.hasNext(); )
		{
			Object o1 = it.next();
			if(c2.contains(o1))
				ret.add(o1);
		}
		return ret;
	}	
	/**
	 *  Compute the intersection of two collections.
	 *  @param c1 The first collection.
	 *  @param c2 The second collection.
	 *  @return The intersection.
	 */
	protected Collection identityIntersection(IOAVState state, Collection c1, Collection c2)
	{
		if(c1==null || c2==null)
			return null;
		
		Collection ret = state.isJavaIdentity() ? (Set)new MixedIdentityHashSet(state) : new LinkedHashSet();
		for(Iterator it=c1.iterator(); it.hasNext(); )
		{
			Object o1 = it.next();
			if(c2.contains(o1))
				ret.add(o1);
		}
		return ret;
	}
	
	/**
	 *  Fetch the tuple memory for a given object.
	 *  @param right The right object.
	 *  @param mem The rete memory.
	 *  @param state The state.
	 *  @return The tuple memory matching that object (or complete).
	 */
	protected Collection fetchTupleMemory(IOAVState state, Object right, ReteMemory mem)
	{
		// Evaluate the indexed constraints
		Collection ret = null;
		if(indexers!=null)
		{
			if(mem.hasNodeMemory(this))
			{
				BetaMemory bmem = (BetaMemory)mem.getNodeMemory(this);	
				ret = indexers[0].findTuples(right, bmem);
	
				for(int i=1; ret!=null && i<indexers.length; i++)
				{
					Collection cres	= indexers[i].findTuples(right, bmem);
					ret = intersection(state, ret, cres);
				}
			}
//			if(mem.debug!=null)
//				mem.debug.add("fetchTupleMemory (indexers): "+this+", "+ret);
		}
		else
		{
			// Fetch the tuple memory
			ret = getTupleSource().getNodeMemory(mem);
//			if(mem.debug!=null)
//				mem.debug.add("fetchTupleMemory (no indexers): "+this+", "+ret);
		}
		
		return ret;
	}
	
	/**
	 *  Fetch the object memory for a given object.
	 *  @param left The left tuple.
	 * @param value	The hash value (if any). 
	 * @param type The attribute type (if any).
	 * @param tupleindex The tuple index of the changed object (if any).
	 *  @param mem The rete memory.
	 *  @param state The state.
	 *  @return The tuple memory matching that object (or complete).
	 */
	protected Collection fetchObjectMemory(IOAVState state, Tuple left, ReteMemory mem)
	{
		// Evaluate the indexed constraints
		Collection ret	= null;
		if(indexers!=null)
		{
			if(mem.hasNodeMemory(this))
			{
				BetaMemory bmem = (BetaMemory)mem.getNodeMemory(this);
				ret = indexers[0].findObjects(left, bmem);

				for(int i=1; ret!=null && i<indexers.length; i++)
				{
					Collection cres	= indexers[i].findObjects(left, bmem);

					ret = identityIntersection(state, ret, cres);
				}
			}
		}
		else
		{
			// Fetch the object memory
			ret = getObjectSource().getNodeMemory(mem);
		}
		
		return ret;
	}
	
	/**
	 *  Check the non-indexed constraints for the given left/right values.
	 */
	protected boolean checkNonindexedConstraints(Tuple left, Object right, IOAVState state)
	{
		boolean	pass	= true;
		for(int i=0; pass && evaluators!=null && i<evaluators.length; i++)
			pass = evaluators[i].evaluate(right, left, state);
		return pass;
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
		for(int i=0; !ret && indexers!=null && i<indexers.length; i++)
			ret = indexers[i].isAffected(-1, attr);
		for(int i=0; !ret && evaluators!=null && i<evaluators.length; i++)
			ret = evaluators[i].isAffected(-1, attr);
		return ret;
	}
	
	/**
	 *  Get the set of relevant attribute types.
	 */
	public AttributeSet	getRelevantAttributes()
	{
		if(relevants==null)
		{
			synchronized(this)
			{
				if(relevants==null)
				{
					AttributeSet	relevants	= new AttributeSet();
					for(int i=0; indexers!=null && i<indexers.length; i++)
					{
						relevants.addAll(indexers[i].getRelevantAttributes());
					}
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
					for(int i=0; indexers!=null && i<indexers.length; i++)
					{
						indirects.addAll(indexers[i].getIndirectAttributes());
					}
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
		AbstractBetaNode clone = (AbstractBetaNode)theclone;
		
		// Deep clone tuple consumers
		clone.tconsumers = new ITupleConsumerNode[tconsumers.length];
		for(int i=0; i<tconsumers.length; i++)
			clone.tconsumers[i] = (ITupleConsumerNode)tconsumers[i].clone();
		
		// Set the object source
		clone.osource = (IObjectSourceNode)osource.clone();
	
		// Set the tuple source
		clone.tsource = (ITupleSourceNode)tsource.clone();
		
		// Shallow clone evaluators consumers
		if(evaluators!=null)
		{
			clone.evaluators = new IConstraintEvaluator[evaluators.length];
			System.arraycopy(evaluators, 0, clone.evaluators, 0, evaluators.length);
		}
		
		// Shallow clone indexers
		if(indexers!=null)
		{
			clone.indexers = new ConstraintIndexer[indexers.length];
			System.arraycopy(indexers, 0, clone.indexers, 0, indexers.length);
		}
		
		// Shallow copy the relevant attributes
		if(relevants!=null)
			clone.relevants = (AttributeSet)((AttributeSet)relevants).clone();
	}
	
	//-------- template methods --------

	/**
	 *  Add a match to the node memory and propagate if necessary.
	 */
	protected abstract void addMatch(Tuple left, Object right, IOAVState state, ReteMemory mem, AbstractAgenda agenda);

	/**
	 *  Remove a match from the node memory and propagate if necessary.
	 */
	protected abstract void removeMatch(Tuple left, Object right, IOAVState state, ReteMemory mem, AbstractAgenda agenda);

	/**
	 *  Propagate a change of a tuple in the result memory.
	 */
	protected abstract void propagateModification(Tuple left, Object right, int tupleindex, OAVAttributeType type, 
		Object oldvalue, Object newvalue, IOAVState state, ReteMemory mem, AbstractAgenda agenda);

	/**
	 *  Check if a match is contained.
	 */
	protected abstract boolean isMatchContained(IOAVState state, Tuple left, Object right, ReteMemory mem);
	
	
//	/**
//	 *  Test if there is left input and no right input, but no value propagated
//	 */
//	protected void	checkConsistency(ReteMemory mem)
//	{
//		Collection	left	= tsource.getNodeMemory(mem);
//		if(left!=null && !left.isEmpty())
//		{
//			Collection right	= osource.getNodeMemory(mem);
//			if(right==null || right.isEmpty())
//			{
//				Collection	own	= getNodeMemory(mem);
//				if(own==null || own.isEmpty())
//				{
//					Thread.dumpStack();
//					System.exit(0);
//				}
//			}
//		}
//	}

}
